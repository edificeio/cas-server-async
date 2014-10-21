package fr.wseduc.cas.endpoint;

import edu.yale.tp.cas.*;
import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.async.Tuple;
import fr.wseduc.cas.data.DataHandler;
import fr.wseduc.cas.data.DataHandlerFactory;
import fr.wseduc.cas.entities.*;
import fr.wseduc.cas.http.ClientResponse;
import fr.wseduc.cas.http.HttpClient;
import fr.wseduc.cas.http.HttpClientFactory;
import fr.wseduc.cas.http.Request;
import fr.wseduc.cas.exceptions.ErrorCodes;
import fr.wseduc.cas.exceptions.Try;
import fr.wseduc.cas.exceptions.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Validator {

	private DataHandlerFactory dataHandlerFactory;
	private HttpClientFactory httpClientFactory;
	private static final Logger log = Logger.getLogger("Validator");

	public void serviceValidate(final Request request) {
		final String service = request.getParameter("service");
		final String ticket = request.getParameter("ticket");
		final boolean renew = Boolean.getBoolean(request.getParameter("renew"));
		final String pgtUrl = request.getParameter("pgtUrl");
		if (service != null && ticket != null && !service.trim().isEmpty() && !ticket.trim().isEmpty()) {
			if (ticket.startsWith("ST-")) {
				final DataHandler dataHandler = dataHandlerFactory.create(request);
				dataHandler.validateTicket(ticket, service,
						new Handler<Try<ValidationException, Tuple<AuthCas, User>>>() {
					@Override
					public void handle(Try<ValidationException, Tuple<AuthCas, User>> v) {
						try {
							final Tuple<AuthCas, User> t = v.get();
							final User user = t._2;
							final ServiceTicket st = t._1.getServiceTicket(ticket);
							if (st == null) {
								error(request, ErrorCodes.INVALID_TICKET);
								return;
							}
							if (pgtUrl != null && !pgtUrl.trim().isEmpty()) {
								validateProxy(pgtUrl, st,
										new Handler<Try<ValidationException, ProxyGrantingTicket>>() {
									@Override
									public void handle(
											Try<ValidationException, ProxyGrantingTicket> event) {
										try {
											final ProxyGrantingTicket pgt = event.get();
											st.setPgt(pgt);
											dataHandler.persistAuth(t._1, new Handler<Boolean>() {
												@Override
												public void handle(Boolean saved) {
													if (saved) {
														success(request, user, pgt.getPgtIOU());
													} else {
														error(request, ErrorCodes.INTERNAL_ERROR);
													}
												}
											});
										} catch (ValidationException e) {
											error(request, e.getError());
										}
									}
								});
							} else {
								dataHandler.persistAuth(t._1, new Handler<Boolean>() {
									@Override
									public void handle(Boolean saved) {
										if (saved) {
											success(request, user);
										} else {
											error(request, ErrorCodes.INTERNAL_ERROR);
										}
									}
								});
							}
						} catch (ValidationException e) {
							error(request, e.getError());
						}
					}
				});
			} else {
				error(request, ErrorCodes.UNAUTHORIZED_SERVICE_PROXY);
			}
		} else {
			error(request, ErrorCodes.INVALID_REQUEST);
		}
	}

	private void validateProxy(String pgtUrl, final ServiceTicket st,
			final Handler<Try<ValidationException, ProxyGrantingTicket>> handler) {
		try {
			URI uri = new URI(pgtUrl);
			if (!"https".equals(uri.getScheme())) {
				handler.handle(new Try<ValidationException, ProxyGrantingTicket>(
						new ValidationException(ErrorCodes.INVALID_PROXY_CALLBACK)));
				return;
			}
			int port = uri.getPort() > 0 ? uri.getPort() : 443;
			HttpClient httpClient = httpClientFactory.create(uri.getHost(), port, true);
			final ProxyGrantingTicket pgt = (st != null && st.getPgt() != null) ?
					st.getPgt() : new ProxyGrantingTicket();
			pgt.addUrl(pgtUrl);
			String proxyUri = pgtUrl.replaceFirst(
					"^(?:([^:/?#]+):)?(?://((?:(([^:@]*):?([^:@]*))?@)?([^:/?#]*)(?::(\\\\d*))?))?", "");
			proxyUri += proxyUri.contains("?") ? "&" : "?";
			httpClient.get(proxyUri + "pgtId=" + pgt.getPgtId() + "&pgtIou=" + pgt.getPgtIOU(),
					new Handler<ClientResponse>() {
						@Override
						public void handle(ClientResponse resp) {
							if (resp.getStatusCode() == 200) {
								handler.handle(new Try<ValidationException, ProxyGrantingTicket>(pgt));
							} else {
								handler.handle(new Try<ValidationException, ProxyGrantingTicket>(
										new ValidationException(ErrorCodes.INVALID_PROXY_CALLBACK)
								));
							}
						}
					});
		} catch (URISyntaxException e) {
			handler.handle(new Try<ValidationException, ProxyGrantingTicket>(
					new ValidationException(ErrorCodes.INVALID_PROXY_CALLBACK)));
		}
	}

	private void success(Request request, User user) {
		success(request, user, null);
	}

	private void success(Request request, User user, String pgtiou) {
		success(request, user, pgtiou, null);
	}

	private void success(Request request, User user, String pgtiou, String[] proxyUrls) {
		AuthenticationSuccessType authenticationSuccessType = new AuthenticationSuccessType();
		authenticationSuccessType.setUser(user.getUser());
		authenticationSuccessType.setAttributes(new AttributesType());
		authenticationSuccessType.getAttributes().setUserAttributes(new AttributesType.UserAttributes());
		if (pgtiou != null && !pgtiou.trim().isEmpty()) {
			authenticationSuccessType.setProxyGrantingTicket(pgtiou);
		}
		if (proxyUrls != null && proxyUrls.length > 0 ) {
			ProxiesType proxiesType = new ProxiesType();
			List<String> proxies = proxiesType.getProxy();
			Collections.addAll(proxies, proxyUrls);
			authenticationSuccessType.setProxies(proxiesType);
		}
		List<Object> l = authenticationSuccessType.getAttributes().getUserAttributes().getAny();
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			for (Map.Entry<String, String> e : user.getAttributes().entrySet()) {
				Element element = doc.createElement(e.getKey());
				element.setTextContent(e.getValue());
				l.add(element);
			}
			ServiceResponseType serviceResponseType = new ServiceResponseType();
			serviceResponseType.setAuthenticationSuccess(authenticationSuccessType);
			sendResponse(request, serviceResponseType);
		} catch (ParserConfigurationException e) {
			log.severe(e.toString());
			request.getResponse().setStatusCode(500);
			request.getResponse().setBody(e.getMessage());
		}
	}

	private void error(Request request, ErrorCodes invalidRequest) {
		AuthenticationFailureType authenticationFailureType = new AuthenticationFailureType();
		authenticationFailureType.setCode(invalidRequest.name());
		authenticationFailureType.setValue(invalidRequest.getMessage());
		ServiceResponseType serviceResponseType = new ServiceResponseType();
		serviceResponseType.setAuthenticationFailure(authenticationFailureType);
		sendResponse(request, serviceResponseType);
	}

	private void sendResponse(Request request, ServiceResponseType serviceResponseType) {
		try {
			StringWriter stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(ServiceResponseType.class);
			Marshaller marshaller = context.createMarshaller();
			XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(stringWriter);
			xmlStreamWriter.setPrefix("cas", "http://www.yale.edu/tp/cas");
			marshaller.marshal(new ObjectFactory().createServiceResponse(serviceResponseType), xmlStreamWriter);
			request.getResponse().setStatusCode(200);
			request.getResponse().setBody(stringWriter.toString());
		} catch (JAXBException | XMLStreamException  e) {
			log.severe(e.toString());
			request.getResponse().setStatusCode(500);
			request.getResponse().setBody(e.getMessage());
		} finally {
			request.getResponse().close();
		}
	}

	public void proxyValidate(final Request request) {
		final String ticket = request.getParameter("ticket");
		if (ticket != null && ticket.startsWith("PT-")) {
			final String service = request.getParameter("service");
			final DataHandler dataHandler = dataHandlerFactory.create(request);
			dataHandler.validateProxyTicket(ticket, service,
					new Handler<Try<ValidationException, Tuple<AuthCas, User>>>() {
				@Override
				public void handle(Try<ValidationException, Tuple<AuthCas, User>> event) {
					try {
						final Tuple<AuthCas, User> t = event.get();
						AuthCas authCas = t._1;
						final ServiceTicket st = authCas.getServiceTicket(ticket);
						if (st != null && st.getPgt() != null && st.getPgt().exists(ticket)) {
							final String [] urls = new String[st.getPgt().getPgtUrls().size()];
							int i = urls.length;
							for (String url : st.getPgt().getPgtUrls()) {
								urls[--i] = url;
							}
							dataHandler.persistAuth(t._1, new Handler<Boolean>() {
								@Override
								public void handle(Boolean saved) {
									if (saved) {
										success(request, t._2, st.getPgt().getPgtIOU(), urls);
									} else {
										error(request, ErrorCodes.INTERNAL_ERROR);
									}
								}
							});
						} else {
							error(request, ErrorCodes.INVALID_TICKET);
						}
					} catch (ValidationException e) {
						error(request, e.getError());
					}
				}
			});
		} else {
			serviceValidate(request);
		}
	}

	public void proxy(final Request request) {
		final String pgt = request.getParameter("pgt");
		final String targetService = request.getParameter("targetService");
		if (pgt != null && !pgt.trim().isEmpty() &&
				targetService != null && !targetService.trim().isEmpty()) {
			final DataHandler dataHandler = dataHandlerFactory.create(request);
			dataHandler.validateProxyGrantingTicket(pgt, targetService,
					new Handler<Try<ValidationException, AuthCas>>() {
				@Override
				public void handle(Try<ValidationException, AuthCas> event) {
					try {
						AuthCas authCas = event.get();
						ServiceTicket st = authCas.getServiceTicketByProxyGrantingTicket(pgt);
						if (st != null && st.getPgt() != null) {
							final ProxyTicket pt = new ProxyTicket();
							st.getPgt().getProxyTickets().add(pt);
							dataHandler.persistAuth(authCas, new Handler<Boolean>(){
								@Override
								public void handle(Boolean saved) {
									if (saved) {
										successProxy(request, pt.getPgId());
									} else {
										errorProxy(request, ErrorCodes.INTERNAL_ERROR);
									}
								}
							});
						} else {
							errorProxy(request, ErrorCodes.INTERNAL_ERROR);
						}
					} catch (ValidationException e) {
						errorProxy(request, e.getError());
					}
				}
			});
		} else {
			errorProxy(request, ErrorCodes.INVALID_REQUEST);
		}
	}

	private void successProxy(Request request, String pgId) {
		ProxySuccessType proxySuccessType = new ProxySuccessType();
		proxySuccessType.setProxyTicket(pgId);
		ServiceResponseType serviceResponseType = new ServiceResponseType();
		serviceResponseType.setProxySuccess(proxySuccessType);
		sendResponse(request, serviceResponseType);
	}

	private void errorProxy(Request request, ErrorCodes invalidRequest) {
		ProxyFailureType proxyFailureType = new ProxyFailureType();
		proxyFailureType.setCode(invalidRequest.name());
		proxyFailureType.setValue(invalidRequest.getMessage());
		ServiceResponseType serviceResponseType = new ServiceResponseType();
		serviceResponseType.setProxyFailure(proxyFailureType);
		sendResponse(request, serviceResponseType);
	}

	public void samlValidate(Request request) {

	}

	public void setDataHandlerFactory(DataHandlerFactory dataHandlerFactory) {
		this.dataHandlerFactory = dataHandlerFactory;
	}

	public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}
}

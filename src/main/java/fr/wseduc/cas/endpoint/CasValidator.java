package fr.wseduc.cas.endpoint;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.yale.tp.cas.AttributesType;
import edu.yale.tp.cas.AuthenticationFailureType;
import edu.yale.tp.cas.AuthenticationSuccessType;
import edu.yale.tp.cas.ObjectFactory;
import edu.yale.tp.cas.ProxiesType;
import edu.yale.tp.cas.ProxyFailureType;
import edu.yale.tp.cas.ProxySuccessType;
import edu.yale.tp.cas.ServiceResponseType;
import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.async.Tuple;
import fr.wseduc.cas.data.DataHandler;
import fr.wseduc.cas.entities.AuthCas;
import fr.wseduc.cas.entities.ProxyTicket;
import fr.wseduc.cas.entities.ServiceTicket;
import fr.wseduc.cas.entities.User;
import fr.wseduc.cas.exceptions.ErrorCodes;
import fr.wseduc.cas.exceptions.Try;
import fr.wseduc.cas.exceptions.ValidationException;
import fr.wseduc.cas.http.Request;

public class CasValidator extends Validator {

	@Override
	public void serviceValidate(final Request request) {
		final String service = request.getParameter("service");
		final String ticket = request.getParameter("ticket");
		final boolean renew = Boolean.getBoolean(request.getParameter("renew"));
		final String pgtUrl = request.getParameter("pgtUrl");
		doValidate(request, service, ticket, renew, pgtUrl);
	}

	@Override
	protected void success(Request request, User user, String service) {
		success(request, user, service, null);
	}

	@Override
	protected void success(Request request, User user, String service, String pgtiou) {
		success(request, user, service, pgtiou, null);
	}

	@Override
	protected void success(Request request, User user, String service, String pgtiou, String[] proxyUrls) {
		AuthenticationSuccessType authenticationSuccessType = new AuthenticationSuccessType();
		authenticationSuccessType.setUser(user.getUser());
		if (pgtiou != null && !pgtiou.trim().isEmpty()) {
			authenticationSuccessType.setProxyGrantingTicket(pgtiou);
		}
		if (proxyUrls != null && proxyUrls.length > 0 ) {
			ProxiesType proxiesType = new ProxiesType();
			List<String> proxies = proxiesType.getProxy();
			Collections.addAll(proxies, proxyUrls);
			authenticationSuccessType.setProxies(proxiesType);
		}
		try {
			if (user.getAttributes() != null) {
				authenticationSuccessType.setAttributes(new AttributesType());
				authenticationSuccessType.getAttributes().setUserAttributes(
						new AttributesType.UserAttributes());
				List<Object> l = authenticationSuccessType.getAttributes().getUserAttributes().getAny();
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				for (Map.Entry<String, String> e : user.getAttributes().entrySet()) {
					Element element = doc.createElement(e.getKey());
					element.setTextContent(e.getValue());
					l.add(element);
				}
			}
			if (user.getAdditionnalAttributes() != null && (! user.getAdditionnalAttributes().isEmpty())) {
				authenticationSuccessType.getAdditionalAttributes().addAll(user.getAdditionnalAttributes());
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

	@Override
	protected void error(Request request, ErrorCodes invalidRequest) {
		AuthenticationFailureType authenticationFailureType = new AuthenticationFailureType();
		authenticationFailureType.setCode(invalidRequest.name());
		authenticationFailureType.setValue(invalidRequest.getMessage());
		ServiceResponseType serviceResponseType = new ServiceResponseType();
		serviceResponseType.setAuthenticationFailure(authenticationFailureType);
		sendResponse(request, serviceResponseType);
	}

	@Override
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
						final ServiceTicket st = authCas.getServiceTicketByProxyTicket(ticket);
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
										success(request, t._2, service, st.getPgt().getPgtIOU(), urls);
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

	@Override
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

	@Override
	protected void successProxy(Request request, String pgId) {
		ProxySuccessType proxySuccessType = new ProxySuccessType();
		proxySuccessType.setProxyTicket(pgId);
		ServiceResponseType serviceResponseType = new ServiceResponseType();
		serviceResponseType.setProxySuccess(proxySuccessType);
		sendResponse(request, serviceResponseType);
	}

	@Override
	protected void errorProxy(Request request, ErrorCodes invalidRequest) {
		ProxyFailureType proxyFailureType = new ProxyFailureType();
		proxyFailureType.setCode(invalidRequest.name());
		proxyFailureType.setValue(invalidRequest.getMessage());
		ServiceResponseType serviceResponseType = new ServiceResponseType();
		serviceResponseType.setProxyFailure(proxyFailureType);
		sendResponse(request, serviceResponseType);
	}

	private void sendResponse(Request request, ServiceResponseType serviceResponseType) {
		try {
			StringWriter stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(ServiceResponseType.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(new ObjectFactory().createServiceResponse(serviceResponseType), stringWriter);
			request.getResponse().setStatusCode(200);
			request.getResponse().setBody(stringWriter.toString());
		} catch (JAXBException e) {
			log.severe(e.toString());
			request.getResponse().setStatusCode(500);
			request.getResponse().setBody(e.getMessage());
		} finally {
			request.getResponse().close();
		}
	}
}

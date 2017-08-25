package fr.wseduc.cas.endpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.async.Tuple;
import fr.wseduc.cas.data.DataHandler;
import fr.wseduc.cas.data.DataHandlerFactory;
import fr.wseduc.cas.entities.AuthCas;
import fr.wseduc.cas.entities.ProxyGrantingTicket;
import fr.wseduc.cas.entities.ServiceTicket;
import fr.wseduc.cas.entities.User;
import fr.wseduc.cas.exceptions.ErrorCodes;
import fr.wseduc.cas.exceptions.Try;
import fr.wseduc.cas.exceptions.ValidationException;
import fr.wseduc.cas.http.ClientResponse;
import fr.wseduc.cas.http.HttpClient;
import fr.wseduc.cas.http.HttpClientFactory;
import fr.wseduc.cas.http.Request;

public abstract class Validator {

	protected DataHandlerFactory dataHandlerFactory;
	protected HttpClientFactory httpClientFactory;
	protected static final Logger log = Logger.getLogger(Validator.class.getName());

	public abstract void serviceValidate(final Request request);

	protected void doValidate(final Request request, final String service, final String ticket) {
		doValidate(request, service, ticket, false, null);
	}

	protected void doValidate(final Request request, final String service, final String ticket, final boolean renew, final String pgtUrl) {

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
											success(request, user, service);
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

	protected void validateProxy(String pgtUrl, final ServiceTicket st,
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

	protected abstract void success(Request request, User user, String service);

	protected abstract void success(Request request, User user, String service, String pgtiou);

	protected abstract void success(Request request, User user, String service, String pgtiou, String[] proxyUrls);

	protected abstract void error(Request request, ErrorCodes invalidRequest);

	public abstract void proxyValidate(final Request request);

	public abstract void proxy(final Request request);

	protected abstract void successProxy(Request request, String pgId);

	protected abstract void errorProxy(Request request, ErrorCodes invalidRequest);


	public void setDataHandlerFactory(DataHandlerFactory dataHandlerFactory) {
		this.dataHandlerFactory = dataHandlerFactory;
	}

	public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}
}

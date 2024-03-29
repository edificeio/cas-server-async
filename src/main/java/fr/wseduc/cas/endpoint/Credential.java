package fr.wseduc.cas.endpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.data.DataHandler;
import fr.wseduc.cas.data.DataHandlerFactory;
import fr.wseduc.cas.entities.AuthCas;
import fr.wseduc.cas.entities.LoginTicket;
import fr.wseduc.cas.entities.ServiceTicket;
import fr.wseduc.cas.exceptions.AuthenticationException;
import fr.wseduc.cas.exceptions.Try;
import fr.wseduc.cas.http.ClientResponse;
import fr.wseduc.cas.http.HttpClient;
import fr.wseduc.cas.http.HttpClientFactory;
import fr.wseduc.cas.http.Request;

public class Credential {

	private DataHandlerFactory dataHandlerFactory;
	private CredentialResponse credentialResponse;
	private HttpClientFactory httpClientFactory;
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private static final Logger log = Logger.getLogger(Credential.class.getName());

	public void loginRequestor(final Request request) {
		final DataHandler dataHandler = dataHandlerFactory.create(request);
		final String service = request.getParameter("service") != null ? request.getParameter("service") : request.getParameter("TARGET");
		final boolean renew = Boolean.getBoolean(request.getParameter("renew"));
		final boolean gateway = Boolean.getBoolean(request.getParameter("gateway"));
		final String method = request.getParameter("method") != null ? request.getParameter("method") : "GET";
		dataHandler.getOrCreateAuth(request, new Handler<AuthCas>() {
			@Override
			public void handle(AuthCas authCas) {
				// TODO check if null
				if (!authCas.isLoggedIn()) {
					credentialResponse.loginRequestorResponse(request, new LoginTicket(), service, renew, gateway, method);
				} else {
					if (authCas.getForceChangePassword()) {
						credentialResponse.resetPasswordRequestorResponse(request);
					} else {
						if (service != null && !service.trim().isEmpty()) {
							loginAcceptor(request, authCas);
						} else {
							credentialResponse.loggedIn(request);
						}
					}
				}
			}
		});
	}

	public void loginAcceptor(final Request request) {
		loginAcceptor(request, null);
	}

	private void loginAcceptor(final Request request, final AuthCas authCas) {
		final DataHandler dataHandler = dataHandlerFactory.create(request);
		if (authCas != null && authCas.isLoggedIn()) {
			generateServiceTicket(request, authCas, dataHandler);
		} else {
			dataHandler.getOrCreateAuth(request, new Handler<AuthCas>() {
				@Override
				public void handle(AuthCas auth) {
					if (auth != null && auth.isLoggedIn()) {
						generateServiceTicket(request, authCas, dataHandler);
					} else {
						request.getFormAttributesMap(new Handler<Map<String, String>>() {
							@Override
							public void handle(Map<String, String> attributes) {
								dataHandler.authenticateUser(
										attributes.get("login"),
										attributes.get("password"),
										authCas, new Handler<Try<AuthenticationException, AuthCas>>() {
									@Override
									public void handle(Try<AuthenticationException, AuthCas> ac) {
										try {
											generateServiceTicket(request, ac.get(), dataHandler);
										} catch (AuthenticationException e) {
											credentialResponse.denyResponse(request, e);
										}
									}
								});
							}
						});
					}
				}
			});
		}
	}

	private void generateServiceTicket(final Request request, final AuthCas authCas, final DataHandler dataHandler) {
		final String service = request.getParameter("service") != null ? request.getParameter("service") : request.getParameter("TARGET");
		dataHandler.validateService(authCas, service, new Handler<Boolean>(){
			@Override
			public void handle(Boolean success) {
				if (success) {
					final ServiceTicket serviceTicket = new ServiceTicket(service);
					final String ticketParameterName = request.getParameter("ticketAttributeName");
					if (ticketParameterName != null && !ticketParameterName.trim().isEmpty()) {
						serviceTicket.setTicketParameter(ticketParameterName);
					}
					if (request.getParameter("TARGET") != null) {
						serviceTicket.setTicketParameter("SAMLart");
					}
					authCas.addServiceTicket(serviceTicket);
					dataHandler.persistAuth(authCas, new Handler<Boolean>() {
						@Override
						public void handle(Boolean success) {
							if (success) {
								credentialResponse.loginAcceptorResponse(request, serviceTicket);
							} else {
								credentialResponse.denyResponse(request,
										new AuthenticationException("SESSION_ERROR"));
							}
					 }
					});
				} else {
					credentialResponse.denyResponse(request, new AuthenticationException("INVALID_SERVICE"));
				}
			}
		});
	}

	public void logout(final Request request) {
		final String service = request.getParameter("service");
		final DataHandler dataHandler = dataHandlerFactory.create(request);
		dataHandler.getAndDestroyAuth(request, new Handler<AuthCas>(){
			@Override
			public void handle(AuthCas authCas) {
				if (authCas != null) {
					singleLogout(authCas);
				}
				if (service != null && !service.trim().isEmpty()) {
					credentialResponse.logoutRedirectService(request, service);
				} else {
					credentialResponse.logoutResponse(request);
				}
			}
		});
	}

	public void logout(final String user) {
		final DataHandler dataHandler = dataHandlerFactory.create(null);
		dataHandler.getAndDestroyAuth(user, new Handler<AuthCas>(){
			@Override
			public void handle(AuthCas authCas) {
				if (authCas != null) {
					singleLogout(authCas);
				}
			}
		});
	}

	private void singleLogout(AuthCas authCas) {
		for (final ServiceTicket st : authCas.getServiceTickets()) {
			try {
				final URI uri = new URI(st.getService());
				int port = uri.getPort() > 0 ? uri.getPort() :
						("https".equals(uri.getScheme()) ? 443 : 80);
				final HttpClient client = httpClientFactory.create(uri.getHost(),
						port, "https".equals(uri.getScheme()));
				log.fine("service uri for post slo request is " + st.getService() +  " ; with ticket : " + st.getTicket());
				client.post(st.getService(), sloBody(st.getTicket()), new Handler<ClientResponse>() {
					@Override
					public void handle(ClientResponse cr) {
						if (cr != null && cr.getStatusCode() != 200) {
							log.fine("Bad response received for post logout request. Status code : " + cr.getStatusCode() + " with uri  : " + st.getService() + ", with ticket : " + st.getTicket());
						}
					}
				});
			} catch (URISyntaxException e) {
				log.severe(e.getMessage());
			}
		}
	}

	private String sloBody(String ticket) {
		return "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\"\n" +
				"     ID=\"" + UUID.randomUUID().toString() + "\" Version=\"2.0\" IssueInstant=\"" +
				df.format(new Date()) + "\">\n" +
				"    <saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">\n" +
				"      @NOT_USED@\n" +
				"    </saml:NameID>\n" +
				"    <samlp:SessionIndex>" + ticket + "</samlp:SessionIndex>\n" +
				"  </samlp:LogoutRequest>";
	}


	public void setDataHandlerFactory(DataHandlerFactory dataHandlerFactory) {
		this.dataHandlerFactory = dataHandlerFactory;
	}

	public void setCredentialResponse(CredentialResponse credentialResponse) {
		this.credentialResponse = credentialResponse;
	}

	public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}
}

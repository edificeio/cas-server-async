package fr.wseduc.cas.endpoint;

import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.data.*;
import fr.wseduc.cas.entities.AuthCas;
import fr.wseduc.cas.entities.LoginTicket;
import fr.wseduc.cas.entities.ServiceTicket;
import fr.wseduc.cas.exceptions.AuthenticationException;
import fr.wseduc.cas.exceptions.Try;
import fr.wseduc.cas.http.Request;

import java.util.Map;

public class Credential {

	private DataHandlerFactory dataHandlerFactory;
	private CredentialResponse credentialResponse;

	public void loginRequestor(final Request request) {
		final DataHandler dataHandler = dataHandlerFactory.create(request);
		final String service = request.getParameter("service");
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
					if (service != null && !service.trim().isEmpty()) {
						loginAcceptor(request, authCas);
					} else {
						credentialResponse.loggedIn(request);
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
		final String service = request.getParameter("service");
		dataHandler.validateService(service, new Handler<Boolean>(){
			@Override
			public void handle(Boolean success) {
				if (success) {
					final ServiceTicket serviceTicket = new ServiceTicket(service);
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

	public void logout(Request request) {

	}

	protected void logoutResponse() {

	}

	private void singleLogout() {

	}

	public void setDataHandlerFactory(DataHandlerFactory dataHandlerFactory) {
		this.dataHandlerFactory = dataHandlerFactory;
	}

	public void setCredentialResponse(CredentialResponse credentialResponse) {
		this.credentialResponse = credentialResponse;
	}

}

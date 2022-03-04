package fr.wseduc.cas.endpoint;

import fr.wseduc.cas.http.Request;
import fr.wseduc.cas.http.Response;
import fr.wseduc.cas.entities.LoginTicket;
import fr.wseduc.cas.entities.ServiceTicket;
import fr.wseduc.cas.exceptions.AuthenticationException;

public abstract class CredentialResponse {

	public abstract void loginRequestorResponse(Request request, LoginTicket loginTicket,
			String service, boolean renew, boolean gateway, String method);

	public abstract void resetPasswordRequestorResponse(Request request);

	public void loginAcceptorResponse(Request request, ServiceTicket serviceTicket) {
		Response response = request.getResponse();
		response.putHeader("Location", serviceTicket.redirectUri());
		response.setStatusCode(302);
		response.close();
	}

	public void denyResponse(Request request, AuthenticationException e) {
		Response response = request.getResponse();
		response.setStatusCode(400);
		response.setBody(e.getMessage());
		response.close();
	}

	public abstract void loggedIn(Request request);

	public void logoutRedirectService(Request request, String service) {
		Response response = request.getResponse();
		response.putHeader("Location", service);
		response.setStatusCode(302);
		response.close();
	}

	public void logoutResponse(Request request) {

	}

}

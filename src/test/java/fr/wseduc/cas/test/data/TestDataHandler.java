package fr.wseduc.cas.test.data;

import java.util.HashMap;
import java.util.Map;

import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.data.DataHandler;
import fr.wseduc.cas.entities.AuthCas;
import fr.wseduc.cas.entities.User;
import fr.wseduc.cas.exceptions.AuthenticationException;
import fr.wseduc.cas.exceptions.Try;
import fr.wseduc.cas.http.Request;

public class TestDataHandler extends DataHandler {

	private static final Map<String, String> tickets = new HashMap<>();

	protected TestDataHandler(Request request) {
		super(request);
	}

	@Override
	public void validateService(String service, Handler<Boolean> handler) {

	}

	@Override
	public void authenticateUser(String user, String password, AuthCas authCas,
			Handler<Try<AuthenticationException, AuthCas>> handler) {

	}

	@Override
	protected void getAuthByProxyGrantingTicket(String pgt, Handler<AuthCas> handler) {

	}

	@Override
	protected void getUser(String ticket, String service, Handler<User> handler) {

	}

	@Override
	protected void getAuth(String ticket, Handler<AuthCas> handler) {

	}

	@Override
	protected void getAuthByProxyTicket(String ticket, Handler<AuthCas> handler) {

	}

	@Override
	public void getOrCreateAuth(Request request, Handler<AuthCas> handler) {

	}

	@Override
	public void persistAuth(AuthCas authCas, Handler<Boolean> handler) {

	}

	@Override
	public void getAndDestroyAuth(Request request, Handler<AuthCas> handler) {

	}

	@Override
	public void getAndDestroyAuth(String user, Handler<AuthCas> handler) {

	}

}

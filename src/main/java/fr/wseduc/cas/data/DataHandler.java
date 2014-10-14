package fr.wseduc.cas.data;

import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.entities.AuthCas;
import fr.wseduc.cas.entities.ServiceTicket;
import fr.wseduc.cas.entities.User;
import fr.wseduc.cas.exceptions.AuthenticationException;
import fr.wseduc.cas.exceptions.ErrorCodes;
import fr.wseduc.cas.exceptions.Try;
import fr.wseduc.cas.exceptions.ValidationException;

public abstract class DataHandler {

	protected final Request request;

	protected DataHandler(Request request) {
		this.request = request;
	}

	public abstract void validateService(String service, Handler<Boolean> handler);

	public abstract void authenticateUser(String user, String password, AuthCas authCas,
			Handler<Try<AuthenticationException, AuthCas>> handler);

	public void validateTicket(final String ticket, final String service,
			final Handler<Try<ValidationException, User>> handler) {
		getAuth(ticket, new Handler<AuthCas>() {
			@Override
			public void handle(AuthCas authCas) {
				ServiceTicket st;
				if (authCas != null && (st = authCas.getServiceTicket(ticket)) != null) {
					if (st.getService().equals(service)) {
						getUser(authCas.getUser(), new Handler<User>() {
							@Override
							public void handle(User user) {
								if (user != null) {
									handler.handle(new Try<ValidationException, User>(user));
								} else {
									handler.handle(new Try<ValidationException, User>(
											new ValidationException(ErrorCodes.INVALID_TICKET)));
								}
							}
						});
					} else {
						handler.handle(new Try<ValidationException, User>(new ValidationException(ErrorCodes.INVALID_SERVICE)));
					}
				} else {
					handler.handle(new Try<ValidationException, User>(new ValidationException(ErrorCodes.INVALID_TICKET)));
				}
			}
		});
	}

	protected abstract void getUser(String userId, Handler<User> handler);

	protected abstract void getAuth(String ticket, Handler<AuthCas> handler);

	public abstract void getOrCreateAuth(Request request, Handler<AuthCas> handler);

	public abstract void persistAuth(AuthCas authCas, Handler<Boolean> handler);

}

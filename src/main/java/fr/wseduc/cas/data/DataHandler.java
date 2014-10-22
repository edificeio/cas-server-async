package fr.wseduc.cas.data;

import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.async.Tuple;
import fr.wseduc.cas.entities.AuthCas;
import fr.wseduc.cas.entities.ProxyTicket;
import fr.wseduc.cas.entities.ServiceTicket;
import fr.wseduc.cas.entities.User;
import fr.wseduc.cas.exceptions.AuthenticationException;
import fr.wseduc.cas.exceptions.ErrorCodes;
import fr.wseduc.cas.exceptions.Try;
import fr.wseduc.cas.exceptions.ValidationException;
import fr.wseduc.cas.http.Request;

public abstract class DataHandler {

	protected final Request request;

	protected DataHandler(Request request) {
		this.request = request;
	}

	public abstract void validateService(String service, Handler<Boolean> handler);

	public abstract void authenticateUser(String user, String password, AuthCas authCas,
			Handler<Try<AuthenticationException, AuthCas>> handler);

	public void validateTicket(final String ticket, final String service,
			final Handler<Try<ValidationException, Tuple<AuthCas, User>>> handler) {
		getAuth(ticket, new Handler<AuthCas>() {
			@Override
			public void handle(final AuthCas authCas) {
				ServiceTicket st;
				long now = System.currentTimeMillis();
				if (authCas != null && (st = authCas.getServiceTicket(ticket)) != null &&
						!st.isUsed() && (now - st.getIssued()) < 300000) {
					st.setUsed(true);
					validateService(authCas, st, service, handler);
				} else {
					handler.handle(new Try<ValidationException, Tuple<AuthCas, User>>(
							new ValidationException(ErrorCodes.INVALID_TICKET)));
				}
			}
		});
	}

	private void validateService(final AuthCas authCas, ServiceTicket st, String service,
			final Handler<Try<ValidationException, Tuple<AuthCas, User>>> handler) {
		if (st.getService().equals(service)) {
			getUser(authCas.getUser(), new Handler<User>() {
				@Override
				public void handle(User user) {
					if (user != null) {
						handler.handle(new Try<ValidationException, Tuple<AuthCas, User>>(
								new Tuple<>(authCas, user)));
					} else {
						handler.handle(new Try<ValidationException, Tuple<AuthCas, User>>(
								new ValidationException(ErrorCodes.INVALID_TICKET)));
					}
				}
			});
		} else {
			handler.handle(new Try<ValidationException, Tuple<AuthCas, User>>(
					new ValidationException(ErrorCodes.INVALID_SERVICE)));
		}

	}

	public void validateProxyTicket(final String ticket, final String service,
			final Handler<Try<ValidationException, Tuple<AuthCas, User>>> handler) {
		getAuthByProxyTicket(ticket, new Handler<AuthCas>() {
			@Override
			public void handle(final AuthCas authCas) {
				ServiceTicket st;
				ProxyTicket pt;
				long now = System.currentTimeMillis();
				if (authCas != null && (st = authCas.getServiceTicketByProxyTicket(ticket)) != null &&
						st.getPgt() != null && (pt = st.getPgt().getProxyTicket(ticket)) != null &&
						!pt.isUsed() && (now - pt.getIssued()) < 300000) {
					pt.setUsed(true);
					validateService(authCas, st, service, handler);
				} else {
					handler.handle(new Try<ValidationException, Tuple<AuthCas, User>>(
							new ValidationException(ErrorCodes.INVALID_TICKET)));
				}
			}
		});
	}

	public void validateProxyGrantingTicket(final String pgt, final String targetService,
			final Handler<Try<ValidationException, AuthCas>> handler) {
		getAuthByProxyGrantingTicket(pgt, new Handler<AuthCas>() {
			@Override
			public void handle(AuthCas authCas) {
				if (authCas != null) {
					ServiceTicket st = authCas.getServiceTicketByProxyGrantingTicket(pgt);
					if (st != null && st.getService() != null && st.getService().equals(targetService)) {
						handler.handle(new Try<ValidationException, AuthCas>(authCas));
					} else {
						handler.handle(new Try<ValidationException, AuthCas>(
								new ValidationException(ErrorCodes.INVALID_SERVICE)));
					}
				} else {
					handler.handle(new Try<ValidationException, AuthCas>(
							new ValidationException(ErrorCodes.INVALID_TICKET)));
				}
			}
		});
	}

	protected abstract void getAuthByProxyGrantingTicket(String pgt, Handler<AuthCas> handler);

	protected abstract void getUser(String userId, Handler<User> handler);

	protected abstract void getAuth(String ticket, Handler<AuthCas> handler);

	protected abstract void getAuthByProxyTicket(String ticket, Handler<AuthCas> handler);

	public abstract void getOrCreateAuth(Request request, Handler<AuthCas> handler);

	public abstract void persistAuth(AuthCas authCas, Handler<Boolean> handler);

	public abstract void getAndDestroyAuth(Request request, Handler<AuthCas> handler);

	public abstract void getAndDestroyAuth(String user, Handler<AuthCas> handler);

}

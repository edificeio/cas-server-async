package fr.wseduc.cas.http;

import fr.wseduc.cas.async.Handler;

public interface HttpClient {

	void get(String uri, Handler<ClientResponse> handler);

}

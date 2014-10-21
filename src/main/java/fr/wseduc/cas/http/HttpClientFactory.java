package fr.wseduc.cas.http;

public interface HttpClientFactory {

	HttpClient create(String host, int port, boolean ssl);

}

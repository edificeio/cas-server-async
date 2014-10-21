package fr.wseduc.cas.test.data;

import fr.wseduc.cas.http.Response;

import java.util.HashMap;
import java.util.Map;

public class MockResponse implements Response {

	private Map<String, String> headers = new HashMap<>();
	private int statusCode = 200;
	private String body = "";

	@Override
	public void setStatusCode(int status) {
		this.statusCode = status;
	}

	@Override
	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public void putHeader(String key, String value) {
		headers.put(key, value);
	}

	@Override
	public void close() {
		System.out.println(body);
	}

}

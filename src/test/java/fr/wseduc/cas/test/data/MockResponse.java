package fr.wseduc.cas.test.data;

import java.util.HashMap;
import java.util.Map;

public class MockResponse implements fr.wseduc.cas.data.Response {

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

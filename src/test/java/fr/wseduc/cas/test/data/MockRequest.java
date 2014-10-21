package fr.wseduc.cas.test.data;

import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.http.Request;
import fr.wseduc.cas.http.Response;

import java.util.Map;

public class MockRequest implements Request {

	private final Map<String, String> params;
	private final Map<String, String> headers;
	private final Map<String, String> formAttributes;
	private final Response response;

	public MockRequest(Map<String, String> params, Map<String, String> headers, Map<String, String> formAttributes) {
		this.params = params;
		this.headers = headers;
		this.formAttributes = formAttributes;
		this.response = new MockResponse();
	}

	@Override
	public String getParameter(String name) {
		return null;
	}

	@Override
	public Map<String, String> getParameterMap() {
		return null;
	}

	@Override
	public String getHeader(String name) {
		return null;
	}

	@Override
	public Response getResponse() {
		return null;
	}

	@Override
	public void getFormAttributesMap(Handler<Map<String, String>> handler) {

	}

}

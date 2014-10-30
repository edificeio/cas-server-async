package fr.wseduc.cas.http;

import java.util.Map;

import fr.wseduc.cas.async.Handler;

public interface Request {

	String getParameter(String name);

	Map<String, String> getParameterMap();

	String getHeader(String name);

	Response getResponse();

	void getFormAttributesMap(Handler<Map<String, String>> handler);

	void getBody(Handler<String> handler, String encoding);
}

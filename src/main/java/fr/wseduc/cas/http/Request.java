package fr.wseduc.cas.http;

import fr.wseduc.cas.async.Handler;

import java.util.Map;

public interface Request {

	String getParameter(String name);

	Map<String, String> getParameterMap();

	String getHeader(String name);

	Response getResponse();

	void getFormAttributesMap(Handler<Map<String, String>> handler);

}

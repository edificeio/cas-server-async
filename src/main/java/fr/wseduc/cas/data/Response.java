package fr.wseduc.cas.data;

public interface Response {

	void setStatusCode(int status);

	void setBody(String body);

	void putHeader(String key, String value);

	void close();

}

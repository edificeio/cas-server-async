package fr.wseduc.cas.data;

import fr.wseduc.cas.http.Request;

public interface DataHandlerFactory {

	public DataHandler create(Request request);

}

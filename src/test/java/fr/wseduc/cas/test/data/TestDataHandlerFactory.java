package fr.wseduc.cas.test.data;

import fr.wseduc.cas.data.DataHandler;
import fr.wseduc.cas.data.DataHandlerFactory;
import fr.wseduc.cas.http.Request;

public class TestDataHandlerFactory implements DataHandlerFactory {

	@Override
	public DataHandler create(Request request) {
		return new TestDataHandler(request);
	}

}

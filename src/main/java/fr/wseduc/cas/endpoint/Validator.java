package fr.wseduc.cas.endpoint;

import edu.yale.tp.cas.*;
import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.data.DataHandler;
import fr.wseduc.cas.data.DataHandlerFactory;
import fr.wseduc.cas.data.Request;
import fr.wseduc.cas.entities.User;
import fr.wseduc.cas.exceptions.ErrorCodes;
import fr.wseduc.cas.exceptions.Try;
import fr.wseduc.cas.exceptions.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Validator {

	private DataHandlerFactory dataHandlerFactory;
	private static final Logger log = Logger.getLogger("Validator");

	public void serviceValidate(final Request request) {
		final String service = request.getParameter("service");
		final String ticket = request.getParameter("ticket");
		final boolean renew = Boolean.getBoolean(request.getParameter("renew"));
		final String pgtUrl = request.getParameter("pgtUrl");
		if (service != null && ticket != null && !service.trim().isEmpty() && !ticket.trim().isEmpty()) {
			if (ticket.startsWith("ST-")) {
				DataHandler dataHandler = dataHandlerFactory.create(request);
				dataHandler.validateTicket(ticket, service, new Handler<Try<ValidationException, User>>() {
					@Override
					public void handle(Try<ValidationException, User> v) {
						try {
							User user = v.get();
							success(request, user);
						} catch (ValidationException e) {
							error(request, e.getError());
						}
					}
				});
			} else {
				error(request, ErrorCodes.UNAUTHORIZED_SERVICE_PROXY);
			}
		} else {
			error(request, ErrorCodes.INVALID_REQUEST);
		}
	}

	private void success(Request request, User user) {
		AuthenticationSuccessType authenticationSuccessType = new AuthenticationSuccessType();
		authenticationSuccessType.setUser(user.getUser());
		authenticationSuccessType.setAttributes(new AttributesType());
		authenticationSuccessType.getAttributes().setUserAttributes(new AttributesType.UserAttributes());
		List<Object> l = authenticationSuccessType.getAttributes().getUserAttributes().getAny();
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			for (Map.Entry<String, String> e : user.getAttributes().entrySet()) {
				Element element = doc.createElement(e.getKey());
				element.setTextContent(e.getValue());
				l.add(element);
			}
			ServiceResponseType serviceResponseType = new ServiceResponseType();
			serviceResponseType.setAuthenticationSuccess(authenticationSuccessType);
			sendResponse(request, serviceResponseType);
		} catch (ParserConfigurationException e) {
			log.severe(e.toString());
			request.getResponse().setStatusCode(500);
			request.getResponse().setBody(e.getMessage());
		}
	}

	private void error(Request request, ErrorCodes invalidRequest) {
		AuthenticationFailureType authenticationFailureType = new AuthenticationFailureType();
		authenticationFailureType.setCode(invalidRequest.name());
		authenticationFailureType.setValue(invalidRequest.getMessage());
		ServiceResponseType serviceResponseType = new ServiceResponseType();
		serviceResponseType.setAuthenticationFailure(authenticationFailureType);
		sendResponse(request, serviceResponseType);
	}

	private void sendResponse(Request request, ServiceResponseType serviceResponseType) {
		try {
			StringWriter stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(ServiceResponseType.class);
			Marshaller marshaller = context.createMarshaller();
			XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(stringWriter);
			xmlStreamWriter.setPrefix("cas", "http://www.yale.edu/tp/cas");
			marshaller.marshal(new ObjectFactory().createServiceResponse(serviceResponseType), xmlStreamWriter);
			request.getResponse().setStatusCode(200);
			request.getResponse().setBody(stringWriter.toString());
		} catch (JAXBException | XMLStreamException  e) {
			log.severe(e.toString());
			request.getResponse().setStatusCode(500);
			request.getResponse().setBody(e.getMessage());
		} finally {
			request.getResponse().close();
		}
	}

	public void proxyValidate(Request request) {

	}

	public void samlValidate(Request request) {

	}

	public void setDataHandlerFactory(DataHandlerFactory dataHandlerFactory) {
		this.dataHandlerFactory = dataHandlerFactory;
	}

}

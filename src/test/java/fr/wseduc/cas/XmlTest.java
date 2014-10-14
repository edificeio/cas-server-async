package fr.wseduc.cas;

import edu.yale.tp.cas.AttributesType;
import edu.yale.tp.cas.AuthenticationSuccessType;
import edu.yale.tp.cas.ObjectFactory;
import edu.yale.tp.cas.ServiceResponseType;
import fr.wseduc.cas.entities.User;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlTest {

	@Test
	public void marshallSuccessTest() throws JAXBException, XMLStreamException {
		User user = new User();
		user.setUser("bla");
		user.setAttributes(new HashMap<String, String>());
		user.getAttributes().put("blip", "blop");
		user.getAttributes().put("titi", "toto");
		AuthenticationSuccessType authenticationSuccessType = new AuthenticationSuccessType();
		authenticationSuccessType.setUser(user.getUser());
		authenticationSuccessType.setAttributes(new AttributesType());
		authenticationSuccessType.getAttributes().setUserAttributes(new AttributesType.UserAttributes());
		List<Object> l = authenticationSuccessType.getAttributes().getUserAttributes().getAny();
//		for (Map.Entry<String, String> e : user.getAttributes().entrySet()) {
//			l.add(e);
//		}
		ServiceResponseType serviceResponseType = new ServiceResponseType();
		serviceResponseType.setAuthenticationSuccess(authenticationSuccessType);
		StringWriter stringWriter = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(ServiceResponseType.class);
		Marshaller marshaller = context.createMarshaller();
		XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance()
				.createXMLStreamWriter(stringWriter);
		xmlStreamWriter.setPrefix("cas", "http://www.yale.edu/tp/cas");
		marshaller.marshal(new ObjectFactory().createServiceResponse(serviceResponseType), xmlStreamWriter);
		System.out.println(stringWriter.toString());
	}

}

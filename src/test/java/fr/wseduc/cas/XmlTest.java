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
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

public class XmlTest {

	@Test
	public void marshallSuccessTest() throws JAXBException {
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
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(new ObjectFactory().createServiceResponse(serviceResponseType), stringWriter);
		System.out.println(stringWriter.toString());
	}

}

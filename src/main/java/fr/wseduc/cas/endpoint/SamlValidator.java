package fr.wseduc.cas.endpoint;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.xmlsoap.schemas.soap.envelope.Envelope;

import urn.oasis.names.tc.saml.assertion.AssertionType;
import urn.oasis.names.tc.saml.protocol.RequestType;
import urn.oasis.names.tc.saml.protocol.ResponseType;
import fr.wseduc.cas.async.Handler;
import fr.wseduc.cas.entities.User;
import fr.wseduc.cas.exceptions.ErrorCodes;
import fr.wseduc.cas.http.Request;

public class SamlValidator extends Validator {

	private long assertionValidityTimeMillis = 30000;

	@Override
	public void serviceValidate(final Request request) {
		final String service = request.getParameter("TARGET");
		request.getBody(new Handler<String>(){
			@Override
			public void handle(String body) {
				try {
					JAXBContext context = JAXBContext.newInstance(Envelope.class, ResponseType.class, AssertionType.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					StringReader reader = new StringReader(body);

					XMLInputFactory xif = XMLInputFactory.newFactory();
					xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
					xif.setProperty("javax.xml.stream.isSupportingExternalEntities", false);

					XMLStreamReader xmlReader = xif.createXMLStreamReader(reader);
					JAXBElement<Envelope> xmlRequest = unmarshaller.unmarshal(xmlReader, Envelope.class);

					JAXBElement<RequestType> samlRequest = (JAXBElement<RequestType>) xmlRequest.getValue().getBody().getAny().get(0);
					String ticket = samlRequest.getValue().getAssertionArtifact().get(0);

					doValidate(request, service, ticket);
				} catch (JAXBException | XMLStreamException e) {
					log.severe(e.toString());
					request.getResponse().setStatusCode(500);
					request.getResponse().setBody(e.getMessage());
					error(request, ErrorCodes.INVALID_REQUEST);
				} catch (Exception e) {
					log.severe(e.toString());
					request.getResponse().setStatusCode(500);
					request.getResponse().setBody(e.getMessage());
					error(request, ErrorCodes.INVALID_REQUEST);
				}
			}
		}, "UTF-8");
	}

	@Override
	protected void success(Request request, User user, String service) {
		try {
			GregorianCalendar gcalendar = new GregorianCalendar();
			gcalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			gcalendar.setTimeInMillis(System.currentTimeMillis());
			XMLGregorianCalendar xmlNow = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcalendar);
			String timeNow = xmlNow.toXMLFormat();
			gcalendar.setTimeInMillis(System.currentTimeMillis() + assertionValidityTimeMillis);
			XMLGregorianCalendar xmlExpire = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcalendar);
			String timeExpire = xmlExpire.toXMLFormat();

			StringWriter stringWriter = new StringWriter();
			XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
			writeEnvelopeStart(xmlStreamWriter);
			writeResponseStart(xmlStreamWriter, service, timeNow);
			writeSuccess(xmlStreamWriter);
			writeAssertion(xmlStreamWriter, user, service, timeNow, timeExpire);
			writeResponseEnd(xmlStreamWriter);
			writeEnvelopeEnd(xmlStreamWriter);

			request.getResponse().setStatusCode(200);
			request.getResponse().setBody(stringWriter.toString());
		} catch (DatatypeConfigurationException | XMLStreamException  e) {
			log.severe(e.toString());
			request.getResponse().setStatusCode(500);
			request.getResponse().setBody(e.getMessage());
		} finally {
			request.getResponse().close();
		}
	}

	@Override
	protected void success(Request request, User user, String service, String pgtiou) {
		error(request, ErrorCodes.UNSUPPORTED_SAML_PROXY_REQUEST);
	}

	@Override
	protected void success(Request request, User user, String service, String pgtiou,
			String[] proxyUrls) {
		error(request, ErrorCodes.UNSUPPORTED_SAML_PROXY_REQUEST);
	}

	@Override
	protected void error(Request request, ErrorCodes invalidRequest) {
		try {
			GregorianCalendar gcalendar = new GregorianCalendar();
			gcalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			gcalendar.setTimeInMillis(System.currentTimeMillis());
			XMLGregorianCalendar xmlNow = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcalendar);
			String timeNow = xmlNow.toXMLFormat();

			StringWriter stringWriter = new StringWriter();
			XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
			writeEnvelopeStart(xmlStreamWriter);
			writeResponseStart(xmlStreamWriter, "localhost", timeNow); // TODO : Service needed here ?
			writeError(xmlStreamWriter, invalidRequest.getMessage());
			writeResponseEnd(xmlStreamWriter);
			writeEnvelopeEnd(xmlStreamWriter);

			request.getResponse().setStatusCode(200);
			request.getResponse().setBody(stringWriter.toString());
		} catch (DatatypeConfigurationException | XMLStreamException  e) {
			log.severe(e.toString());
			request.getResponse().setStatusCode(500);
			request.getResponse().setBody(e.getMessage());
		} finally {
			request.getResponse().close();
		}
	}

	private void writeEnvelopeStart(XMLStreamWriter xw) throws XMLStreamException {
		xw.setPrefix("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
		xw.writeStartElement("http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
		xw.writeNamespace("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
		xw.writeEmptyElement("http://schemas.xmlsoap.org/soap/envelope/", "Header");
		xw.writeStartElement("http://schemas.xmlsoap.org/soap/envelope/", "Body");
	}

	private void writeEnvelopeEnd(XMLStreamWriter xw) throws XMLStreamException {
		xw.writeEndElement();
		xw.writeEndElement();
	}

	private void writeResponseStart(XMLStreamWriter xw, String service, String timeNow) throws XMLStreamException {
		xw.setPrefix("", "urn:oasis:names:tc:SAML:1.0:protocol");
		xw.writeStartElement("urn:oasis:names:tc:SAML:1.0:protocol", "Response");
		xw.writeNamespace("", "urn:oasis:names:tc:SAML:1.0:protocol");
		xw.writeNamespace("samlp", "urn:oasis:names:tc:SAML:1.0:protocol");
		xw.writeNamespace("saml", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
		xw.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		xw.writeAttribute("IssueInstant", timeNow);
		xw.writeAttribute("MajorVersion", "1");
		xw.writeAttribute("MinorVersion", "1");
		xw.writeAttribute("Recipient", service);
		xw.writeAttribute("ResponseID", "_" + UUID.randomUUID().toString());
	}

	private void writeResponseEnd(XMLStreamWriter xw) throws XMLStreamException {
		xw.writeEndElement();
	}

	private void writeSuccess(XMLStreamWriter xw) throws XMLStreamException {
		xw.writeStartElement("", "Status", "urn:oasis:names:tc:SAML:1.0:protocol");
		xw.writeStartElement("", "StatusCode", "urn:oasis:names:tc:SAML:1.0:protocol");
		xw.writeAttribute("Value", "samlp:Success");
		xw.writeEndElement();
		xw.writeEndElement();
	}

	private void writeError(XMLStreamWriter xw, String message) throws XMLStreamException {
		xw.writeStartElement("", "Status", "urn:oasis:names:tc:SAML:1.0:protocol");
		xw.writeStartElement("", "StatusCode", "urn:oasis:names:tc:SAML:1.0:protocol");
		xw.writeAttribute("Value", "samlp:Responder");
		xw.writeEndElement();
		xw.writeStartElement("", "StatusMessage", "urn:oasis:names:tc:SAML:1.0:protocol");
		xw.writeCharacters(message);
		xw.writeEndElement();
		xw.writeEndElement();
	}

	private void writeAssertion(XMLStreamWriter xw, User user, String service, String timeNow, String timeExpire) throws XMLStreamException {
		xw.setPrefix("", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeStartElement("", "Assertion", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeNamespace("", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeAttribute("AssertionID", "_" + UUID.randomUUID().toString());
		xw.writeAttribute("IssueInstant", timeNow);
		xw.writeAttribute("Issuer", "localhost"); // TODO: Host from conf
		xw.writeAttribute("MajorVersion", "1");
		xw.writeAttribute("MinorVersion", "1");

		xw.writeStartElement("", "Conditions", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeAttribute("NotBefore", timeNow);
		xw.writeAttribute("NotOnOrAfter", timeExpire);
		xw.writeStartElement("", "AudienceRestrictionCondition", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeStartElement("", "Audience", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeCharacters(service);
		xw.writeEndElement(); // Audience
		xw.writeEndElement(); // AudienceRestrictionCondition
		xw.writeEndElement(); // Conditions

		xw.writeStartElement("", "AttributeStatement", "urn:oasis:names:tc:SAML:1.0:assertion");
		writeSubject(xw, user);
		writeAttributes(xw, user);
		xw.writeEndElement(); // AttributeStatement

		xw.writeStartElement("", "AuthenticationStatement ", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeAttribute("AuthenticationInstant", timeNow);
		xw.writeAttribute("AuthenticationMethod", "urn:oasis:names:tc:SAML:1.0:am:password");
		writeSubject(xw, user);
		xw.writeEndElement(); // AuthenticationStatement

		xw.writeEndElement(); // Assertion
	}

	private void writeSubject(XMLStreamWriter xw, User user) throws XMLStreamException {
		xw.writeStartElement("", "Subject", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeStartElement("", "NameIdentifier", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeCharacters(user.getUser());
		xw.writeEndElement(); // NameIdentifier
		xw.writeStartElement("", "SubjectConfirmation", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeStartElement("", "ConfirmationMethod", "urn:oasis:names:tc:SAML:1.0:assertion");
		xw.writeCharacters("urn:oasis:names:tc:SAML:1.0:cm:artifact");
		xw.writeEndElement(); // ConfirmationMethod
		xw.writeEndElement(); // SubjectConfirmation
		xw.writeEndElement(); // Subject
	}

	private void writeAttributes(XMLStreamWriter xw, User user) throws XMLStreamException {
		if (user != null && user.getAttributes() != null) {
			for (Map.Entry<String, String> entry : user.getAttributes().entrySet()) {
				xw.writeStartElement("", "Attribute ", "urn:oasis:names:tc:SAML:1.0:assertion");
				xw.writeAttribute("AttributeName", entry.getKey());
				xw.writeAttribute("AttributeNamespace", "http://www.ja-sig.org/products/cas");
				xw.writeStartElement("", "AttributeValue ", "urn:oasis:names:tc:SAML:1.0:assertion");
				xw.writeCharacters(entry.getValue());
				xw.writeEndElement(); // AttributeValue
				xw.writeEndElement(); // Attribute
			}
		}
	}

	@Override
	public void proxyValidate(Request request) {
		error(request, ErrorCodes.UNSUPPORTED_SAML_PROXY_VALIDATION);
	}

	@Override
	public void proxy(Request request) {
		error(request, ErrorCodes.UNSUPPORTED_SAML_PROXY_VALIDATION);
	}

	@Override
	protected void successProxy(Request request, String pgId) {
		error(request, ErrorCodes.UNSUPPORTED_SAML_PROXY_VALIDATION);
	}

	@Override
	protected void errorProxy(Request request, ErrorCodes invalidRequest) {
		error(request, ErrorCodes.UNSUPPORTED_SAML_PROXY_VALIDATION);
	}

	public long getAssertionValidityTimeMillis() {
		return assertionValidityTimeMillis;
	}

	public void setAssertionValidityTimeMillis(long assertionValidityTimeMillis) {
		this.assertionValidityTimeMillis = assertionValidityTimeMillis;
	}
}

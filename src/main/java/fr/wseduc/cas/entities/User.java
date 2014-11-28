package fr.wseduc.cas.entities;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

public class User {

	private String user;
	private Map<String, String> attributes;
	private List<Element> additionnalAttributes;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public List<Element> getAdditionnalAttributes() {
		return additionnalAttributes;
	}

	public void setAdditionnalAttributes(List<Element> additionnalAttributes) {
		this.additionnalAttributes = additionnalAttributes;
	}

}

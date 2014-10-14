package fr.wseduc.cas.entities;

import java.util.Map;

public class User {

	private String user;
	private Map<String, String> attributes;

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

}

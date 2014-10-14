package fr.wseduc.cas.entities;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

public class AuthCas {

	private String id;
	private LoginTicket loginTicket;
	private String user;
	private List<ServiceTicket> serviceTickets = new ArrayList<>();

	public void addServiceTicket(ServiceTicket serviceTicket) {
		if (serviceTickets == null) {
			serviceTickets = new ArrayList<>();
		}
		serviceTickets.add(serviceTicket);
	}

	public boolean isLoggedIn() {
		return user != null && !user.trim().isEmpty();
	}

	public ServiceTicket getServiceTicket(String ticket) {
		if (ticket == null) {
			return  null;
		}
		for (ServiceTicket st : serviceTickets) {
			if (ticket.equals(st.getTicket())) {
				return st;
			}
		}
		return null;
	}

	public List<ServiceTicket> getServiceTickets() {
		return serviceTickets;
	}

	public void setServiceTickets(List<ServiceTicket> serviceTickets) {
		this.serviceTickets = serviceTickets;
	}

	public LoginTicket getLoginTicket() {
		return loginTicket;
	}

	public void setLoginTicket(LoginTicket loginTicket) {
		this.loginTicket = loginTicket;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

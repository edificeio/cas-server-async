package fr.wseduc.cas.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthCas {

	private String id;
	private LoginTicket loginTicket;
	private String user;
	private Set<String> structureIds = new HashSet<>();

	/** For events **/
	private String userOsName;
	private String userOsVersion;
	private String userDeviceType;
	private String userDeviceName;
    private String userIp;
    private String userSessionId;
	private String userUa;

	private List<ServiceTicket> serviceTickets = new ArrayList<>();
	private boolean forceChangePassword = false;

	public Set<String> getStructureIds() {
		return structureIds;
	}

	public void setStructureIds(Set<String> structureIds) {
		this.structureIds = structureIds;
	}
	
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

	public ServiceTicket getServiceTicketByProxyTicket(String ticket) {
		if (ticket == null) {
			return  null;
		}
		for (ServiceTicket st : serviceTickets) {
			if (st.getPgt() != null && st.getPgt().getProxyTickets() != null) {
				for (ProxyTicket pt : st.getPgt().getProxyTickets()) {
					if (ticket.equals(pt.getPgId())) {
						return st;
					}
				}
			}
		}
		return null;
	}

	public ServiceTicket getServiceTicketByProxyGrantingTicket(String ticket) {
		if (ticket == null) {
			return  null;
		}
		for (ServiceTicket st : serviceTickets) {
			if (st.getPgt() != null && ticket.equals(st.getPgt().getPgtId())) {
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

	public boolean getForceChangePassword() {
		return forceChangePassword;
	}

	public void setForceChangePassword(boolean forceChangePassword) {
		this.forceChangePassword = forceChangePassword;
	}

    public String getUserOsName() {
        return userOsName;
    }

    public void setUserOsName(String userOsName) {
        this.userOsName = userOsName;
    }

    public String getUserOsVersion() {
        return userOsVersion;
    }

    public void setUserOsVersion(String userOsVersion) {
        this.userOsVersion = userOsVersion;
    }

    public String getUserDeviceType() {
        return userDeviceType;
    }

    public void setUserDeviceType(String userDeviceType) {
        this.userDeviceType = userDeviceType;
    }

    public String getUserDeviceName() {
        return userDeviceName;
    }

    public void setUserDeviceName(String userDeviceName) {
        this.userDeviceName = userDeviceName;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(String userSessionId) {
        this.userSessionId = userSessionId;
    }

    public String getUserUa() {
        return userUa;
    }

    public void setUserUa(String userUa) {
        this.userUa = userUa;
    }
}

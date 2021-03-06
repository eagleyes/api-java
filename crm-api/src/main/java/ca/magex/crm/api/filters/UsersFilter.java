package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class UsersFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Identifier organizationId;
	
	private Identifier personId;

	private Status status;

	private String username;

	private String role;

	public UsersFilter(Identifier organizationId, Identifier personId, Status status, String username, String role) {
		this.organizationId = organizationId;
		this.personId = personId;
		this.status = status;
		this.username = username;
		this.role = role;
	}

	public UsersFilter() {
		this(null, null, null, null, null);
	}
	
	public Identifier getPersonId() {
		return personId;
	}
	
	public UsersFilter withPersonId(Identifier personId) {
		return new UsersFilter(personId, personId, status, username, role);
	}

	public Identifier getOrganizationId() {
		return organizationId;
	}
	
	public UsersFilter withOrganizationId(Identifier organizationId) {
		return new UsersFilter(personId, personId, status, username, role);
	}

	public Status getStatus() {
		return status;
	}
	
	public UsersFilter withStatus(Status status) {
		return new UsersFilter(personId, personId, status, username, role);
	}
	
	public String getUsername() {
		return username;
	}
	
	public UsersFilter withUsername(String username) {
		return new UsersFilter(personId, personId, status, username, role);
	}

	public String getRole() {
		return role;
	}
	
	public UsersFilter withRole(String role) {
		return new UsersFilter(personId, personId, status, username, role);
	}

	public Comparator<User> getComparator(Paging paging) {
		return paging.new PagingComparator<User>();
	}

}

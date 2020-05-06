package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class UsersFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Identifier personId;

	private Identifier organizationId;

	private Status status;

	private String role;

	public UsersFilter(Identifier personId, Identifier organizationId, Status status, String role) {
		this.personId = personId;
		this.organizationId = organizationId;
		this.status = status;
		this.role = role;
	}

	public UsersFilter() {
		this(null, null, null, null);
	}
	
	public Identifier getPersonId() {
		return personId;
	}

	public Identifier getOrganizationId() {
		return organizationId;
	}

	public Status getStatus() {
		return status;
	}

	public String getRole() {
		return role;
	}

	public Comparator<PersonSummary> getComparator(Paging paging) {
		return paging.new PagingComparator<PersonSummary>();
	}

}
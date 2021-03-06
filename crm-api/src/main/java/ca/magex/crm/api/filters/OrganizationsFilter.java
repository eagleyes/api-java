package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.system.Status;

public class OrganizationsFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String displayName;

	private Status status;

	public OrganizationsFilter(String displayName, Status status) {
		this.displayName = displayName;
		this.status = status;
	}

	public OrganizationsFilter() {
		this(null, null);
	}

	public Status getStatus() {
		return status;
	}
	
	public OrganizationsFilter withStatus(Status status) {
		return new OrganizationsFilter(displayName, status);
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public OrganizationsFilter withDisplayName(String displayName) {
		return new OrganizationsFilter(displayName, status);
	}

	public Comparator<OrganizationSummary> getComparator(Paging paging) {
		return paging.new PagingComparator<OrganizationSummary>();		
	}
	
}
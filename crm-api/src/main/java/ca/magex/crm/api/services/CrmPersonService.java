package ca.magex.crm.api.services;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public interface CrmPersonService {

	PersonDetails createPerson(
		@NotNull Identifier organizationId, 
		@NotNull PersonName name, 
		@NotNull MailingAddress address, 
		@NotNull Communication communication, 
		@NotNull BusinessPosition position);

	PersonSummary enablePerson(
		@NotNull Identifier personId
	);

	PersonSummary disablePerson(
		@NotNull Identifier personId
	);

	PersonDetails updatePersonName(
		@NotNull Identifier personId, 
		@NotNull PersonName name
	);

	PersonDetails updatePersonAddress(
		@NotNull Identifier personId, 
		@NotNull MailingAddress address
	);

	PersonDetails updatePersonCommunication(
		@NotNull Identifier personId, 
		@NotNull Communication communication
	);

	PersonDetails updatePersonBusinessPosition(
		@NotNull Identifier personId, 
		@NotNull BusinessPosition position
	);

	PersonSummary findPersonSummary(
		@NotNull Identifier personId
	);

	PersonDetails findPersonDetails(
		@NotNull Identifier personId
	);

	long countPersons(
		@NotNull PersonsFilter filter
	);

	FilteredPage<PersonDetails> findPersonDetails(
		@NotNull PersonsFilter filter, 
		@NotNull Paging paging
	);

	FilteredPage<PersonSummary> findPersonSummaries(
		@NotNull PersonsFilter filter, 
		@NotNull Paging paging
	);
	
	default Page<PersonDetails> findPersonDetails(@NotNull PersonsFilter filter) {
		return findPersonDetails(filter, defaultPersonsPaging());
	}
	
	default Page<PersonSummary> findPersonSummaries(@NotNull PersonsFilter filter) {
		return findPersonSummaries(filter, defaultPersonsPaging());
	}
	
	default Page<PersonSummary> findActivePersonSummariesForOrg(@NotNull Identifier organizationId) {
		return findPersonSummaries(new PersonsFilter(organizationId, null, Status.ACTIVE), defaultPersonsPaging());
	}
	
	default PersonsFilter defaultPersonsFilter() {
		return new PersonsFilter();
	};
	
	default Paging defaultPersonsPaging() {
		return new Paging(SORT_OPTIONS.get(0));
	}
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("displayName")),
		Sort.by(Order.desc("displayName")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);
	
	default List<Sort> getPersonsSortOptions() {
		return SORT_OPTIONS;
	}
	
}
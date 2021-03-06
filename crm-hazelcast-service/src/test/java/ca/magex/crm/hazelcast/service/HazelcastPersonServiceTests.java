package ca.magex.crm.hazelcast.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPersonServiceTests {

	@Autowired private CrmPersonService hzPersonService;
	@Autowired private HazelcastInstance hzInstance;
	
	@MockBean private CrmOrganizationService organizationService;
	
	@Before
	public void reset() {
		hzInstance.getMap(HazelcastPersonService.HZ_PERSON_KEY).clear();
		Mockito.when(organizationService.findOrganizationSummary(new Identifier("BLIZZARD"))).thenReturn(Mockito.mock(OrganizationSummary.class));
	}

	@Test
	public void testPersons() {
		PersonName leroy = new PersonName("Mr", "Leroy", "MF", "Jenkins");
		MailingAddress eiffel = new MailingAddress("5 Avenue Anatole France", "Paris", "", "France", "75007");
		Communication comms = new Communication("Leader", "English", "leeroy@blizzard.com", new Telephone("555-9898"), "555-9797");
		BusinessPosition position = new BusinessPosition("IT", "Tester", "Junior");
		/* create */
		PersonDetails p1 = hzPersonService.createPerson(new Identifier("BLIZZARD"), leroy, eiffel, comms, position);
		Assert.assertEquals("Jenkins, Leroy MF", p1.getDisplayName());
		Assert.assertEquals(leroy, p1.getLegalName());
		Assert.assertEquals(eiffel, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(position, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, hzPersonService.findPersonDetails(p1.getPersonId()));
		
		hzPersonService.createPerson(
			new Identifier("BLIZZARD"), 
			new PersonName("Ms", "Tammy", "GD", "Jones"), 
			new MailingAddress("5 Avenue Anatole France", "Paris", "", "France", "75007"), 
			new Communication("Leader", "English", "leeroy@blizzard.com", new Telephone("555-9898"), "555-9797"), 
			new BusinessPosition("IT", "Tester", "Junior"));
		
		hzPersonService.createPerson(
			new Identifier("BLIZZARD"), 
			new PersonName("Mr", "James", "Earl", "Bond"), 
			new MailingAddress("5 Avenue Anatole France", "Paris", "", "France", "75007"), 
			new Communication("Leader", "English", "leeroy@blizzard.com", new Telephone("555-9898"), "555-9797"), 
			new BusinessPosition("IT", "Tester", "Junior"));
		
		/* update */
		PersonName tommy = new PersonName("Mrs", "Michelle", "Pauline", "Smith");
		p1 = hzPersonService.updatePersonName(p1.getPersonId(), tommy);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(eiffel, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(position, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, hzPersonService.findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, hzPersonService.updatePersonName(p1.getPersonId(), tommy));
		
		MailingAddress louvre = new MailingAddress("Rue de Rivoli", "Paris", "", "France", "75001");
		p1 = hzPersonService.updatePersonAddress(p1.getPersonId(), louvre);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(louvre, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(position, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, hzPersonService.findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, hzPersonService.updatePersonAddress(p1.getPersonId(), louvre));
		
		Communication comms2 = new Communication("Follower", "French", "follower@blizzard.com", new Telephone("666-9898"), "666-9797");
		p1 = hzPersonService.updatePersonCommunication(p1.getPersonId(), comms2);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(louvre, p1.getAddress());
		Assert.assertEquals(comms2, p1.getCommunication());
		Assert.assertEquals(position, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, hzPersonService.findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, hzPersonService.updatePersonCommunication(p1.getPersonId(), comms2));
		
		BusinessPosition position2 = new BusinessPosition("Legal", "Lawyer", "Senior");
		p1 = hzPersonService.updatePersonBusinessPosition(p1.getPersonId(), position2);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(louvre, p1.getAddress());
		Assert.assertEquals(comms2, p1.getCommunication());
		Assert.assertEquals(position2, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, hzPersonService.findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, hzPersonService.updatePersonBusinessPosition(p1.getPersonId(), position2));
		
		/* disable */
		PersonSummary ps1 = hzPersonService.disablePerson(p1.getPersonId());
		Assert.assertEquals("Smith, Michelle Pauline", ps1.getDisplayName());
		Assert.assertEquals(Status.INACTIVE, ps1.getStatus());
		Assert.assertEquals(ps1, hzPersonService.findPersonSummary(ps1.getPersonId()));
		Assert.assertEquals(ps1, hzPersonService.disablePerson(p1.getPersonId()));
		
		/* enable */
		ps1 = hzPersonService.enablePerson(p1.getPersonId());
		Assert.assertEquals("Smith, Michelle Pauline", ps1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, ps1.getStatus());
		Assert.assertEquals(ps1, hzPersonService.findPersonSummary(ps1.getPersonId()));
		Assert.assertEquals(ps1, hzPersonService.enablePerson(p1.getPersonId()));
		
		/* count */
		Assert.assertEquals(3, hzPersonService.countPersons(new PersonsFilter(null, null, null)));
		Assert.assertEquals(3, hzPersonService.countPersons(new PersonsFilter(new Identifier("BLIZZARD"), null, null)));
		Assert.assertEquals(3, hzPersonService.countPersons(new PersonsFilter(new Identifier("BLIZZARD"), null, Status.ACTIVE)));
		Assert.assertEquals(1, hzPersonService.countPersons(new PersonsFilter(new Identifier("BLIZZARD"), p1.getDisplayName(), Status.ACTIVE)));
		Assert.assertEquals(0, hzPersonService.countPersons(new PersonsFilter(new Identifier("BLIZZARD"), null, Status.INACTIVE)));
		Assert.assertEquals(0, hzPersonService.countPersons(new PersonsFilter(new Identifier("ACTIVISION"), "SpaceX", Status.INACTIVE)));
		
		/* find details */
		Page<PersonDetails> detailsPage = hzPersonService.findPersonDetails(
				new PersonsFilter(null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = hzPersonService.findPersonDetails(
				new PersonsFilter(new Identifier("BLIZZARD"), null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = hzPersonService.findPersonDetails(
				new PersonsFilter(new Identifier("BLIZZARD"), null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = hzPersonService.findPersonDetails(
				new PersonsFilter(new Identifier("BLIZZARD"), null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = hzPersonService.findPersonDetails(
				new PersonsFilter(new Identifier("BLIZZARD"), p1.getDisplayName(), Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		detailsPage = hzPersonService.findPersonDetails(
				new PersonsFilter(new Identifier("ACTIVISION"), "SpaceX", Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		/* find summaries */
		Page<PersonSummary> summariesPage = hzPersonService.findPersonSummaries(
				new PersonsFilter(null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = hzPersonService.findPersonSummaries(
				new PersonsFilter(new Identifier("BLIZZARD"), null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = hzPersonService.findPersonSummaries(
				new PersonsFilter(new Identifier("BLIZZARD"), null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = hzPersonService.findPersonSummaries(
				new PersonsFilter(new Identifier("BLIZZARD"), null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = hzPersonService.findPersonSummaries(
				new PersonsFilter(new Identifier("BLIZZARD"), p1.getDisplayName(), Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
		
		summariesPage = hzPersonService.findPersonSummaries(
				new PersonsFilter(new Identifier("ACTIVISION"), "SpaceX", Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
	}
	
	@Test
	public void testInvalidPersonId() {
		try {
			hzPersonService.findPersonDetails(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}

		try {
			hzPersonService.findPersonSummary(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}

		try {
			hzPersonService.updatePersonName(new Identifier("abc"), new PersonName("", "", "", ""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			hzPersonService.updatePersonAddress(new Identifier("abc"), new MailingAddress("", "", "", "", ""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			hzPersonService.updatePersonCommunication(new Identifier("abc"), new Communication("", "", "", new Telephone(""), ""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			hzPersonService.updatePersonBusinessPosition(new Identifier("abc"), new BusinessPosition("", "", ""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			hzPersonService.disablePerson(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			hzPersonService.enablePerson(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
	}
}
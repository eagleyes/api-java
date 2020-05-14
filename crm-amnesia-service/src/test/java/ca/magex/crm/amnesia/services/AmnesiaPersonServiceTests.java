package ca.magex.crm.amnesia.services;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.test.AbstractPersonServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaPersonServiceTests extends AbstractPersonServiceTests {

	@Autowired
	private AmnesiaDB db;

	@Autowired 
	private CrmPersonService personService;
	
	@Autowired 
	private CrmOrganizationService organizationService;
	
	@Before
	public void reset() {
		db.reset();
	}
	
	public CrmPersonService getPersonService() {
		return personService;
	}
	
	public CrmOrganizationService getOrganizationService() {
		return organizationService;
	}

}
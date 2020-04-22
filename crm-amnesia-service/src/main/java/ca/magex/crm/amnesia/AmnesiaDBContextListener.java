package ca.magex.crm.amnesia;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPasswordService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Lang;

@Component
public class AmnesiaDBContextListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(AmnesiaDBContextListener.class);

	@Autowired private CrmLookupService lookupService;
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmPersonService personService;
	@Autowired private CrmPasswordService passwordService;
	@Autowired(required = false) private PasswordEncoder passwordEncoder;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("Creating Magex Organization");
		OrganizationDetails magex = organizationService.createOrganization("Magex");
		Locale locale = Lang.ENGLISH;

		PersonDetails crmAdmin = personService.createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "Crm", "", "Admin"),
				new MailingAddress("123 Main Street", "Ottawa", "Ontario", lookupService.findCountryByCode("CA").getName(locale), "K1S 1B9"),
				new Communication("Crm Administrator", lookupService.findLanguageByCode("en").getName(locale), "crmadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
				new BusinessPosition(lookupService.findBusinessSectorByCode("4").getName(locale), lookupService.findBusinessUnitByCode("4").getName(locale), lookupService.findBusinessClassificationByCode("4").getName(locale)));
		personService.addUserRole(crmAdmin.getPersonId(), lookupService.findRoleByCode("CRM_ADMIN").getCode());
		passwordService.setPassword(crmAdmin.getPersonId(), passwordEncoder == null ? "admin" : passwordEncoder.encode("admin"));

		PersonDetails sysAdmin = personService.createPerson(
				magex.getOrganizationId(),
				new PersonName(null, "System", "", "Admin"),
				new MailingAddress("123 Main Street", "Ottawa", "Ontario", lookupService.findCountryByCode("CA").getName(locale), "K1S 1B9"),
				new Communication("System Administrator", lookupService.findLanguageByCode("en").getName(locale), "sysadmin@magex.ca", new Telephone("613-555-5555"), "613-555-5556"),
				new BusinessPosition(lookupService.findBusinessSectorByCode("4").getName(locale), lookupService.findBusinessUnitByCode("4").getName(locale), lookupService.findBusinessClassificationByCode("4").getName(locale)));
		personService.addUserRole(sysAdmin.getPersonId(), lookupService.findRoleByCode("SYS_ADMIN").getCode());
		passwordService.setPassword(sysAdmin.getPersonId(), passwordEncoder == null ? "sysadmin" : passwordEncoder.encode("sysadmin"));
	}
}

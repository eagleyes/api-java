package ca.magex.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import ca.magex.crm.amnesia.services.AmnesiaAnonymousPolicies;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.SecuredCrmServices;

@SpringBootApplication
@ComponentScan(basePackages = {"ca.magex.crm.amnesia", "ca.magex.crm.rest", "ca.magex.crm.spring.jwt", "ca.magex.crm.resource"})
public class JwtRestfulApiServer {

	public static void main(String[] args) {
		SpringApplication.run(JwtRestfulApiServer.class, args);
	}

	@Autowired private CrmLookupService lookupService;
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;
	
	@Bean
	public SecuredCrmServices organizations() {
		/* use anonymous policies */
		AmnesiaAnonymousPolicies policies = new AmnesiaAnonymousPolicies();
		return new SecuredCrmServices(
				lookupService, 
				organizationService, policies, 
				locationService, policies,
				personService, policies);
	}
}
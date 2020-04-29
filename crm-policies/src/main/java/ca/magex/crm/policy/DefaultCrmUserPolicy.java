package ca.magex.crm.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Profile(value = {
		MagexCrmProfiles.CRM_NO_AUTH_EMBEDDED,
		MagexCrmProfiles.CRM_NO_AUTH_REMOTE
})
public class DefaultCrmUserPolicy implements CrmUserPolicy {

	@Autowired private CrmUserService userService;
	@Autowired private CrmPersonService personService;

	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		try {
			personService.findPersonSummary(personId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		try {
			userService.findUserById(userId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		try {
			userService.findUserById(userId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		try {
			userService.findUserById(userId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}
}
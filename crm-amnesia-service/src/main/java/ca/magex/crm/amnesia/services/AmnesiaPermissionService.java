package ca.magex.crm.amnesia.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaPermissionService implements CrmPermissionService {
	
	private AmnesiaDB db;
	
	public AmnesiaPermissionService(AmnesiaDB db) {
		this.db = db;
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		List<Group> allMatchingGroups = db.findByType(Group.class).collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingGroups, paging);
	}

	@Override
	public Group findGroup(Identifier groupId) {
		return db.findById(groupId, Group.class);
	}

	@Override
	public Group findGroupByCode(String code) {
		return db.findGroupByCode(code);
	}

	@Override
	public Group createGroup(String code, Localized name) {
		return db.saveGroup(new Group(db.generateId(), code, Status.ACTIVE, name));
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		return db.saveGroup(db.findGroup(groupId).withName(name));
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		return db.saveGroup(findGroup(groupId).withStatus(Status.ACTIVE));
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		return db.saveGroup(findGroup(groupId).withStatus(Status.INACTIVE));
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		List<Role> allRoles = db.findByType(Role.class)
			.filter(r -> r.getGroupId().equals(filter.getGroupId()))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allRoles, paging);
	}

	@Override
	public Role findRole(Identifier roleId) {
		return db.findById(roleId, Role.class);
	}

	@Override
	public Role findRoleByCode(String code) {
		return db.findRoleByCode(code);
	}

	@Override
	public Role createRole(Identifier groupId, String code, Localized name) {
		return db.saveRole(new Role(db.generateId(), groupId, code, Status.ACTIVE, name));
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		return db.saveRole(db.findRole(roleId).withName(name));
	}

	@Override
	public Role enableRole(Identifier roleId) {
		return db.saveRole(findRole(roleId).withStatus(Status.ACTIVE));
	}

	@Override
	public Role disableRole(Identifier roleId) {
		return db.saveRole(findRole(roleId).withStatus(Status.INACTIVE));
	}

}
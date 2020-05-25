package ca.magex.crm.restful.controllers;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;

@Controller
public class PermissionsController extends AbstractCrmController {

	@GetMapping("/api/groups")
	public void findGroups(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, (messages, transformer) -> { 
			return createPage(crm.findGroups(
				extractGroupsFilter(extractLocale(req), req), 
				extractPaging(GroupsFilter.getDefaultPaging(), req)), 
				e -> transformer.formatGroup(e));
		});
	}
	
	private GroupsFilter extractGroupsFilter(Locale locale, HttpServletRequest req) throws BadRequestException {
		Status status = req.getParameter("status") == null ? null : crm.findStatusByLocalizedName(locale, req.getParameter("status"));
		if (req.getParameter("name") != null) {
			String name = req.getParameter("name");
			if (locale.equals(Lang.ENGLISH)) {
				return new GroupsFilter(name, null, null, status);
			} else if (locale.equals(Lang.FRENCH)) {
				return new GroupsFilter(null, name, null, status);
			} else {
				return new GroupsFilter(null, null, name, status);
			}
		} else {
			String englishName = req.getParameter("englishName") == null ? null : req.getParameter("englishName");
			String frenchName = req.getParameter("frenchName") == null ? null : req.getParameter("frenchName");
			String code = req.getParameter("code") == null ? null : req.getParameter("code");
			return new GroupsFilter(englishName, frenchName, code, status);
		}
	}

	@PostMapping("/api/groups")
	public void createGroup(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, (messages, transformer) -> {
			JsonObject body = extractBody(req);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			return transformer.formatGroup(crm.createGroup(name));
		});
	}

	@GetMapping("/api/groups/{groupId}")
	public void getGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") String groupId) throws IOException {
		handle(req, res, (messages, transformer) -> {
			return transformer.formatGroup(crm.findGroup(new Identifier(groupId)));
		});
	}

	@PatchMapping("/api/groups/{groupId}")
	public void updateGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") String groupId) throws IOException {
		handle(req, res, (messages, transformer) -> {
			JsonObject body = extractBody(req);
			String code = getString(body, "code", "", null, messages);
			String englishName = getString(body, "englishName", "", null, messages);
			String frenchName = getString(body, "frenchName", "", null, messages);
			Localized name = new Localized(code, englishName, frenchName);
			validate(messages);
			crm.updateGroupName(new Identifier(groupId), name);
			return transformer.formatGroup(crm.findGroup(new Identifier(groupId)));
		});
	}

	@PutMapping("/api/groups/{groupId}/enable")
	public void enableGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") String groupId) throws IOException {
		handle(req, res, (messages, transformer) -> {
			confirm(extractBody(req), new Identifier(groupId), messages);
			crm.enableGroup(new Identifier(groupId));
			return transformer.formatGroup(crm.findGroup(new Identifier(groupId)));
		});
	}

	@PutMapping("/api/groups/{groupId}/disable")
	public void disableGroup(HttpServletRequest req, HttpServletResponse res, 
			@PathVariable("groupId") String groupId) throws IOException {
		handle(req, res, (messages, transformer) -> {
			confirm(extractBody(req), new Identifier(groupId), messages);
			crm.disableGroup(new Identifier(groupId));
			return transformer.formatGroup(crm.findGroup(new Identifier(groupId)));
		});
	}

}
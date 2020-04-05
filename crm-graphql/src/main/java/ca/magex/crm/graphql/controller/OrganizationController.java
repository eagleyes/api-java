package ca.magex.crm.graphql.controller;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.magex.crm.graphql.service.GraphQLOrganizationsService;
import graphql.ExecutionResult;
import graphql.servlet.internal.GraphQLRequest;

@RequestMapping("/graphql")
@RestController
public class OrganizationController {
	private static Logger logger = LoggerFactory.getLogger(OrganizationController.class);

	@Autowired
	@Qualifier("graphQLOrganizationService")
	private GraphQLOrganizationsService graphQLService;

	@PostMapping
	public ResponseEntity<Object> doQuery(@RequestBody GraphQLRequest request, HttpServletRequest req) throws JSONException {
		logger.info("Entering doQuery@" + getClass().getSimpleName());
		ExecutionResult result = graphQLService.getGraphQL().execute(request.getQuery());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
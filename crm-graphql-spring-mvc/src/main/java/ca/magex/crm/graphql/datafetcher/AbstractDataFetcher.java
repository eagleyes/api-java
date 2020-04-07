package ca.magex.crm.graphql.datafetcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.graphql.util.PagingBuilder;
import graphql.schema.DataFetchingEnvironment;

/**
 * Base class for the data fetchers with any common data
 * 
 * @author Jonny
 *
 */
public abstract class AbstractDataFetcher {

	protected Properties languagesLookup = new Properties();
	protected Properties sectorsLookup = new Properties();
	protected Properties unitsLookup = new Properties();
	protected Properties classificationsLookup = new Properties();

	protected Crm crm = null;

	protected AbstractDataFetcher(Crm crm) {
		this.crm = crm;
		
		URL languages = getClass().getResource("/codes/languages.properties");
		try (InputStream c = languages.openStream()) {
			this.languagesLookup.load(c);
		} catch (IOException ioe) {
			throw new RuntimeException("Error loading languages.properties");
		}
		
		URL sectors = getClass().getResource("/codes/sectors.properties");
		try (InputStream c = sectors.openStream()) {
			this.sectorsLookup.load(c);
		} catch (IOException ioe) {
			throw new RuntimeException("Error loading sectors.properties");
		}
		
		URL units = getClass().getResource("/codes/units.properties");
		try (InputStream c = units.openStream()) {
			this.unitsLookup.load(c);
		} catch (IOException ioe) {
			throw new RuntimeException("Error loading units.properties");
		}
		
		URL classifications = getClass().getResource("/codes/classifications.properties");
		try (InputStream c = classifications.openStream()) {
			this.classificationsLookup.load(c);
		} catch (IOException ioe) {
			throw new RuntimeException("Error loading classifications.properties");
		}
	}
	
	/**
	 * extracts the filter from the environment
	 * @param environment
	 * @return
	 */
	protected Map<String,Object> extractFilter(DataFetchingEnvironment environment) {
		return environment.getArgument("filter");
	}

	/**
	 * extracts the paging input from the environment
	 * 
	 * @param environment
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Paging extractPaging(DataFetchingEnvironment environment) {
		Map<String, Object> pagingMap = environment.getArgument("paging");
		List<String> sortFields = (List<String>) pagingMap.get("sortField");
		List<String> sortOrders = (List<String>) pagingMap.get("sortOrder");

		return new PagingBuilder()
				.withPageNumber((Integer) pagingMap.get("pageNumber"))
				.withPageSize((Integer) pagingMap.get("pageSize"))
				.withSortFields(sortFields)
				.withSortDirections(sortOrders)
				.build();
	}

	/**
	 * extracts the mailing address from the environment
	 * @param environment
	 * @param addressKey
	 * @return
	 */
	protected MailingAddress extractMailingAddress(DataFetchingEnvironment environment, String addressKey) {
		Map<String,Object> addressMap = environment.getArgument(addressKey);
		return new MailingAddress(
				(String) addressMap.get("street"), 
				(String) addressMap.get("city"), 
				(String) addressMap.get("province"), 
				crm.findCountryByCode((String) addressMap.get("countryCode")),
				(String) addressMap.get("postalCode"));
	}
	
	/**
	 * extracts the person name from the environment
	 * @param environment
	 * @param nameKey
	 * @return
	 */
	protected PersonName extractPersonName(DataFetchingEnvironment environment, String nameKey) {
		Map<String,Object> nameMap = environment.getArgument(nameKey);
		return new PersonName(
				crm.findSalutationByCode((Integer) nameMap.get("salutation")),
				(String) nameMap.get("firstName"),
				(String) nameMap.get("middleName"),
				(String) nameMap.get("lastName"));
	}
	
	/**
	 * extracts the communications from the environment
	 * @param environment
	 * @param commsKey
	 * @return
	 */
	protected Communication extractCommunication(DataFetchingEnvironment environment, String commsKey) {
		Map<String,Object> commsMap = environment.getArgument(commsKey);
		return new Communication(
				(String) commsMap.get("jobTitle"), 
				new Language(
						(String) commsMap.get("language"),
						languagesLookup.getProperty((String) commsMap.get("language"))),
				(String) commsMap.get("email"), 
				new Telephone(
						(String) commsMap.get("phoneNumber"),
						(String) commsMap.get("phoneExtension")),
				(String) commsMap.get("faxNumber"));
	}
	
	/**
	 * Extracts the business position from the environment
	 * @param environment
	 * @param businessKey
	 * @return
	 */
	protected BusinessPosition extractBusinessPosition(DataFetchingEnvironment environment, String businessKey) {
		Map<String,Object> businessMap = environment.getArgument(businessKey);
		
		return new BusinessPosition(
				new BusinessSector(
						(Integer) businessMap.get("sector"), 
						sectorsLookup.getProperty(businessMap.get("sector").toString())), 
				new BusinessUnit(
						(Integer) businessMap.get("unit"), 
						unitsLookup.getProperty(businessMap.get("unit").toString())),
				new BusinessClassification(
						(Integer) businessMap.get("classification"), 
						classificationsLookup.getProperty(businessMap.get("classification").toString())));
	}
	
	/**
	 * Extracts the role from the environment
	 * @param environment
	 * @param businessKey
	 * @return
	 */
	protected Role extractRole(DataFetchingEnvironment environment, String roleKey) {
		return crm.findRoleByCode(environment.getArgument(roleKey));
	}
	
	/**
	 * Extracts the role from the environment
	 * @param environment
	 * @param businessKey
	 * @return
	 */
	protected List<Role> extractRoles(DataFetchingEnvironment environment, String roleKey) {
		List<String> roles = environment.getArgument(roleKey);
		return roles.stream().map(crm::findRoleByCode).collect(Collectors.toList());		
	}
}
package ca.magex.crm.api.common;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;

public class BusinessPosition {

	private BusinessSector sector;
	
	private BusinessUnit unit;
	
	private BusinessClassification classification;

	public BusinessPosition(BusinessSector sector, BusinessUnit unit, BusinessClassification classification) {
		super();
		this.sector = sector;
		this.unit = unit;
		this.classification = classification;
	}

	public BusinessSector getSector() {
		return sector;
	}

	public BusinessPosition withSector(BusinessSector sector) {
		return new BusinessPosition(sector, unit, classification);
	}

	public BusinessUnit getUnit() {
		return unit;
	}

	public BusinessPosition withUnit(BusinessUnit unit) {
		return new BusinessPosition(sector, unit, classification);
	}

	public BusinessClassification getClassification() {
		return classification;
	}

	public BusinessPosition withClassification(BusinessClassification classification) {
		return new BusinessPosition(sector, unit, classification);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
}
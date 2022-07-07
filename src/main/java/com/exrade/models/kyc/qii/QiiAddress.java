package com.exrade.models.kyc.qii;

import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.runtime.kyc.qii.DateStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class QiiAddress extends BaseEntityUUIDTimeStampable {
    private QiiActualityType actualityType;
    private String city;
    private String country;
    private String function; //function2
    private String houseNumber;
    private String municipality;
    private String postalCode;
    private String startDate;
    private String street;
    private String suffix;

	public QiiActualityType getActualityType() {
		return actualityType;
	}
	public void setActualityType(QiiActualityType actualityType) {
		this.actualityType = actualityType;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	public String getMunicipality() {
		return municipality;
	}
	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@JsonSerialize(using = DateStringSerializer.class, as=String.class)
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}

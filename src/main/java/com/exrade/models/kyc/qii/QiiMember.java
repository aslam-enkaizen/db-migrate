package com.exrade.models.kyc.qii;

import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.runtime.kyc.qii.DateStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QiiMember extends BaseEntityUUIDTimeStampable {

	private QiiAddress address;
	private List<QiiAddress> addresses = new ArrayList<QiiAddress>();
	private String dateOfBirth;
	private String email;
	private int equity;
	private String firstName;
	private QiiGender gender;
	private double income;
	private QiiIncome incomeHistory;
	private QiiIncomeType incomeType;
	private String initials;
	private String lastName;
	private Map<String, String> links = new HashMap<String, String>();
	private QiiMaritialStatus maritalStatus;
	private int memberId;
	private int numberOfVerifiedChildren;
	private String placeOfBirth;
	private String phoneCode;
	private String phoneNumber;
	private List<QiiStatement> statements = new ArrayList<QiiStatement>();
	private QiiMemberType type;
	private List<QiiVerifiedIdentityDocument> verifiedIdentityDocuments = new ArrayList<QiiVerifiedIdentityDocument>();

	public QiiAddress getAddress() {
		return address;
	}
	public void setAddress(QiiAddress address) {
		this.address = address;
	}
	public List<QiiAddress> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<QiiAddress> addresses) {
		this.addresses = addresses;
	}

	@JsonSerialize(using = DateStringSerializer.class, as=String.class)
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getEquity() {
		return equity;
	}
	public void setEquity(int equity) {
		this.equity = equity;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public QiiGender getGender() {
		return gender;
	}
	public void setGender(QiiGender gender) {
		this.gender = gender;
	}
	public double getIncome() {
		return income;
	}
	public void setIncome(double income) {
		this.income = income;
	}
	public QiiIncome getIncomeHistory() {
		return incomeHistory;
	}
	public void setIncomeHistory(QiiIncome incomeHistory) {
		this.incomeHistory = incomeHistory;
	}
	public QiiIncomeType getIncomeType() {
		return incomeType;
	}
	public void setIncomeType(QiiIncomeType incomeType) {
		this.incomeType = incomeType;
	}
	public String getInitials() {
		return initials;
	}
	public void setInitials(String initials) {
		this.initials = initials;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Map<String, String> getLinks() {
		return links;
	}
	public void setLinks(Map<String, String> links) {
		this.links = links;
	}
	public QiiMaritialStatus getMaritalStatus() {
		return maritalStatus;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public void setMaritalStatus(QiiMaritialStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public int getNumberOfVerifiedChildren() {
		return numberOfVerifiedChildren;
	}
	public void setNumberOfVerifiedChildren(int numberOfVerifiedChildren) {
		this.numberOfVerifiedChildren = numberOfVerifiedChildren;
	}
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	public String getPhoneCode() {
		return phoneCode;
	}
	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public List<QiiStatement> getStatements() {
		return statements;
	}
	public void setStatements(List<QiiStatement> statements) {
		this.statements = statements;
	}
	public QiiMemberType getType() {
		return type;
	}
	public void setType(QiiMemberType type) {
		this.type = type;
	}
	public List<QiiVerifiedIdentityDocument> getVerifiedIdentityDocuments() {
		return verifiedIdentityDocuments;
	}
	public void setVerifiedIdentityDocuments(List<QiiVerifiedIdentityDocument> verifiedIdentityDocuments) {
		this.verifiedIdentityDocuments = verifiedIdentityDocuments;
	}

}

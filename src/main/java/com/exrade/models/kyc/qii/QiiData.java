package com.exrade.models.kyc.qii;

import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.ArrayList;
import java.util.List;

public class QiiData extends BaseEntityUUIDTimeStampable {
	private String householdId;
	private Membership membership;
	private Negotiation negotiation;
	private String requestManagementId;
	private String requestManagementName;
	private String requestManagementLinkName;
	private String requestManagementRedirect;

	private List<QiiMember> members = new ArrayList<QiiMember>();
	// $filter=householdId eq 11513
	//private String members; // first name, last name, type (owner, cohabitant, guarantar), gender, date of birth, place of birth, email, phone number, income, street, house number, post code, city, municipality, actuality type, function, marital status, no of children

	// $filter=memberId eq 39481
	//private String MemberDocuments; // fileName

	// $filter=memberId eq 39481
	//private String MemberVerifiedIdentificationDocument; // document number, expiration date, type
	//private String MemberAddressHistory; //	actuality type (previous, present etc.), street, house number, post code, city, municipality, country, start date
	//private String MemberVerifiedAsset; // ??
	//private String MemberOwnStatements; // question, answer
	//private String MemberProfiles; // email, phone number, income type, equity

	public QiiData() {}

	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

	public String getMembershipUUID() {
		return getMembership() != null ? getMembership().getIdentifier() : null;
	}

	public Negotiation getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(Negotiation negotiation) {
		this.negotiation = negotiation;
	}

	public String getNegotiationUUID() {
		return getNegotiation() != null ? getNegotiation().getUuid() : null;
	}

	public String getRequestManagementLinkName() {
		return requestManagementLinkName;
	}

	public void setRequestManagementLinkName(String requestManagementLinkName) {
		this.requestManagementLinkName = requestManagementLinkName;
	}

	public String getRequestManagementId() {
		return requestManagementId;
	}

	public void setRequestManagementId(String requestManagementId) {
		this.requestManagementId = requestManagementId;
	}

	public String getHouseholdId() {
		return householdId;
	}

	public void setHouseholdId(String householdId) {
		this.householdId = householdId;
	}

	public String getRequestManagementName() {
		return requestManagementName;
	}

	public void setRequestManagementName(String requestManagementName) {
		this.requestManagementName = requestManagementName;
	}

	public String getRequestManagementRedirect() {
		return requestManagementRedirect;
	}

	public void setRequestManagementRedirect(String requestManagementRedirect) {
		this.requestManagementRedirect = requestManagementRedirect;
	}

	public List<QiiMember> getMembers() {
		return members;
	}

	public void setMembers(List<QiiMember> members) {
		this.members = members;
	}
}

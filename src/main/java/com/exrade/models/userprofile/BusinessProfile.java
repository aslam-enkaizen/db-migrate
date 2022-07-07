package com.exrade.models.userprofile;

import com.exrade.runtime.rest.RestParameters.ProfileFields;
import com.exrade.util.ObjectsUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Deprecated
public class BusinessProfile extends Profile implements IBusinessProfile {
	
	public BusinessProfile() {
	}
	
	public BusinessProfile(String iName) {
		super();
		// by default business profile will be searchable
		publicProfile = true;
		name = iName;
	}
	
	/**
	 * Convert a personal profile to a business profile 
	 * @param profile
	 * @param name
	 * @param planSubscription
	 * @return
	 */
	public static BusinessProfile create(Profile profile,String name){
		BusinessProfile businessProfile = new BusinessProfile();
		ObjectsUtil.bindFields(profile, businessProfile, Arrays.asList(ProfileFields.PLAN_SUBSCRIPTION));
		businessProfile.setName(name);
		return businessProfile;
	}
	
	private String name;

	private String logo;
	
	private String vat;
	
	private String nace;

	private String competences;
	
	private boolean domainVerified;
	
	private boolean identityVerified;
	
	private String legalEmail;
	
	private String subdomain;
	
	private String agreementTemplate;

	private String video;
	
	private Map<String, Object> customFields = new HashMap<>();
	
	public String getCompetences() {
		return competences;
	}

	public void setCompetences(String competences) {
		this.competences = competences;
	}

	public String getVat() {
		return vat;
	}

	public void setVat(String vat) {
		this.vat = vat;
	}

	public String getNace() {
		return nace;
	}

	public void setNace(String nace) {
		this.nace = nace;
	}

	public boolean isDomainVerified() {
		return domainVerified;
	}

	public void setDomainVerified(boolean domainVerified) {
		this.domainVerified = domainVerified;
	}
	
	public boolean isIdentityVerified() {
		return identityVerified;
	}
	
	public void setIdentityVerified(boolean identityVerified) {
		this.identityVerified = identityVerified;
	}

	@Override
	public String getLegalEmail() {
		return legalEmail;
	}

	public void setLegalEmail(String legalEmail) {
		this.legalEmail = legalEmail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getAgreementTemplate() {
		return agreementTemplate;
	}

	public void setAgreementTemplate(String agreementTemplate) {
		this.agreementTemplate = agreementTemplate;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}
	
	@Override
	public Map<String, Object> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, Object> customFields) {
		this.customFields = customFields;
	}

	@Override
	public String getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}
}

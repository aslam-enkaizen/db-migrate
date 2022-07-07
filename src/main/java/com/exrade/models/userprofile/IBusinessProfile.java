package com.exrade.models.userprofile;

public interface IBusinessProfile extends IProfile {

	public String getName();
	
	public String getLogo();
	
	public String getCompetences();

	public String getVat();

	public String getNace();

	public String getLegalEmail();
	
	public boolean isDomainVerified();

	public boolean isIdentityVerified();
	
	public String getAgreementTemplate();
	
	public String getSubdomain();
	
}
package com.exrade.models.userprofile;

import com.exrade.models.Subject;
import com.exrade.platform.persistence.IEntity;

public interface Negotiator extends Subject,IEntity  {
	
	public String getId();
	
	public boolean isPublicProfile();
	
	public String getName();
	
	public IProfile getProfile();

	public IUser getUser();
	
	public IPlan getPlan();

	boolean isActive();
	
	boolean isProfileActive();
	
	boolean isAgreementSigner();
	
	boolean isGuest();
	
	Double getMaxNegotiationAmount();
	
}

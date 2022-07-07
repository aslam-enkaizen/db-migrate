package com.exrade.runtime.authorisation;

import com.exrade.models.authorisation.AuthorisationRequest;
import com.exrade.models.authorisation.AuthorisationStatus;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IAuthorisationManager {
	
	public AuthorisationRequest createAuthorisationRequest(AuthorisationRequest iRequest);
	
	public AuthorisationRequest updateAuthorisationRequest(String iRequestUUID, Negotiator iResponder, AuthorisationStatus iStatus, String iNote);
	
	public AuthorisationRequest getAuthorisationRequestByUUID(String iRequestUUID);
	
	public void deleteAuthorisationRequest(String iRequestUUID);
	
	public List<AuthorisationRequest> listAuthorisationRequests(QueryFilters iFilters);
	
	public boolean hasPendingAuthorisation(String objectID);
}

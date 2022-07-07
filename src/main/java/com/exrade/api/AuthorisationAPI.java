package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.authorisation.AuthorisationRequest;
import com.exrade.models.authorisation.AuthorisationStatus;
import com.exrade.models.userprofile.Negotiator;

import java.util.List;
import java.util.Map;

public interface AuthorisationAPI {

	AuthorisationRequest createAuthorisationRequest(ExRequestEnvelope request, AuthorisationRequest iAuthorisationRequest);
	
	void deleteAuthorisationRequest(ExRequestEnvelope request, String iAuthorisationRequestUUID);
	
	AuthorisationRequest updateAuthorisationRequest(ExRequestEnvelope request, String iRequestUUID,
			Negotiator iResponder, AuthorisationStatus iStatus, String iNote);
	
	List<AuthorisationRequest> listAuthorisationRequest(ExRequestEnvelope request, Map<String, String> iFilters);
	
	AuthorisationRequest getAuthorisationRequestByUUID(ExRequestEnvelope request, String iAuthorisationRequestUUID);
}

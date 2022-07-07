package com.exrade.api.impl;

import com.exrade.api.AuthorisationAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.authorisation.AuthorisationRequest;
import com.exrade.models.authorisation.AuthorisationStatus;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.authorisation.AuthorisationManager;
import com.exrade.runtime.authorisation.IAuthorisationManager;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.AuthorisationFields;
import com.exrade.runtime.rest.RestParameters.AuthorisationFilters;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;

public class AuthorisationManagerAdapter implements AuthorisationAPI {

	private IAuthorisationManager manager = new AuthorisationManager();

	@Override
	public AuthorisationRequest createAuthorisationRequest(ExRequestEnvelope request,
			AuthorisationRequest iAuthorisationRequest) {
		ContextHelper.initContext(request);
		if(iAuthorisationRequest.getSender() == null)
			iAuthorisationRequest.setSender(ContextHelper.getMembership());
		return manager.createAuthorisationRequest(iAuthorisationRequest);
	}

	@Override
	public void deleteAuthorisationRequest(ExRequestEnvelope request,
			String iAuthorisationRequestUUID) {
		ContextHelper.initContext(request);
		manager.deleteAuthorisationRequest(iAuthorisationRequestUUID);
	}

	@Override
	public AuthorisationRequest updateAuthorisationRequest(ExRequestEnvelope request,
			String iRequestUUID, Negotiator iResponder, AuthorisationStatus iStatus, String iNote) {
		ContextHelper.initContext(request);
		return manager.updateAuthorisationRequest(iRequestUUID, iResponder, iStatus, iNote);
	}

	@Override
	public List<AuthorisationRequest> listAuthorisationRequest(
			ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);

		if (Strings.isNullOrEmpty(iFilters.get(AuthorisationFilters.REQUEST_TYPE))) {
			filters.putIfNotNull(AuthorisationFilters.REQUEST_TYPE, "all");
		}
		else {
			filters.putIfNotNull(AuthorisationFilters.REQUEST_TYPE, iFilters.get(AuthorisationFilters.REQUEST_TYPE));
		}

		filters.putIfNotNull(AuthorisationFields.STATUS, iFilters.get(AuthorisationFields.STATUS));
		filters.putIfNotNull(AuthorisationFilters.SENDER_UUID, iFilters.get(AuthorisationFilters.SENDER_UUID));
		filters.putIfNotNull(AuthorisationFields.OBJECTID, iFilters.get(AuthorisationFields.OBJECTID));
		filters.putIfNotNull(AuthorisationFields.OBJECTTYPE, iFilters.get(AuthorisationFields.OBJECTTYPE));
		filters.putIfNotNull(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID, iFilters.get(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID));
		filters.put("sender.profile.uuid", ContextHelper.getMembership().getProfile().getUuid());

		if (filters.isNullOrEmpty(QueryParameters.SORT)){
			filters.put(QueryParameters.SORT, OrientSqlBuilder.DESC_SORT + RestParameters.CREATION_DATE);
		}
		return manager.listAuthorisationRequests(filters);
	}

	@Override
	public AuthorisationRequest getAuthorisationRequestByUUID(
			ExRequestEnvelope request, String iAuthorisationRequestUUID) {
		ContextHelper.initContext(request);
		AuthorisationRequest authorisationRequest = manager.getAuthorisationRequestByUUID(iAuthorisationRequestUUID);

		Security.checkAuthorisationRequestAccess(authorisationRequest, ContextHelper.getMembership());

		return authorisationRequest;
	}

}

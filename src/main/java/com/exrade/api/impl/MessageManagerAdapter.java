package com.exrade.api.impl;

import com.exrade.api.MessageAPI;
import com.exrade.core.ExLogger;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.informationmodel.Attribute;
import com.exrade.models.messaging.Information;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ExConcurrentModificationException;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.message.IMessageManager;
import com.exrade.runtime.message.MessageManager;
import com.exrade.runtime.message.persistence.MessageBoxQuery.MessageBoxQFilters;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.InformationMessageFilters;
import com.exrade.runtime.rest.RestParameters.MessageFields;
import com.exrade.runtime.rest.RestParameters.MessageFilters;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;

public class MessageManagerAdapter implements MessageAPI {

	private IMessageManager manager = new MessageManager();

	@Override
	public NegotiationMessage readByUUID(ExRequestEnvelope request, String iMessageUUID) {
		ContextHelper.initContext(request);
		return manager.readByUUID(iMessageUUID);
	}

	@Override
	public List<Information> listMessages(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);

		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putIfNotNull(MessageFields.MESSAGE_TYPE, Information.class.getSimpleName());
		filters.put(MessageBoxQFilters.NEGOTIATION_UUID, iFilters.get(MessageBoxQFilters.NEGOTIATION_UUID));
		filters.put(InformationMessageFilters.PARTNER_UUID, iFilters.get(InformationMessageFilters.PARTNER_UUID));

		if(!Strings.isNullOrEmpty(iFilters.get(QueryParameters.SORT)))
			filters.put(QueryParameters.SORT, OrientSqlBuilder.DESC_SORT+MessageFields.MESSAGE_CREATION_TIME);

		filters.put(MessageBoxQFilters.SENT, true);
		filters.put(MessageBoxQFilters.RECEIVED, true);

		if(!Strings.isNullOrEmpty(iFilters.get(MessageFilters.STATUS))){
			if(iFilters.get(MessageFilters.STATUS).equalsIgnoreCase(InformationMessageFilters.SENT)){
				filters.put(MessageBoxQFilters.SENT, false);
			}
			else if(iFilters.get(MessageFilters.STATUS).equalsIgnoreCase(InformationMessageFilters.RECEIVED)){
				filters.put(MessageBoxQFilters.RECEIVED, false);
			}
		}

		INegotiationManager negotiationManager = new NegotiationManager();
		Negotiation negotiation = negotiationManager.getNegotiation(iFilters.get(MessageBoxQFilters.NEGOTIATION_UUID));
		Negotiator involvedNegotiator = negotiationManager.getInvolvedNegotiator(negotiation, ContextHelper.getMembership());

		if(involvedNegotiator != null){
			filters.put(MessageBoxQFilters.SENDER_UUID, involvedNegotiator.getIdentifier());
			filters.put(MessageBoxQFilters.RECEIVER_UUID, involvedNegotiator.getIdentifier());
		}

		return manager.listMessages(filters);
	}

	@Override
	public NegotiationMessage getMessageDraft(ExRequestEnvelope request,
			String iNegotiationID) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		return manager.getMessageDraft(iNegotiationID);
	}

	@Override
	public boolean updateMessageDraft(ExRequestEnvelope request, String iNegotiationUUID, String iMessageUUID,
			List<Attribute> items, String template) {
		ContextHelper.initContext(request);

		boolean success = false;
		boolean updateMessageDraftSuccess = false;

		for(int retryCount = 0; retryCount < RestParameters.MAX_RETRY; retryCount++) {
			try {
				updateMessageDraftSuccess = manager.updateMessageDraft(iNegotiationUUID, iMessageUUID, items, template);
				success = true;
				break;
			}
			catch(ExConcurrentModificationException ex) {
				ExLogger.get().error("Concurrent modification error!", ex);
			}
		}

		if(!success)
			throw new ExException("Failed to complete the action. Please try again");

		return updateMessageDraftSuccess;
	}

	@Override
	public void updateEvaluableMessage(ExRequestEnvelope requestEnvelope, String negotiationUUID, String messageUUID,
			String template) {
		ContextHelper.initContext(requestEnvelope);
		manager.updateEvaluableMessage(negotiationUUID, messageUUID, template);
		
	}



}

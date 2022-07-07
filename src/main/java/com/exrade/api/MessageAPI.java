package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.informationmodel.Attribute;
import com.exrade.models.messaging.Information;
import com.exrade.models.messaging.NegotiationMessage;

import java.util.List;
import java.util.Map;

public interface MessageAPI {

	NegotiationMessage readByUUID(ExRequestEnvelope request, String iMessageUUID);

	List<Information> listMessages(ExRequestEnvelope request, Map<String, String> iFilters);

	NegotiationMessage getMessageDraft(ExRequestEnvelope request,
			String iNegotiationID);

	boolean updateMessageDraft(ExRequestEnvelope request, String iNegotiationUUID,
			String iMessageUUID, List<Attribute> items, String template);

	void updateEvaluableMessage(ExRequestEnvelope requestEnvelope, String negotiationUUID, String messageUUID,
			String asText);
	
}

package com.exrade.runtime.message;

import com.exrade.models.informationmodel.Attribute;
import com.exrade.models.messaging.Information;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IMessageManager {

	NegotiationMessage readByUUID(String iMessageUUID);

	List<Information> listMessages(QueryFilters iFilters);

	NegotiationMessage getMessageDraft(String iNegotiationID);

	boolean updateMessageDraft(String iNegotiationUUID, String iMessageUUID, List<Attribute> items, String template);

	boolean updateMessageDraft(String negotiationUUID, NegotiationMessage draftMessage);

	boolean updateEvaluableMessage(String iNegotiationID, String iMessageID, String template);
	
}
package com.exrade.runtime.message;

import com.exrade.core.ExLogger;
import com.exrade.models.common.Meta.Operation;
import com.exrade.models.informationmodel.Attribute;
import com.exrade.models.informationmodel.IInformationModel;
import com.exrade.models.informationmodel.InformationModelDocument;
import com.exrade.models.messaging.EvaluableMessage;
import com.exrade.models.messaging.Information;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.message.persistence.MessagePersistenceManager;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.signatures.ISignatureManager;
import com.exrade.runtime.signatures.SignatureManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.util.List;

public class MessageManager implements IMessageManager {

	private MessagePersistenceManager messagePersistentManager;
	private INegotiationManager negotiationManager;

	public MessageManager() {
		this(new MessagePersistenceManager(),new NegotiationManager());
	}

	public MessageManager(MessagePersistenceManager iMessagePersistenceManager,INegotiationManager iNegotiationManager) {
		this.messagePersistentManager = iMessagePersistenceManager;
		this.negotiationManager = iNegotiationManager;
	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.message.IMessageManager#readByUUID(java.lang.String)
	 */
	@Override
	public NegotiationMessage readByUUID(String iMessageUUID) {
		NegotiationMessage negotiationMessage = null;
		if (iMessageUUID != null){
			QueryFilters filters = new QueryFilters();
			filters.put(QueryParameters.UUID,iMessageUUID);
			negotiationMessage = messagePersistentManager.read(filters);
		}
		return negotiationMessage;
	}


	/* (non-Javadoc)
	 * @see com.exrade.runtime.message.IMessageManager#listMessages(com.exrade.platform.persistence.query.QueryFilters)
	 */
	@Override
	public List<Information> listMessages(QueryFilters iFilters) {
		return messagePersistentManager.listMessages(iFilters);
	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.negotiation.INegotiationManager#getMessageDraft(java.lang.String)
	 */
	@Override
	public NegotiationMessage getMessageDraft(String iNegotiationID) {
		Negotiation neg = negotiationManager.getNegotiation(iNegotiationID);
		NegotiationMessage draftMessage = null;
		Negotiator involvedNegotiator = negotiationManager.getInvolvedNegotiator(neg, ContextHelper.getMembership());

		try {
			draftMessage = neg.getMessageDraft(involvedNegotiator);
		}
		catch(ExException ex) {
			//ExException("Message draft was not cleared")
			if(ex.getMessage().equals("Message draft was not cleared")) {
				draftMessage = neg.getStateMachine(involvedNegotiator).getMessageDrafts().get(0);
				ExLogger.get().debug("Marking message as draft: " + draftMessage);
				draftMessage.setDraft(true);
				draftMessage.setSender(null);
				draftMessage.setReceiver(null);
				messagePersistentManager.update(draftMessage);
			}
		}

		// Add permission on fields editing
		if (draftMessage != null){
			InformationModelDocument.addOperationsForNegotiable(((Offer)draftMessage).getItems(),Sets.newHashSet(Operation.SET));
		}
		return draftMessage;
	}

	@Override
	public boolean updateMessageDraft(String iNegotiationID, String iMessageID, List<Attribute> items, String template) {
		if(Strings.isNullOrEmpty(template) && ExCollections.isEmpty(items))
			throw new ExException("Both contract template and information model attributes cannot be empty");

		Negotiation neg = negotiationManager.getNegotiation(iNegotiationID);
		NegotiationMessage message = readByUUID(iMessageID);

		if(!Strings.isNullOrEmpty(template)) {
			ISignatureManager signatureManager = new SignatureManager();
			template = signatureManager.updateSigners(iNegotiationID, template);

			Attribute.updateIssues(InformationModelUtil.getFieldsFromTemplate(template, neg.getLanguage()),
					((IInformationModel)message).getItems());
			((Offer)message).setModelData(InformationModelUtil.extractDataFromTemplate(template));
		}
		else {
			Attribute.updateIssuesData(items,((IInformationModel)message).getItems());
		}

		((Offer)message).setTemplate(template);

		message = messagePersistentManager.update(message);
		ExLogger.get().debug("Offer version: {}", message.getVersion());
		return true;
	}

	@Override
	public boolean updateMessageDraft(String negotiationUUID, NegotiationMessage draftMessage) {
		return updateMessageDraft(negotiationUUID, draftMessage.getUuid(), ((IInformationModel)draftMessage).getItems(), ((Offer)draftMessage).getTemplate());
	}

	@Override
	public boolean updateEvaluableMessage(String iNegotiationID, String iMessageID, String template) {
		List<NegotiationMessage> messages = negotiationManager.getMessages(iNegotiationID);
		
		for(NegotiationMessage message: messages) {
			EvaluableMessage evaluableMessage = (EvaluableMessage)message;
			if(evaluableMessage.getNegotiationMessage().getUuid().equals(iMessageID)) {
				Security.checkEvaluableMessageUpdatePermission(iNegotiationID, evaluableMessage);
				evaluableMessage.setTemplate(template);
				messagePersistentManager.update(evaluableMessage);
				return true;
			}
		}
		
		return false;
	}
}

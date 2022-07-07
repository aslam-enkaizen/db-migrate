package com.exrade.runtime.message;

import com.exrade.core.ExLogger;
import com.exrade.models.messaging.*;
import com.exrade.runtime.engine.StateMachine;
import com.exrade.util.ExCollections;
import com.exrade.util.SortingUtil;

import java.util.List;

public class DraftMessageGenerator {

	public NegotiationMessage createMessageDraft(StateMachine stateMachine) {
		NegotiationMessage draftMessage = null;

		String currentMessageType = stateMachine.getStage().getDoAction().getKindOf();

		ExLogger.get().debug("Creating message draft of type: " + currentMessageType);
		if (currentMessageType.equals(Offer.class.getSimpleName())) {
			draftMessage = createOfferDraft(stateMachine);
		} else if (currentMessageType.equals(CounterOffer.class.getSimpleName())) {
			draftMessage = createCounterOfferDraft(stateMachine);
		}
		ExLogger.get().debug("Finished creating message draft: " + draftMessage);

		return draftMessage;
	}

	private Offer createOfferDraft(StateMachine stateMachine) {
		Offer draftMessage = null;
		NegotiationMessageFactory msgFactory = new NegotiationMessageFactory(stateMachine.getNegotiation());

		List<EvaluableMessage> evaluatedOffers = stateMachine.getNegotiation().getMessageBox()
				.getEvaluableMessageList();
		if (evaluatedOffers != null && evaluatedOffers.size() > 0) {
			SortingUtil.sortByMessageCreationTime(evaluatedOffers, true);
			for (EvaluableMessage evaluableMessage : evaluatedOffers) {
				// if requestor and receiver is same person && 
					// if requestor is participant (participant can have only one evaluable message) or
					// if requestor is owner and there is only one participant or
					// if the message marked as accepted
				if (evaluableMessage.getNegotiationMessage().getReceiver().equals(stateMachine.getUser())
						&& (stateMachine.getNegotiation().isParticipant(stateMachine.getUser()) 
								|| stateMachine.getNegotiation().getParticipants().size() == 1
								|| evaluableMessage.isAccepted())) {
					draftMessage = msgFactory.createOffer(((Offer) evaluableMessage.getNegotiationMessage()), null, null, null);
					draftMessage.setOfferFromOponent((Offer) evaluableMessage.getNegotiationMessage()); // Set reference to the last received offer
					ExLogger.get().debug("Creating message draft [Offer] from accepted evaluableMessage: " + evaluableMessage);
					break;
				}
			}
		}

		if (draftMessage == null) {
			List<NegotiationMessage> sentOffers = stateMachine.getNegotiation().getMessageBox().getSentMessages(stateMachine.getUser(), Offer.class);
			
			if(ExCollections.isNotEmpty(sentOffers)) {
				SortingUtil.sortByMessageCreationTime(sentOffers, true);
				draftMessage = msgFactory.createOffer((Offer)sentOffers.get(0), null, null, null);
				ExLogger.get().debug("Creating message draft [Offer] from lastSentOffer: " + sentOffers.get(0));
			}
		}

		if (draftMessage == null) { // fallback
			draftMessage = msgFactory.createOffer(stateMachine.getNegotiation().readStartOffer(), null, null, null);
			ExLogger.get().debug("Creating message draft [Offer] from startOffer");
		}

		return draftMessage;
	}

	private CounterOffer createCounterOfferDraft(StateMachine stateMachine) {
		CounterOffer draftMessage = null;
		NegotiationMessageFactory msgFactory = new NegotiationMessageFactory(stateMachine.getNegotiation());

		List<EvaluableMessage> evaluatedOffers = stateMachine.getNegotiation().getMessageBox()
				.getEvaluableMessageList();
		if (evaluatedOffers != null && evaluatedOffers.size() > 0) {
			SortingUtil.sortByMessageCreationTime(evaluatedOffers, true);
			for (EvaluableMessage evaluableMessage : evaluatedOffers) {
				if (evaluableMessage.getNegotiationMessage().getReceiver().equals(stateMachine.getUser())
						&& (stateMachine.getNegotiation().isParticipant(stateMachine.getUser()) || evaluableMessage
								.isAccepted())) {
					draftMessage = msgFactory.createCounterOffer((Offer) evaluableMessage.getNegotiationMessage());
					ExLogger.get().debug("Creating message draft [CounterOffer] from accepted evaluableMessage: " + evaluableMessage);
					break;
				}
			}
		}

		if (draftMessage == null) {
			Offer lastReceivedOffer = (Offer) stateMachine.getNegotiation().getMessageBox()
					.getLastEnqueuedReceivedMessage(stateMachine.getUser(), Offer.class);
			if (lastReceivedOffer != null) {
				draftMessage = msgFactory.createCounterOffer(lastReceivedOffer);
				ExLogger.get().debug("Creating message draft [CounterOffer] from lastReceivedOffer: " + lastReceivedOffer);
			}
		}

		if (draftMessage == null) {
			CounterOffer lastReceivedOffer = (CounterOffer) stateMachine.getNegotiation().getMessageBox()
					.getLastEnqueuedReceivedMessage(stateMachine.getUser(), CounterOffer.class);
			if (lastReceivedOffer != null) {
				draftMessage = msgFactory.createCounterOffer(lastReceivedOffer);
				ExLogger.get().debug("Creating message draft [CounterOffer] from lastReceivedCounterOffer: " + lastReceivedOffer);
			}
		}

		if (draftMessage == null) { // fallback
			draftMessage = msgFactory.createCounterOffer(stateMachine.getNegotiation().readStartOffer());
			ExLogger.get().debug("Creating message draft [CounterOffer] from startOffer");
		}

		return draftMessage;
	}
}

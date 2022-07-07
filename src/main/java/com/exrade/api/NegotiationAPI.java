package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.common.FAQ;
import com.exrade.models.common.Meta;
import com.exrade.models.history.NegotiationEvent;
import com.exrade.models.messaging.AdmissionRequest;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.models.negotiation.INegotiation;
import com.exrade.models.negotiation.INegotiationSummary;
import com.exrade.models.negotiation.NegotiationParameter;
import com.exrade.models.negotiation.NegotiationStatus;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;
import java.util.Map;

public interface NegotiationAPI {

	boolean acceptAdmission(ExRequestEnvelope request, String iUserID, String iNegotiationID,
			String admissionUUID);

	String addFAQ(ExRequestEnvelope request, String iNegotiationID, String iQuestion, String iAnswer);

	void cancel(ExRequestEnvelope request, String iNegotiationID, String iReason);

	void cancelByMembership(ExRequestEnvelope request, Membership membership, String iReason);

	String createAndPublishNegotiation(ExRequestEnvelope request, NegotiationParameter negotiationParameter);

	void deleteFAQ(ExRequestEnvelope request, String iNegotiationID, String iFaqUUID);

	void deleteNegotiation(ExRequestEnvelope request, String negotiationID);

	boolean fireTransition(ExRequestEnvelope request, String iNegotiationID, String iTransitionName,String note);

	List<AdmissionRequest> getAdmissionRequests(ExRequestEnvelope request, String iUserID,
			String iNegotiationID, String messageStatus);

	List<FAQ> getFAQ(ExRequestEnvelope request, String iNegotiationID, String iInvitationCode);

	List<NegotiationEvent> getLogEventList(ExRequestEnvelope request, String iUserUUID,
			String iNegotiationID);

	NegotiationMessage getMessage(ExRequestEnvelope request, String iNegotiationUUID, String messageID);

	List<NegotiationMessage> getMessages(ExRequestEnvelope request, String iNegotiationID);

	NegotiationParameter getNegotiationDraft(ExRequestEnvelope request, String iNegotiationUUID);
	
	INegotiationSummary getNegotiationSummary(ExRequestEnvelope request, String iNegotiationID, String iInvitationCode);

	NegotiationStatus getStatus(ExRequestEnvelope request, Negotiator negotiator, String iNegotiationID);

	NegotiationStatus getStatus(ExRequestEnvelope request, String iNegotiationID);

	NegotiationStatus getStatus(ExRequestEnvelope request, String iUserProfileUUID, String iNegotiationID);

	String join(ExRequestEnvelope request, String iNegotiationID, String iInvitationCode);

	List<INegotiationSummary> listInvitedNegotiations(ExRequestEnvelope request, QueryFilters iFilters);

	List<INegotiationSummary> listInvolvedNegotiations(ExRequestEnvelope request, QueryFilters iFilters,
			String iUserID);

	List<INegotiationSummary> listJoinedNegotiations(ExRequestEnvelope request, QueryFilters iFilters,
			String iUserID);

	List<INegotiationSummary> listNegotiations(ExRequestEnvelope request,  Map<String, String> filterParams);

	List<INegotiationSummary> listOwnedNegotiations(ExRequestEnvelope request, QueryFilters iFilters,
			String iUserUUID);

	boolean markAccepted(ExRequestEnvelope request, String iNegotiationID, String iOfferToAcceptID,
			boolean exclusive);

	boolean markNotAccepted(ExRequestEnvelope request, String iNegotiationID, String iOfferToNotAcceptID);

	boolean rejectAdmission(ExRequestEnvelope request, String iUserID, String iNegotiationID,
			String admissionUUID);

	boolean removeParticipant(ExRequestEnvelope request, String userID, String negotiationID,
			String participantID);

	void sendInfoMessage(ExRequestEnvelope request, String userID, String negotiationID, String content,
			List<String> receiverIDs, List<String> files);

	void sendInfoMessageToAll(ExRequestEnvelope request, String userID, String negotiationID,
			String content, List<String> files);

	void updateMessageScore(ExRequestEnvelope request, String negotiationID, String evaluableMessageID,
			int score);
	
	NegotiationParameter copy(ExRequestEnvelope requestEnvelope, String uuid);

	void publishNegotiation(ExRequestEnvelope request, String negotiationUUID);

	void updateNegotiationDraft(ExRequestEnvelope request,
			String negotiationUUID, NegotiationParameter negotiationParameter);

	String createNegotiationDraft(ExRequestEnvelope request,
			NegotiationParameter negotiationParameter);

	void updateAndPublishNegotiation(ExRequestEnvelope requestEnvelope,
			String iNegotiationUUID, NegotiationParameter negotiationParameter);

	void discardNegotiationDraft(ExRequestEnvelope request,
			String iNegotiationID);

	void updateNegotiation(ExRequestEnvelope requestEnvelope,
			String iNegotiationUUID, NegotiationParameter negotiationParameter);
	
	Meta getNegotiationSchema(ExRequestEnvelope requestEnvelope,String iNegotiationUUID);

	NegotiationParameter getNegotiationParameter(ExRequestEnvelope request,
			String iNegotiationUUID, Map<String, String> apiFilters);

	NegotiationParameter getNegotiationParameterEmpty(
			ExRequestEnvelope request, Map<String, String> queryParameters);

	String join(ExRequestEnvelope requestEnvelope, String negotiationUUID,
			String invitationCode, List<String> signers);
	
	void toggleArchiveStatus(ExRequestEnvelope requestEnvelope, String iNegotiationID);
	
	String generateNegotiationFromPreconfigured(ExRequestEnvelope requestEnvelope, String iNegotiationTemplateUUID);

	INegotiation getNegotiationByUUID(ExRequestEnvelope requestEnvelope, String uuid);

}

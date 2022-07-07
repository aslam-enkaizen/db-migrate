package com.exrade.api.impl;

import com.exrade.api.NegotiationInvitationAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.contact.Contact;
import com.exrade.models.invitations.AbstractInvitation;
import com.exrade.models.invitations.InvitationStatus;
import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.models.negotiation.UserAdmissionStatus;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.security.NegotiationRole;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.contact.ContactManager;
import com.exrade.runtime.contact.IContactManager;
import com.exrade.runtime.invitation.INegotiationInvitationManager;
import com.exrade.runtime.invitation.NegotiationInvitationManager;
import com.exrade.runtime.invitation.persistence.InvitationQuery;
import com.exrade.runtime.rest.RestParameters.NegotiationInvitationFields;
import com.exrade.runtime.rest.RestParameters.NegotiationInvitationFilters;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.MultiLanguageUtil;
import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NegotiationInvitationManagerAdapter implements
		NegotiationInvitationAPI {

	private INegotiationInvitationManager manager = new NegotiationInvitationManager();
	
	@Override
	public void acceptInvitation(ExRequestEnvelope request, String invitationID) {
		ContextHelper.initContext(request);
		manager.acceptInvitation(invitationID);
	}

	@Override
	public void acceptNegotiationInvitationIfExists(ExRequestEnvelope request,
			String iInvitedUserUUID, String iNegotiationUUID) {
		ContextHelper.initContext(request);
		manager.acceptNegotiationInvitationIfExists(iInvitedUserUUID, iNegotiationUUID);
	}

	@Override
	public void blockInvitation(ExRequestEnvelope request, String invitationID) {
		ContextHelper.initContext(request);
		manager.blockInvitation(invitationID);
	}

	@Override
	public void deleteInvitation(ExRequestEnvelope request,
			String invitationUUID) {
		ContextHelper.initContext(request);
		manager.deleteInvitation(invitationUUID);
	}

	@Override
	public void doAfterAccept(ExRequestEnvelope request,
			AbstractInvitation invite) {
		ContextHelper.initContext(request);
		manager.doAfterAccept(invite);
	}

	@Override
	public AbstractInvitation getInvitation(ExRequestEnvelope request,
			String invitationID) {
		ContextHelper.initContext(request);
		return manager.getInvitation(invitationID);
	}

	@Override
	public NegotiationInvitation getNegotiationInvitation(
			ExRequestEnvelope request, Negotiator iInvitedMember,
			String iInvitedUserEmail, String iNegotiationUUID) {
		ContextHelper.initContext(request);
		return manager.getNegotiationInvitation(iInvitedMember, iInvitedUserEmail, iNegotiationUUID);
	}

	@Override
	public NegotiationInvitation inviteToNegotiation(ExRequestEnvelope request, String iNegotiationUUID,
			String iInvitedMemberUUID, String iInvitedEmail) {
		ContextHelper.initContext(request);
		
		Membership invitedMembership = null;
		if (Strings.isNullOrEmpty(iInvitedEmail) && Strings.isNullOrEmpty(iInvitedMemberUUID)){
			throw new ExParamException(ErrorKeys.PARAMS_INVALID, MultiLanguageUtil.getLabel("Invitation email and member id cant be both empty"));
		}
		else if (Strings.isNullOrEmpty(iInvitedEmail) && !Strings.isNullOrEmpty(iInvitedMemberUUID)){
			IMembershipManager membershipManager = new MembershipManager();
			invitedMembership = membershipManager.findByUUID(iInvitedMemberUUID, false);
			if(invitedMembership == null) {
				IContactManager contactManager = new ContactManager();
				Contact contact = contactManager.getContactByUUID(iInvitedMemberUUID);
				if(contact != null) {
					iInvitedEmail = contact.getEmail();
				}
				else {
					throw new ExParamException(ErrorKeys.PARAMS_INVALID, MultiLanguageUtil.getLabel("Invalid member id"));
				}
			}
			else {
				iInvitedEmail = invitedMembership.getEmail();
			}
		}
		
		Security.checkNegotiationAdmissionStatusAndRoles(iNegotiationUUID, UserAdmissionStatus.OWNED, Arrays.asList(NegotiationRole.OWNER, NegotiationRole.ADMINISTRATOR));
		
		return manager.inviteToNegotiation(ContextHelper.getMembership(), iNegotiationUUID, invitedMembership, iInvitedEmail.toLowerCase());
	}

	@Override
	public boolean isValidInvitation(ExRequestEnvelope request,
			String invitationUUID) {
		ContextHelper.initContext(request);
		return manager.isValidInvitation(invitationUUID);
	}

	@Override
	public void rejectInvitation(ExRequestEnvelope request, String invitationID) {
		ContextHelper.initContext(request);
		manager.rejectInvitation(invitationID);
	}

	@Override
	public void updateInvitationsForNewUser(ExRequestEnvelope request,
			Negotiator iNegotiator) {
		ContextHelper.initContext(request);
		manager.updateInvitationsForNewUser(iNegotiator);
	}

	@Override
	public void updateInvitationStatus(ExRequestEnvelope request,
			AbstractInvitation invitation, InvitationStatus invitationStatus) {
		ContextHelper.initContext(request);
		manager.updateInvitationStatus(invitation, invitationStatus);
	}

	@Override
	public void updateInvitationStatus(ExRequestEnvelope request,
			String invitationUUID, InvitationStatus invitationStatus) {
		ContextHelper.initContext(request);
		manager.updateInvitationStatus(invitationUUID, invitationStatus);
	}

	@Override
	public List<NegotiationInvitation> getAllIncomingInvitations(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putAll(getInvitationFilters(iFilters));
		filters.put(NegotiationInvitationFilters.INVITATION_INBOX,InvitationQuery.INCOMING);
		return manager.find(filters);
	}

	@Override
	public List<NegotiationInvitation> getSentInvitationsOfNegotiation(ExRequestEnvelope request,
			String iNegotiationUUID, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putAll(getInvitationFilters(iFilters));
		filters.put(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID,iNegotiationUUID);
		return manager.find(filters);
	}
	
	private static QueryFilters getInvitationFilters(Map<String, String> iFilters) {
		QueryFilters filters = QueryFilters.create(iFilters);
		if (filters.isNullOrEmpty(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID)){
			filters.putIfNotNull(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID,iFilters.get(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID));
		}
		if (filters.isNullOrEmpty(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID)){
			filters.putIfNotNull(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID,iFilters.get("invitedNegotiationUUID"));
		}
		if (filters.isNullOrEmpty(NegotiationInvitationFilters.INVITATION_INBOX)){
			filters.putIfNotNull(NegotiationInvitationFilters.INVITATION_INBOX,iFilters.get(NegotiationInvitationFilters.INVITATION_INBOX));
		}
		filters.putIfNotNull(NegotiationInvitationFields.INVITED_EMAIL,iFilters.get(NegotiationInvitationFields.INVITED_EMAIL));
		filters.putIfNotNull(NegotiationInvitationFields.INVITATION_STATUS,iFilters.get(NegotiationInvitationFields.INVITATION_STATUS));
		filters.putIfNotNull(NegotiationInvitationFields.INVITED_MEMBERSHIP,iFilters.get(NegotiationInvitationFields.INVITED_MEMBERSHIP));
		filters.putIfNotNull(NegotiationInvitationFilters.INVITED_MEMBERSHIP_UUID,iFilters.get(NegotiationInvitationFilters.INVITED_MEMBERSHIP_UUID));
		filters.putIfNotNull(NegotiationInvitationFields.INVITED_NEGOTIATION,iFilters.get(NegotiationInvitationFields.INVITED_NEGOTIATION));
		return filters;
	}

}

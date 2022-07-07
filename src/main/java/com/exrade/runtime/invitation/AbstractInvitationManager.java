package com.exrade.runtime.invitation;

import com.exrade.core.ExLogger;
import com.exrade.models.contact.Contact;
import com.exrade.models.invitations.AbstractInvitation;
import com.exrade.models.invitations.InvitationStatus;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExInvitationException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.contact.ContactManager;
import com.exrade.runtime.contact.IContactManager;
import com.exrade.runtime.invitation.persistence.InvitationPersistence;
import com.exrade.runtime.notification.NotificationManager;
import org.slf4j.Logger;

public abstract class AbstractInvitationManager implements IInvitationManager {

	protected static final Logger logger = ExLogger.get();

	protected InvitationPersistence invitationPersistence;

	protected NotificationManager notificationManager = new NotificationManager();

	@Override
	public void deleteInvitation(String invitationID) {
		QueryFilters filters = new QueryFilters();
		filters.put(QueryParameters.UUID, invitationID);
		AbstractInvitation invite = invitationPersistence.read(filters);

		if (invite != null){
			invitationPersistence.delete(invite);
		}
		else{
			throw new ExNotFoundException(invitationID);
		}
	}

	public abstract void doAfterAccept(AbstractInvitation iInvitation);

	@Override
	public void acceptInvitation(String invitationUUID) {
		AbstractInvitation invitation = invitationPersistence.readObjectByUUID(AbstractInvitation.class,invitationUUID);

		if (invitation == null){
			throw new ExNotFoundException(invitationUUID);
		}

		if (invitation != null && invitation.isInvitationStatus(InvitationStatus.PENDING)){
			doAfterAccept(invitation);
		}
		else {
			throw new ExInvitationException(ErrorKeys.INVITATION_STATUS_INVALID);
		}
	}

	public abstract void doAfterReject(AbstractInvitation iInvitation);

	@Override
	public void rejectInvitation(String invitationUUID) {
		AbstractInvitation invitation = invitationPersistence.readObjectByUUID(AbstractInvitation.class,invitationUUID);

		if (invitation == null){
			throw new ExNotFoundException(invitationUUID);
		}

		if (invitation != null && invitation.isInvitationStatus(InvitationStatus.PENDING)){
			updateInvitationStatus(invitation, InvitationStatus.REJECTED);
			doAfterReject(invitation);
		}
		else {
			throw new ExInvitationException(ErrorKeys.INVITATION_STATUS_INVALID);
		}
	}

	@Override
	public void blockInvitation(String invitationUUID) {
		AbstractInvitation invitation = invitationPersistence.readObjectByUUID(AbstractInvitation.class,invitationUUID);

		if (invitation == null){
			throw new ExNotFoundException(invitationUUID);
		}

		if (invitation != null && invitation.isInvitationStatus(InvitationStatus.PENDING)){
			updateInvitationStatus(invitation, InvitationStatus.BLOCKED);
		}
		else {
			throw new ExInvitationException(ErrorKeys.INVITATION_STATUS_INVALID);
		}
	}

	@Override
	public void updateInvitationStatus(AbstractInvitation invitation,InvitationStatus invitationStatus) {
		invitation.setInvitationStatus(invitationStatus);
		invitationPersistence.update(invitation);
	}

	@Override
	public void updateInvitationStatus(String invitationUUID,InvitationStatus invitationStatus) {
		AbstractInvitation invitation = invitationPersistence.readObjectByUUID(AbstractInvitation.class,invitationUUID);
		updateInvitationStatus(invitation,invitationStatus);
	}

	@Override
	public boolean isValidInvitation(String invitationUUID) {
		QueryFilters filters = new QueryFilters();
		filters.put(QueryParameters.UUID, invitationUUID);
		AbstractInvitation invite = invitationPersistence.read(filters);

		return (invite != null
				&& invite.isInvitationStatus(InvitationStatus.PENDING));

	}

	@Override
	public AbstractInvitation getInvitation(String invitationID) {
		QueryFilters filters = new QueryFilters();
		filters.put(QueryParameters.UUID, invitationID);
		return invitationPersistence.read(filters);
	}


	protected void createContact(AbstractInvitation invitation, Membership invitedMembership, Membership ownerMembership){
		if(invitation != null){
			try{
				Contact contact = null;
				if(invitedMembership == null)
					contact = new Contact(invitation.getInvitedEmail());
				else
					contact = new Contact(invitedMembership);
				contact.setOwnerProfile(ownerMembership.getProfile());
				contact.setOwner(ownerMembership);
				IContactManager contactManager = new ContactManager();
				contactManager.addContact(contact);
			}
			catch(Exception ex){
				logger.warn("Cannot create contact from invitation. {}. InvitationUUID: {}", ex.getMessage(), invitation.getUuid());
			} // ignore exception if cannot create contact
		}
	}
}

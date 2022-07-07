package com.exrade.runtime.userprofile;

import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.userprofile.*;
import com.exrade.models.userprofile.security.MemberStatus;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.Date;
import java.util.List;

public interface IMembershipManager {

	/**
	 * Add a user as a membership of the given profile with the specified role
	 * @param user
	 * @param profile
	 * @param roleName
	 * @return the new membership relation
	 */
	Membership addMembership(User user, Profile profile, String roleName);
	
	Membership createGuestMembership(String firstName, String lastName, String email, String phone, String title, IProfile profile);
	
	Membership createGuestMembership(IUser user, IProfile profile, String title);

	void updateRole(String memberUUID, String roleName);

	void setDefaultMembership(String memberUUID, boolean iDefaultProfileValue);

	Negotiator findDefaultMembershipByEmail(String iEmail);

	Membership findByUUID(String uuid, boolean noCache);

	List<Membership> getProfileMembers(String iProfileUUID);
	
	List<Membership> getActiveProfileMembers(String iProfileUUID);

	List<Membership> getUserMemberships(String iUserUUID);

	/**
	 * If exists read the membership of the given user and profile  
	 * @param userUUID
	 * @param iProfileUUID
	 * @param includeGuest
	 * @return Membership relation
	 */
	Membership getMembershipOf(String userUUID, String iProfileUUID, boolean includeGuest);

	/**
	 * Check if a User is a member of the given profile
	 * @param iUserUUID
	 * @param iProfileUUID
	 * @return
	 */
	boolean isMembership(String iUserUUID, String iProfileUUID);

	/**
	 * Check if a User is owning the given Membership relation
	 * @param iUserUUID
	 * @param iMembershipUUID
	 * @return
	 */
	boolean isMembershipOwnedBy(IUser user, String iMembershipUUID);

	List<Membership> find(QueryFilters iFilters);

	Membership getOwnerMembership(String iProfileUUID);
	
	void updateMemberStatus(String iMembershipUUID, MemberStatus iMemberStatus);

	Membership addMembership(User user, Profile profile, String title,
			String roleName, Date expirationDate, List<String> identityDocuments,
			Double maxNegotiationAmount, boolean agreementSigner, Membership supervisor);

	Membership addMembership(MemberInvitation memberInvitation);

	Membership updateMembership(Membership membership);

	Membership getMembershipByEmail(String email, String iProfileUUID, boolean includeGuest);

	Membership updateMembership(MemberInvitation invitation);
	
	void disableAdditionalMembersFromProfile(Profile iProfile);
	
	void enableOwnerMembership(Profile iProfile);
}
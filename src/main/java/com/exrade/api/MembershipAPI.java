package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.MemberStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MembershipAPI {

	/**
	 * Add a user as a membership of the given profile with the specified role
	 * @param user
	 * @param iProfileUUID
	 * @param roleName
	 * @param roleName2 
	 * @param agreementSigner 
	 * @param maxNegotiationAmount 
	 * @param identityDocuments 
	 * @param expirationDate 
	 * @return the new membership relation
	 */
	Membership addMembership(ExRequestEnvelope request, String userUUID, String iProfileUUID, String title, String roleName, 
			Date expirationDate, List<String> identityDocuments, Double maxNegotiationAmount, boolean agreementSigner, String supervisor);
	
	Membership createGuestMembership(ExRequestEnvelope request, String firstName, String lastName, String email, String phone, String title);

	Membership changeCurrentMembership(ExRequestEnvelope request, String newMembershipUUID);
	
	void updateRole(ExRequestEnvelope request, String memberUUID, String roleName);

	void setDefaultMembership(ExRequestEnvelope request, String memberUUID, boolean iDefaultProfileValue);

	Negotiator findDefaultMembershipByEmail(ExRequestEnvelope request, String iEmail);
	
	Membership findGuestMembershipByEmail(ExRequestEnvelope request, String iEmail);

	Membership findByUUID(ExRequestEnvelope request, String uuid);

	List<Membership> findByProfileUUID(ExRequestEnvelope request, String iProfileUUID);

	List<Membership> findByUserUUID(ExRequestEnvelope request, String iUserUUID);

	/**
	 * If exists read the membership of the given user and profile  
	 * @param userUUID
	 * @param iProfileUUID
	 * @return Membership relation
	 */
	Membership getMembershipOf(ExRequestEnvelope request, String userUUID, String iProfileUUID);

	/**
	 * Check if a User is a member of the given profile
	 * @param iUserUUID
	 * @param iProfileUUID
	 * @return
	 */
	boolean isMembership(ExRequestEnvelope request, String iUserUUID, String iProfileUUID);

	/**
	 * Check if a User is owning the given Membership relation
	 * @param iUserUUID
	 * @param iMembershipUUID
	 * @return
	 */
	boolean isMembershipOwnedBy(ExRequestEnvelope request, User user, String iMembershipUUID);

	List<Membership> find(ExRequestEnvelope request, Map<String, String> iFilters);

	Membership getOwnerMembership(ExRequestEnvelope request, String iProfileUUID);
	
	void updateMemberStatus(ExRequestEnvelope request, String iMembershipUUID, MemberStatus iMemberStatus);

	Membership updateMembership(ExRequestEnvelope request, Membership membership);

}
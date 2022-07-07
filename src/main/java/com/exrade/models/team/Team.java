package com.exrade.models.team;

import com.exrade.models.userprofile.IProfile;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class Team extends BaseEntityUUIDTimeStampable  {

	private TeamType partyType = TeamType.OWNER;

	private IProfile profile;

	private TeamObjectType objectType;

	private String objectID;

	@OneToMany(orphanRemoval = true)
	private List<ITeamMember> members = new ArrayList<>();

	public TeamType getPartyType() {
		return partyType;
	}

	public void setPartyType(TeamType partyType) {
		this.partyType = partyType;
	}

	public List<ITeamMember> getMembers() {
		return members;
	}

	public void setMembers(List<ITeamMember> members) {
		this.members = members;
	}

	public IProfile getProfile() {
		return profile;
	}

	public void setProfile(IProfile profile) {
		this.profile = profile;
	}

	public String getProfileUUID() {
		if(getProfile() != null)
			return getProfile().getUuid();

		return null;
	}

	public TeamObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(TeamObjectType objectType) {
		this.objectType = objectType;
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public ITeamMember findTeamMember(String uuid) {
		for(ITeamMember member : getMembers()) {
			if(member.getUuid().equals(uuid))
				return member;
		}
		return null;
	}

}

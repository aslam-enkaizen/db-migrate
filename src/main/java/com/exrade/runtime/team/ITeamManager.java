package com.exrade.runtime.team;

import com.exrade.models.team.ITeamMember;
import com.exrade.models.team.Team;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;


public interface ITeamManager {
	Team createTeam(Team iTeam);

	Team createTeam(String objectType, String objectId);

	Team getTeam(String iTeamUUID);

	Team getTeam(String objectType, String objectId);

	List<Team> getTeams(QueryFilters iFilters);

	ITeamMember addTeamMember(String iTeamUUID, String membershipUUID, String roleName);

	ITeamMember getTeamMember(String iTeamUUID, String membershipUUID);

	ITeamMember updateTeamMember(String iTeamUUID, String membershipUUID, String roleName);

	void removeTeamMember(String iTeamUUID, String membershipUUID);
	
	void notifyTeamMember(String iTeamUUID, String membershipUUID);

}
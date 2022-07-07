package com.exrade.runtime.team;

import com.exrade.core.ExLogger;
import com.exrade.models.Role;
import com.exrade.models.activity.Verb;
import com.exrade.models.contract.Contract;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.team.*;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.security.InformationModelTemplateRole;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.contract.IContractManager;
import com.exrade.runtime.informationmodel.IInformationModelManager;
import com.exrade.runtime.informationmodel.InformationModelManager;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.ContractNotificationEvent;
import com.exrade.runtime.notification.event.InformationModelNotificationEvent;
import com.exrade.runtime.notification.event.NegotiationNotificationEvent;
import com.exrade.runtime.rest.RestParameters.TeamFields;
import com.exrade.runtime.security.RoleManager;
import com.exrade.runtime.team.persistence.TeamQuery;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import org.apache.commons.lang3.EnumUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeamManager implements ITeamManager {

	private final PersistentManager persistenceManager = new PersistentManager();
	private final NotificationManager notificationManager = new NotificationManager();
	private final IMembershipManager membershipManager = new MembershipManager();
	private final IInformationModelManager informationModelManager = new InformationModelManager();
	private final IContractManager contractManager = new ContractManager();
	private final INegotiationManager negotiationManager = new NegotiationManager();

	@Override
	public Team createTeam(Team iTeam) {
		// TODO: check if user can create the team
		Team team = getTeam(iTeam.getObjectType().toString(), iTeam.getObjectID());
		if (team != null)
			throw new ExException("Team exists!");

		team = persistenceManager.create(iTeam);
		return team;
	}

	@Override
	public Team createTeam(String objectType, String objectId) {
		Negotiator membership = ContextHelper.getMembership();

		// TODO: check if user can create the team
		Team team = PersistentManager.newDbInstance(Team.class);
		team.setObjectID(objectId);
		team.setObjectType(EnumUtils.getEnum(TeamObjectType.class, objectType));

		if (team.getObjectType().equals(TeamObjectType.CONTRACT)) {
			Contract contract = contractManager.getContractByUUID(objectId);
			if (contract != null && contract.getCreator() != null) {
				team.setProfile(contract.getCreator().getProfile());
				team.getMembers().add(
						createTeamMember(contract.getCreator().getIdentifier(), InformationModelTemplateRole.MANAGER));
			}
		} else if (team.getObjectType().equals(TeamObjectType.NEGOTIATION)) {
			Negotiation negotiation = negotiationManager.getNegotiation(objectId);
			if (negotiation != null) {
				if (negotiation.getOwner().getProfile().getUuid().equals(membership.getProfile().getUuid())) {
					team.setPartyType(TeamType.OWNER);
					team.setProfile(negotiation.getOwner().getProfile());
					team.getMembers().add(createTeamMember(negotiation.getOwner().getIdentifier(),
							InformationModelTemplateRole.MANAGER));
				}
				// create team for participant
				else if (negotiation.getParticipants() != null) {
					for (Negotiator participant : negotiation.getParticipants()) {
						if (participant.getProfile().getUuid().equals(membership.getProfile().getUuid())) {
							team.setPartyType(TeamType.PARTICIPANT);
							team.setProfile(participant.getProfile());
							team.getMembers().add(createTeamMember(participant.getIdentifier(),
									InformationModelTemplateRole.MANAGER));

							break;
						}
					}
				}
			}
		} else if (team.getObjectType().equals(TeamObjectType.INFORMATION_MODEL_TEMPLATE)) {
			InformationModelTemplate template = informationModelManager.readByUUID(objectId);
			if (template != null && template.getAuthorMembership() != null) {
				team.setProfile(template.getAuthorMembership().getProfile());
				team.getMembers().add(createTeamMember(template.getAuthorMembership().getIdentifier(),
						InformationModelTemplateRole.MANAGER));
			}
		}
		else {
			ExLogger.get().warn("Invalid team type. ObjectType: {}, ObjectId: {}", objectType, objectId);
			return null;
		}

		// adding default profile
		//if (team.getProfile() == null) {
		//	team.setProfile(membership.getProfile());
		//	team.getMembers().add(createTeamMember(membership.getIdentifier(), InformationModelTemplateRole.MANAGER));
		//}

		return createTeam(team);
	}

	@Override
	public Team getTeam(String iTeamUUID) {
		Team team = persistenceManager.readObjectByUUID(Team.class, iTeamUUID);
		return team;
	}

	@Override
	public Team getTeam(String objectType, String objectId) {
		QueryFilters filters = QueryFilters.create(TeamFields.OBJECT_TYPE, objectType);
		filters.put(TeamFields.OBJECT_ID, objectId);
		filters.put(TeamFields.PROFILE_UUID, ContextHelper.getMembership().getProfile().getUuid());
		List<Team> teams = getTeams(filters);

		if (ExCollections.isNotEmpty(teams))
			return teams.get(0);
		return null;
	}

	@Override
	public List<Team> getTeams(QueryFilters iFilters) {
		return persistenceManager.listObjects(new TeamQuery(), iFilters);
	}

	@Override
	public ITeamMember addTeamMember(String iTeamUUID, String membershipUUID, String roleName) {
		Team team = this.getTeam(iTeamUUID);
		if (team == null)
			throw new ExNotFoundException(iTeamUUID);

		if (team.findTeamMember(membershipUUID) != null)
			return team.findTeamMember(membershipUUID);

		Membership membership = membershipManager.findByUUID(membershipUUID, true);
		if (membership == null)
			throw new ExNotFoundException(membershipUUID);

		RoleManager roleManager = new RoleManager();
		Role role = roleManager.findByName(roleName);
		if (role == null)
			throw new ExNotFoundException(roleName);

		TeamUserMember teamMember = new TeamUserMember();
		teamMember.setNegotiator(membership);
		teamMember.getRoles().add(role);
		team.getMembers().add(teamMember);
		team = persistenceManager.update(team);

		if (team.getObjectType().equals(TeamObjectType.CONTRACT)) {
			Contract contract = contractManager.getContractByUUID(team.getObjectID());
			notificationManager.process(
					new ContractNotificationEvent(NotificationType.CONTRACT_MEMBER_ADDED, contract, membership));
			ActivityLogger.log((Membership) ContextHelper.getMembership(), Verb.ADD, contract,
					getMemberships(team.getMembers()));
		} else if (team.getObjectType().equals(TeamObjectType.NEGOTIATION)) {
			Negotiation negotiation = negotiationManager.getNegotiation(team.getObjectID());
			notificationManager
					.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_MEMBER_ADDED, negotiation, membership));
			ActivityLogger.log((Membership) ContextHelper.getMembership(), Verb.ADD, negotiation,
					getMemberships(team.getMembers()));
		} else if (team.getObjectType().equals(TeamObjectType.INFORMATION_MODEL_TEMPLATE)) {
			InformationModelTemplate informationModelTemplate = informationModelManager.readByUUID(team.getObjectID());
			notificationManager.process(new InformationModelNotificationEvent(
					NotificationType.INFORMATION_MODEL_MEMBER_ADDED, informationModelTemplate, membership));
			ActivityLogger.log((Membership) ContextHelper.getMembership(), Verb.ADD, membership,
					informationModelTemplate, getMemberships(team.getMembers()));
		} else {
			ExLogger.get().warn("Invalid team type. ObjectType: {}, ObjectId: {}", team.getObjectType(),
					team.getObjectID());
		}

		return teamMember;
	}

	@Override
	public ITeamMember getTeamMember(String iTeamUUID, String membershipUUID) {
		ITeamMember teamMember = null;
		Team team = this.getTeam(iTeamUUID);
		if (team == null)
			throw new ExNotFoundException(iTeamUUID);

		Iterator<ITeamMember> memberItr = team.getMembers().iterator();
		while (memberItr.hasNext()) {
			ITeamMember member = memberItr.next();
			if (member.getUuid().equals(membershipUUID)) {
				teamMember = member;
				break;
			}
		}
		return teamMember;
	}

	@Override
	public void removeTeamMember(String iTeamUUID, String membershipUUID) {
		Team team = this.getTeam(iTeamUUID);
		if (team == null)
			throw new ExNotFoundException(iTeamUUID);

		Iterator<ITeamMember> memberItr = team.getMembers().iterator();
		while (memberItr.hasNext()) {
			ITeamMember member = memberItr.next();
			if (member.getUuid().equals(membershipUUID)) {
				memberItr.remove();
				team = persistenceManager.update(team);

				InformationModelTemplate template = informationModelManager.readByUUID(team.getObjectID());
				// notificationManager.process(new
				// TeamNotificationEvent(NotificationType.CONTRACT_MEMBER_REMOVED, team,
				// (Membership)((TeamUserMember) member).getNegotiator()));
				ActivityLogger.log((Membership) ContextHelper.getMembership(), Verb.REMOVE,
						((TeamUserMember) member).getNegotiator(), template, getMemberships(team.getMembers()));
				break;
			}
		}
	}

	@Override
	public ITeamMember updateTeamMember(String iTeamUUID, String membershipUUID, String roleName) {
		ITeamMember teamMember = null;
		Team team = this.getTeam(iTeamUUID);
		if (team == null)
			throw new ExNotFoundException(iTeamUUID);

		RoleManager roleManager = new RoleManager();
		Role role = roleManager.findByName(roleName);
		if (role == null)
			throw new ExNotFoundException(roleName);

		for (ITeamMember member : team.getMembers()) {
			if (member.getUuid().equals(membershipUUID)) {
				member.getRoles().clear();
				member.getRoles().add(role);
				teamMember = member;
				break;
			}
		}
		persistenceManager.update(team);

		return teamMember;
	}

	private TeamUserMember createTeamMember(String membershipUUID, String roleName) {

		Membership membership = membershipManager.findByUUID(membershipUUID, true);
		if (membership == null)
			throw new ExNotFoundException(membershipUUID);

		RoleManager roleManager = new RoleManager();
		Role role = roleManager.findByName(roleName);
		if (role == null)
			throw new ExNotFoundException(roleName);

		TeamUserMember teamMember = PersistentManager.newDbInstance(TeamUserMember.class);
		teamMember.setNegotiator(membership);
		teamMember.getRoles().add(role);

		return teamMember;
	}

	private List<Negotiator> getMemberships(List<ITeamMember> members) {
		List<Negotiator> memberships = new ArrayList<>();
		for (ITeamMember member : members) {
			if (member.getMemberObjectType().equals(TeamUserMember.MEMBER_OBJECT_TYPE)) {
				memberships.add(((TeamUserMember) member).getNegotiator());
			}
		}
		return memberships;
	}

	@Override
	public void notifyTeamMember(String iTeamUUID, String membershipUUID) {
		Team team = this.getTeam(iTeamUUID);
		if (team == null)
			throw new ExNotFoundException(iTeamUUID);

		Membership membership = membershipManager.findByUUID(membershipUUID, true);
		if (membership == null)
			throw new ExNotFoundException(membershipUUID);

		if (team.getObjectType().equals(TeamObjectType.CONTRACT)) {
			Contract contract = contractManager.getContractByUUID(team.getObjectID());
			notificationManager.process(
					new ContractNotificationEvent(NotificationType.CONTRACT_MEMBER_ADDED, contract, membership));
			ActivityLogger.log((Membership) ContextHelper.getMembership(), Verb.ADD, contract,
					getMemberships(team.getMembers()));
		} else if (team.getObjectType().equals(TeamObjectType.NEGOTIATION)) {
			Negotiation negotiation = negotiationManager.getNegotiation(team.getObjectID());
			notificationManager
					.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_MEMBER_ADDED, negotiation, membership));
			ActivityLogger.log((Membership) ContextHelper.getMembership(), Verb.ADD, negotiation,
					getMemberships(team.getMembers()));
		} else if (team.getObjectType().equals(TeamObjectType.INFORMATION_MODEL_TEMPLATE)) {
			InformationModelTemplate informationModelTemplate = informationModelManager.readByUUID(team.getObjectID());
			notificationManager.process(new InformationModelNotificationEvent(
					NotificationType.INFORMATION_MODEL_MEMBER_ADDED, informationModelTemplate, membership));
			ActivityLogger.log((Membership) ContextHelper.getMembership(), Verb.ADD, membership,
					informationModelTemplate, getMemberships(team.getMembers()));
		} else {
			ExLogger.get().warn("Invalid team type. ObjectType: {}, ObjectId: {}", team.getObjectType(),
					team.getObjectID());
		}

	}

}

package com.exrade.models.userprofile;

import com.exrade.models.Permission;
import com.exrade.models.Role;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.ArrayList;
import java.util.List;

public class Actor extends BaseEntityUUIDTimeStampable implements Negotiator {
	private Negotiator membership;
	private List<Role> exRoles = new ArrayList<>();
	private List<Permission> exPermissions = new ArrayList<>();
	private boolean active = true;

	public Actor(){}
	
	public Actor(Negotiator membership, Role role){
		setMembership(membership);
		addRole(role);
	}
	
	public List<Role> getExRoles(){
		return exRoles;
	}
	
	public List<Permission> getExPermissions(){
		return exPermissions;
	}
	
	@Override
	public List<Role> getRoles() {
		return getExRoles();
	}

	@Override
	public List<Permission> getPermissions() {
		return getExPermissions();
	}

	@Override
	public String getIdentifier() {
		return getUuid();
	}

	@Override
	public boolean isPublicProfile() {
		return getMembership().isPublicProfile();
	}

	@Override
	public String getName() {
		return getMembership().getName();
	}

	@Override
	public IProfile getProfile() {
		return getMembership().getProfile();
	}

	@Override
	public IUser getUser() {
		return getMembership().getUser();
	}

	@Override
	public IPlan getPlan() {
		return getMembership().getPlan();
	}

	public Negotiator getMembership() {
		return membership;
	}

	public void setMembership(Negotiator membership) {
		this.membership = membership;
	}

	public void addRole(Role role){
		getRoles().add(role);
	}
	
	public boolean removeRole(String roleName){
		
		for(Role role : getRoles()){
			if(role.getName().equals(roleName)){
				getRoles().remove(role);
				return true;
			}
		}
		return false;
	}
	
	public void addPermission(Permission permission){
		getPermissions().add(permission);
	}

	@Override
	public boolean isActive() {
		return getMembership().isActive() && active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isAgreementSigner() {
		return getMembership().isAgreementSigner();
	}

	@Override
	public Double getMaxNegotiationAmount() {
		return getMembership().getMaxNegotiationAmount();
	}

	@Override
	public boolean isGuest() {
		return getMembership().isGuest();
	}

	@Override
	public boolean isProfileActive() {
		return getMembership().isProfileActive();
	}
}

package com.exrade.models.userprofile.security;

import com.exrade.models.Permission;
import com.exrade.models.Role;
import com.exrade.platform.persistence.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ExRole extends BaseEntity implements Role {

	private String name;
	private List<Permission> permissions = new ArrayList<>();

	public ExRole(){}
	
	public ExRole(String iName) {
		name = iName;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public List<Permission> getPermissions() {
		return permissions;
	}
	
	public void addPermission(Permission permission) {
		getPermissions().add(permission);
	}
	
	public void removePermission(Permission permission) {
		getPermissions().remove(permission);
	}
	
	@Override
	public boolean equals(Object iObj) {
		if (iObj == null || !(iObj.getClass().isInstance(this))) {
			return false;
		}
		return Objects.equals(getName(), ((ExRole) iObj).getName());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

}

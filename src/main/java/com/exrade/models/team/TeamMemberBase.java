package com.exrade.models.team;

import com.exrade.models.Role;
import com.exrade.platform.persistence.BaseEntity;
import com.exrade.platform.persistence.TimeStampable;
import com.exrade.runtime.timer.TimeProvider;

import java.util.Date;

public abstract class TeamMemberBase extends BaseEntity implements TimeStampable, ITeamMember  {

	private Date creationDate = TimeProvider.now();
	
	private Date updateDate;
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}

	@Override
	public void setCreationDate(Date iDate) {
		creationDate = iDate;
	}

	@Override
	public void setUpdateDate(Date iDate) {
		updateDate = iDate;
	}
	
	@Override
	public boolean hasRole(String roleName) {
		for(Role role : getRoles()) {
			if(role.getName().equals(roleName))
				return true;
		}
		return false;
	}
	
}

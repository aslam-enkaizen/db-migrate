package com.exrade.models.workgroup;

import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.BaseEntityUUID;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post extends BaseEntityUUID {

	private WorkGroup workGroup;
	
	private Membership creator;
	
	private Date created;
	
	private Date updated;
	
	private String title;
	
	private String description;
	
	private List<String> files = new ArrayList<>();

	public WorkGroup getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}

	public Membership getCreator() {
		return creator;
	}

	public void setCreator(Membership creator) {
		this.creator = creator;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}
	
	public Boolean isCreator(Membership iMembership) {
		if(getCreator().equals(iMembership))
			return true;
	
		return false;
	}
	
	public Boolean isCreatorOrWorkGroupOwner(Membership iMembership) {
		return isCreator(iMembership) || getWorkGroup().isOwner(iMembership);
	}
}

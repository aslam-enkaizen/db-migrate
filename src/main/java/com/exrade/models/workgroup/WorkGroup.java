package com.exrade.models.workgroup;

import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.BaseEntityUUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class WorkGroup extends BaseEntityUUID {

	private String name;
	
	private String category;
	
	private String description;
	
	private Membership owner;
	
	private Date created;
	
	private Date updated;
	
	private String logo;
	
	private String cover;
	
	private Set<String> tags = new HashSet<String>();
	
	private List<Membership> members = new ArrayList<Membership>();
	
	private List<Negotiation> negotiations = new ArrayList<Negotiation>();
	
	public WorkGroup(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Membership getOwner() {
		return owner;
	}

	public void setOwner(Membership owner) {
		this.owner = owner;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date createDate) {
		this.created = createDate;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updateDate) {
		this.updated = updateDate;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public List<Membership> getMembers() {
		return members;
	}

	public void setMembers(List<Membership> members) {
		this.members = members;
	}
	
	public Boolean isMember(Membership iMembership) {
		for(Membership membership : getMembers())
			if(membership.equals(iMembership))
				return true;
		
		return false;
	}
	
	public Boolean isOwner(Membership iMembership) {
		if(getOwner().equals(iMembership))
			return true;
	
		return false;
	}
	
	public Boolean isOwnerOrMember(Membership iMembership) {
		return isOwner(iMembership) || isMember(iMembership);
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	@JsonIgnore
	public List<Negotiation> getNegotiations() {
		return negotiations;
	}

	public void setNegotiations(List<Negotiation> negotiations) {
		this.negotiations = negotiations;
	}
}

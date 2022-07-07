package com.exrade.models.projects;

import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.IPersistenceUUID;
import com.exrade.util.ObjectsUtil;

import javax.persistence.Id;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.List;

public class Project implements IPersistenceUUID {

	@Id
	private String id;
	@Version
	private Integer version;
	
	private String uuid = ObjectsUtil.generateUniqueID();
	private Negotiator owner;
	private String name;
	private List<Negotiation> negotiations=new ArrayList<>();
	private Priority priority;
	
	public Project(){}

	public Project(Negotiator owner, String name, Priority priority) {
		this.owner = owner;
		this.name = name;
		this.priority = priority;
	}
	
	public Negotiator getOwner() {
		return owner;
	}
	public void setOwner(Negotiator owner) {
		this.owner = owner;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Negotiation> getNegotiations() {
		return negotiations;
	}
	public void setNegotiations(List<Negotiation> negotiations) {
		this.negotiations = negotiations;
	}
	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	public String getId() {
		return id;
	}
	public Integer getVersion() {
		return version;
	}
	@Override
	public String getUuid() {
		return uuid;
	}
	
}

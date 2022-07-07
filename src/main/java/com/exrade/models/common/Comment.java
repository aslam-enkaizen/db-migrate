package com.exrade.models.common;

import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.IPersistenceUUID;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ObjectsUtil;

import javax.persistence.Id;
import javax.persistence.Version;
import java.util.Date;
import java.util.Objects;

/**
 * Purpose of this class is to comment about Negotiation or NegotiationMessage. (Comments
 * on NegotiationMesssage is not allowed in V1):Jason
 * 
 * @author Jason Finnegan
 */
public class Comment implements IPersistenceUUID {

	@Id
	private String id;

	@Version
	private Integer version;

	private String message;
	
	private String uuid = ObjectsUtil.generateUniqueID(); 

	private Date created;

	private Membership creator;

	public Comment() {
	}
	
	public static Comment createComment(String iMessage, Membership iCreator){
		Objects.requireNonNull(iMessage,"Message cant be null");
		Objects.requireNonNull(iCreator,"User comment author cant be null");
		
		Comment comment = new Comment();
		comment.message = iMessage;
		comment.created = TimeProvider.now();
		comment.creator = iCreator;
		return comment;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public Integer getVersion() {
		return version;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Negotiator getCreator() {
		return creator;
	}

	public void setCreator(Membership creator) {
		this.creator = creator;
	}

	@Override
	public String getUuid() {
		return uuid;
	}


}

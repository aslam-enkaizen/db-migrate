package com.exrade.models.authorisation;

import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.runtime.timer.TimeProvider;

public class AuthorisationResponse extends BaseEntityUUIDTimeStampable {
	
	protected Negotiator sender;
	
	protected String note;
	
	protected AuthorisationStatus status;

	public AuthorisationResponse(){}
	
	public AuthorisationResponse(Negotiator responder, AuthorisationStatus status, String note){
		setSender(responder);
		setStatus(status);
		setNote(note);
		setCreationDate(TimeProvider.now());
	}
	
	public Negotiator getSender() {
		return sender;
	}

	public void setSender(Negotiator sender) {
		this.sender = sender;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public AuthorisationStatus getStatus() {
		return status;
	}

	public void setStatus(AuthorisationStatus status) {
		this.status = status;
	}
	
}

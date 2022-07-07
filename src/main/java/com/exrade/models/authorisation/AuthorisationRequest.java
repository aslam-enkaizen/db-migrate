package com.exrade.models.authorisation;

import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ContextHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class AuthorisationRequest extends BaseEntityUUIDTimeStampable {
	protected String title;

	protected List<Negotiator> receivers = new ArrayList<Negotiator>();

	protected Negotiator sender;

	protected List<AuthorisationResponse> responses = new ArrayList<AuthorisationResponse>();

	protected String note;

	protected String objectID;

	protected AuthorisationObjectType objectType;

	protected AuthorisationStatus status = AuthorisationStatus.PENDING;

	protected AcceptanceRule acceptanceRule = AcceptanceRule.ANY;

	protected Date responseDeadline;

	protected Priority priority;

	protected Map<String, Object> extraContext = new HashMap<>();

	public AuthorisationRequest() {
	}

	public AuthorisationRequest(Negotiator iSender, List<Negotiator> iReceivers, String iObjectID,
			AcceptanceRule iAcceptanceRule, String iNote) {
		setCreationDate(TimeProvider.now());
		setSender(iSender);
		setReceivers(iReceivers);
		setObjectID(iObjectID);
		setAcceptanceRule(iAcceptanceRule);
		setNote(iNote);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public AcceptanceRule getAcceptanceRule() {
		return acceptanceRule;
	}

	public void setAcceptanceRule(AcceptanceRule acceptanceRule) {
		this.acceptanceRule = acceptanceRule;
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public AuthorisationObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(AuthorisationObjectType objectType) {
		this.objectType = objectType;
	}

	public AuthorisationStatus getStatus() {
		return status;
	}

	public void setStatus(AuthorisationStatus status) {
		this.status = status;
	}

	public List<Negotiator> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<Negotiator> receivers) {
		this.receivers = receivers;
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

	public List<AuthorisationResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<AuthorisationResponse> responses) {
		this.responses = responses;
	}

	public Date getResponseDeadline() {
		return responseDeadline;
	}

	public void setResponseDeadline(Date responseDeadline) {
		this.responseDeadline = responseDeadline;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public Map<String, Object> getExtraContext() {
		return extraContext;
	}

	public void setExtraContext(Map<String, Object> extraContext) {
		this.extraContext = extraContext;
	}

	public boolean isCanAuthorise() {
		Negotiator requestor = ContextHelper.getMembership();
		if (requestor != null && getStatus() == AuthorisationStatus.PENDING
				&& getAuthorisersWithPendingRequest().contains(requestor)) {

			return true;
		}

		return false;
	}

	@JsonIgnore
	public List<Negotiator> getAuthorisersWithPendingRequest() {
		List<Negotiator> authorisers = new ArrayList<>();

		for (Negotiator requestReceiver : getReceivers()) {
			boolean foundResponse = false;

			for (AuthorisationResponse response : getResponses()) {
				if (response.getSender().equals(requestReceiver)) {
					if (response.getStatus() == AuthorisationStatus.PENDING)
						authorisers.add(requestReceiver);
					foundResponse = true;
					break;
				}
			}

			if (!foundResponse)
				authorisers.add(requestReceiver);
		}

		return authorisers;
	}

}

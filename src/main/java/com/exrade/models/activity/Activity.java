package com.exrade.models.activity;

import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.runtime.timer.TimeProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activity extends BaseEntityUUID {

	private ASObject actor;
	private ASObject object;
	private ASObject target;
	private Map<String, String> extraContext = new HashMap<String, String>();
	private Verb verb;
	private Date  published; 
	private String url;
	private String message;
	
	public static Activity create(ASObject actor, Verb verb, ASObject object, ASObject target){
		Activity activity = new Activity();
		activity.setActor(actor);
		activity.setObject(object);
		activity.setPublished(TimeProvider.now());
		activity.setTarget(target);
		activity.setVerb(verb);
		return activity;
	}
	
	public ASObject getActor() {
		return actor;
	}
	public void setActor(ASObject actor) {
		this.actor = actor;
	}
	public ASObject getObject() {
		return object;
	}
	public void setObject(ASObject object) {
		this.object = object;
	}
	public ASObject getTarget() {
		return target;
	}
	public void setTarget(ASObject target) {
		this.target = target;
	}
	public Verb getVerb() {
		return verb;
	}
	public void setVerb(Verb verb) {
		this.verb = verb;
	}
	public Date getPublished() {
		return published;
	}
	public void setPublished(Date published) {
		this.published = published;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getExtraContext() {
		return extraContext;
	}

	public void setExtraContext(Map<String, String> extraContext) {
		this.extraContext = extraContext;
	}
	
	public void addExtraContext(String key, String value){
		getExtraContext().put(key, value);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

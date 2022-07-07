package com.exrade.runtime.notification.event;

import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.workgroup.Post;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.models.workgroup.WorkGroupComment;

public class WorkGroupNotificationEvent extends NotificationEvent<WorkGroup> {

	private Post post;
	private WorkGroupComment comment;
	private Negotiation negotiation;
	private Membership member;
	
	public WorkGroupNotificationEvent(NotificationType source, WorkGroup payload) {
		super(source, payload);
	}
	
	public WorkGroupNotificationEvent(NotificationType source, WorkGroup payload, Negotiation negotiation) {
		super(source, payload);
		this.negotiation = negotiation;
	}
	
	public WorkGroupNotificationEvent(NotificationType source, WorkGroup payload, Membership member) {
		super(source, payload);
		this.member = member;
	}
	
	public WorkGroupNotificationEvent(NotificationType source, Post post) {
		super(source, post.getWorkGroup());
		this.post = post;
	}
	
	public WorkGroupNotificationEvent(NotificationType source, WorkGroupComment comment) {
		super(source, comment.getPost().getWorkGroup());
		this.post = comment.getPost();
		this.comment = comment;
	}
	
	public WorkGroup getWorkGroup(){
		return getPayload();
	}

	public Post getPost() {
		return post;
	}

	public WorkGroupComment getComment() {
		return comment;
	}

	public Negotiation getNegotiation() {
		return negotiation;
	}

	public Membership getMember() {
		return member;
	}
	
}

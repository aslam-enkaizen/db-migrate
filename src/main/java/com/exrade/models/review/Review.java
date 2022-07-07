package com.exrade.models.review;

import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

public class Review extends BaseEntityUUIDTimeStampable {
	
	private Negotiator reviewer;
	
	private Negotiation negotiation;
	
	@JsonManagedReference
	private ReviewRequest reviewRequest;

	private Offer offer;
	
	private Double score;
	
	private String comment;

	private ReviewStatus status = ReviewStatus.PENDING;
	
	private List<String> files = new ArrayList<>();
	
	public Review(){}

	public Negotiator getReviewer() {
		return reviewer;
	}

	public void setReviewer(Negotiator reviewer) {
		this.reviewer = reviewer;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Negotiation getNegotiation() {
		return negotiation;
	}

	public ReviewRequest getReviewRequest() {
		return reviewRequest;
	}

	public void setReviewRequest(ReviewRequest reviewRequest) {
		this.reviewRequest = reviewRequest;
	}

	public String getReviewRequestUUID() {
		return getReviewRequest().getUuid();
	}

	public void setNegotiation(Negotiation negotiation) {
		this.negotiation = negotiation;
	}
	
	
	public String getNegotiationUUID(){
		return getNegotiation() != null ? getNegotiation().getUuid() : null;
	}
	
	public String getOfferUUID(){
		return getOffer() != null ? getOffer().getUuid() : null;
	}

	public ReviewStatus getStatus() {
		return status;
	}

	public void setStatus(ReviewStatus status) {
		this.status = status;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}
	
	public String getReviewTemplateUUID() {
		return getReviewRequest().getReviewTemplateUUID();
	}
	
}

package com.exrade.models.review;

import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReviewRequest extends BaseEntityUUIDTimeStampable {

	private List<Negotiator> reviewers = new ArrayList<Negotiator>();

	private Negotiator creator;
	
	private Negotiation negotiation;
	
	private List<Offer> offers = new ArrayList<>();
	
	@JsonBackReference
	private List<Review> reviews = new ArrayList<>();

	private ReviewStatus status = ReviewStatus.PENDING;
	
	private Date responseDeadline;
	
	private String note;
	
	private String reviewTemplateUUID;
	
	public ReviewRequest(){}

	public List<Negotiator> getReviewers() {
		return reviewers;
	}

	public void setReviewers(List<Negotiator> reviewers) {
		this.reviewers = reviewers;
	}

	public Negotiator getCreator() {
		return creator;
	}

	public void setCreator(Negotiator creator) {
		this.creator = creator;
	}

	public Negotiation getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(Negotiation negotiation) {
		this.negotiation = negotiation;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	public ReviewStatus getStatus() {
		return status;
	}

	public void setStatus(ReviewStatus status) {
		this.status = status;
	}

	public Date getResponseDeadline() {
		return responseDeadline;
	}

	public void setResponseDeadline(Date responseDeadline) {
		this.responseDeadline = responseDeadline;
	}
	
	public String getNegotiationUUID(){
		return getNegotiation().getUuid();
	}
	
	public List<String> getOfferUUIDs(){
		List<String> offerUUIDs = new ArrayList<>();
		
		for(Offer offer : getOffers()){
			offerUUIDs.add(offer.getUuid());
		}
		
		return offerUUIDs;
	}
	
	public Map<String, ReviewStatus> getReviewStatusPerReviewer(){
		Map<String, ReviewStatus> statuses = new java.util.HashMap<>();
		for(Negotiator reviewer : getReviewers()){
			statuses.put(reviewer.getIdentifier(), ReviewStatus.COMPLETED);
		}
		
		for(Review review : getReviews()){
			if(statuses.get(review.getReviewer().getIdentifier()) == ReviewStatus.COMPLETED && review.getStatus() == ReviewStatus.PENDING){
				statuses.put(review.getReviewer().getIdentifier(), ReviewStatus.PENDING);
			}
		}
		return statuses;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getReviewTemplateUUID() {
		return reviewTemplateUUID;
	}

	public void setReviewTemplateUUID(String reviewTemplateUUID) {
		this.reviewTemplateUUID = reviewTemplateUUID;
	}

}

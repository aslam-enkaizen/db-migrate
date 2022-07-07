package com.exrade.runtime.review;

import com.exrade.core.ExLogger;
import com.exrade.models.activity.ObjectType;
import com.exrade.models.activity.Verb;
import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.review.Review;
import com.exrade.models.review.ReviewRequest;
import com.exrade.models.review.ReviewStatus;
import com.exrade.models.review.Reviewer;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.common.SyncOperationControllerFactory;
import com.exrade.runtime.filemanagement.FileManager;
import com.exrade.runtime.filemanagement.IFileManager;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.ReviewNotificationEvent;
import com.exrade.runtime.rest.RestParameters.ReviewFilters;
import com.exrade.runtime.rest.RestParameters.ReviewRequestFields;
import com.exrade.runtime.rest.RestParameters.ReviewRequestFilters;
import com.exrade.runtime.review.persistence.ReviewPersistenceManager;
import com.exrade.runtime.review.persistence.ReviewQuery;
import com.exrade.runtime.review.persistence.ReviewRequestQuery;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.exrade.platform.security.Security.hasAccessPermission;

public class ReviewManager implements IReviewManager {

	private ReviewPersistenceManager reviewPersistentManager;
	private INegotiationManager negotiationManager = new NegotiationManager();
	private NotificationManager notificationManager = new NotificationManager();
	private IMembershipManager membershipManager = new MembershipManager();
	private IFileManager fileManager = new FileManager();

	public ReviewManager() {
		this(new ReviewPersistenceManager());
	}

	public ReviewManager(ReviewPersistenceManager iReviewPersistentManager) {
		this.reviewPersistentManager = iReviewPersistentManager;
	}

	@Override
	public ReviewRequest createReviewRequest(ReviewRequest iRequest) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.REVIEWS);

		Negotiation negotiation = negotiationManager.getNegotiation(iRequest.getNegotiationUUID());
		
		if(negotiation == null 
				|| (!negotiation.isOwner(ContextHelper.getMembership()) 
				&& !negotiation.isParticipant(ContextHelper.getMembership())))
			throw new ExException(ErrorKeys.NOT_AUTHORIZED);
		
		List<ReviewRequest> reviewRequests = getReviewRequestsForNegotiation(iRequest.getNegotiationUUID(), ContextHelper.getMembership().getProfile().getUuid());
		if(ExCollections.isNotEmpty(reviewRequests)){
			for(ReviewRequest req : reviewRequests){
				if(req.getStatus() == ReviewStatus.PENDING)
					throw new ExException(ErrorKeys.NOT_AUTHORIZED);
			}
		}
		
		iRequest.setCreationDate(TimeProvider.now());
		iRequest.setStatus(ReviewStatus.PENDING);
		//TODO: check if the user is permitted to create reviewrequest
		ReviewRequest createdReviewRequest = reviewPersistentManager.create(iRequest);
		for(Negotiator reviewer : createdReviewRequest.getReviewers()){
			for(Offer offer : createdReviewRequest.getOffers()){
				Review review = new Review();
				review.setReviewer(reviewer);
				review.setOffer(offer);
				review.setReviewRequest(createdReviewRequest);
				review.setNegotiation(createdReviewRequest.getNegotiation());
				review.setStatus(ReviewStatus.PENDING);
				createdReviewRequest.getReviews().add(review);
			}
		}
		reviewPersistentManager.update(createdReviewRequest);
		
		notificationManager.process(new ReviewNotificationEvent(NotificationType.REVIEW_REQUESTED, createdReviewRequest));
		ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.REQUEST, createdReviewRequest, createdReviewRequest.getReviewers());
		
		return getReviewRequestByUUID(createdReviewRequest.getUuid());
	}

	@Override
	public ReviewRequest getReviewRequestByUUID(String iRequestUUID) {
		//for get don't need to check permission
		//TODO: check authorisation of requestor
		return reviewPersistentManager.readObjectByUUID(ReviewRequest.class, iRequestUUID);
	}

	@Override
	public List<ReviewRequest> listReviewRequests(QueryFilters iFilters) {
		//for list don't need to check permission
		//TODO: check filters
		return reviewPersistentManager.listObjects(new ReviewRequestQuery(), iFilters);
	}

	@Override
	public Review createReview(String iReviewRequestUUID, Review iReview) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.REVIEWS);

		ReviewRequest reviewRequest = getReviewRequestByUUID(iReviewRequestUUID);
		if(reviewRequest.getStatus() == ReviewStatus.COMPLETED 
				|| !reviewRequest.getReviewers().contains(ContextHelper.getMembership()) 
				|| !iReview.getReviewer().equals(ContextHelper.getMembership()))
			throw new ExException(ErrorKeys.NOT_AUTHORIZED);
		
		iReview.setReviewRequest(reviewRequest);
		iReview.setStatus(ReviewStatus.PENDING);
		iReview.setNegotiation(reviewRequest.getNegotiation());
		
		Review createdReview = reviewPersistentManager.create(iReview);
		
		reviewRequest.getReviews().add(createdReview);
		reviewRequest.setUpdateDate(TimeProvider.now());
		reviewPersistentManager.update(reviewRequest);
		
		fileManager.updateFileMetadata(createdReview);
		return createdReview;
	}
	
	@Override
	public Review updateReview(Review iReview) {
		//for update don't need to check permission
		Review existingReview = this.getReviewByUUID(iReview.getUuid());
		
		if(existingReview.getStatus() == ReviewStatus.COMPLETED 
				|| !existingReview.getReviewer().equals(iReview.getReviewer())
				|| !existingReview.getReviewer().equals(ContextHelper.getMembership()))
			throw new ExException(ErrorKeys.NOT_AUTHORIZED);
		
		ReviewSyncOperationController controller = (ReviewSyncOperationController) SyncOperationControllerFactory
				.getInstance().createSyncOperationController(existingReview.getReviewRequestUUID(), ObjectType.REVIEW);
		 
		existingReview = controller.updateReview(iReview);
		fileManager.updateFileMetadata(existingReview);
		
		if(existingReview.getReviewRequest().getReviewStatusPerReviewer().get(ContextHelper.getMembershipUUID()) == ReviewStatus.COMPLETED) {
			ExLogger.get().info("Creating activity log for review: " + iReview.getUuid());
			ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.REVIEW, existingReview.getReviewRequest(), Arrays.asList(existingReview.getReviewRequest().getCreator()));
		}
		
		return existingReview;
	}

	@Override
	public Review getReviewByUUID(String iReviewUUID) {
		//for get don't need to check permission

		return reviewPersistentManager.readObjectByUUID(Review.class, iReviewUUID);
	}
	
	@Override
	public List<Review> listReviews(String iReviewRequestUUID, QueryFilters iFilters) {
		//for list don't need to check permission

		iFilters.put(ReviewFilters.REVIEW_REQUEST_UUID, iReviewRequestUUID);
		return reviewPersistentManager.listObjects(new ReviewQuery(), iFilters);
	}

	@Override
	public List<Review> listReviews(QueryFilters iFilters) {
		//for list don't need to check permission;

		return reviewPersistentManager.listObjects(new ReviewQuery(), iFilters);
	}

	private List<ReviewRequest> getReviewRequestsForNegotiation(String negotiationUUID, String profileUUID){
		QueryFilters queryFilters = QueryFilters.create(ReviewRequestFields.NEGOTIATION_UUID, negotiationUUID);
		queryFilters.put(ReviewRequestFilters.CREATOR_PROFILE_UUID, profileUUID);
		return listReviewRequests(queryFilters);
	}

	@Override
	public void updateReviewers(String negotiationID, List<Reviewer> reviewers) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.REVIEWS);

		Negotiation negotiation = negotiationManager.getNegotiation(negotiationID);
		if(negotiation.isClosed())
			throw new ExException("Negotiation is closed!");
		
		negotiationManager.updateReviewers(negotiation, bindNegotiators(reviewers));
		negotiationManager.storeNegotiation(negotiation);
	}
	
	public List<Negotiator> bindNegotiators(List<Reviewer> reviewers){
		List<Negotiator> offerReviewers = new ArrayList<>();
		
		for(Reviewer reviewer : reviewers){
			// use email and profileId for relating membership, guest etc.
			// update template or update signer??
			Negotiator negotiator = null;
			
			if(!Strings.isNullOrEmpty(reviewer.getMembershipIdentifier())) {
				negotiator = membershipManager.findByUUID(reviewer.getMembershipIdentifier(), true);
			}
			
			if(negotiator == null && !Strings.isNullOrEmpty(reviewer.getEmail())) {
				negotiator = membershipManager.getMembershipByEmail(reviewer.getEmail(), ContextHelper.getMembership().getProfile().getUuid(), true);
			}
			
			if(negotiator == null) {
				negotiator = membershipManager.createGuestMembership(reviewer.getFirstName(), reviewer.getLastName(), reviewer.getEmail(), reviewer.getPhone(), reviewer.getTitle(), ContextHelper.getMembership().getProfile());
			}
			
			if(negotiator != null) {
				offerReviewers.add(negotiator);
				reviewer.setMembershipIdentifier(negotiator.getIdentifier()); // update negotiator identifier reference
			}
		}
		
		return offerReviewers;
	}
}

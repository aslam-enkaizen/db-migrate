package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.PlanSubscription;
import com.exrade.models.userprofile.security.MemberRole;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.MembershipFilters;
import com.exrade.runtime.userprofile.*;
import com.exrade.runtime.userprofile.persistence.query.MemberProfileQuery.MemberProfileQFilters;
import com.google.common.base.Strings;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProfileDowngradeJob extends ExradeJob implements Job {

	private static Logger logger = ExLogger.get();
	private IMembershipManager membershipManager = new MembershipManager();
	private IProfileManager profileManager = new ProfileManager();
	private SubscriptionManager subscriptionManager = new SubscriptionManager();
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		
		logger.info("ProfileDowngradeJob starting...");
		try{
			setupContext(null);
			
			QueryFilters filters = QueryFilters.create(MemberProfileQFilters.PROFILE_TRIAL_EXPIRATION_DATE, getDate(Calendar.DAY_OF_MONTH, -1, false));
			filters.put(MembershipFilters.ROLENAME, MemberRole.OWNER);
			List<Membership> memberships = membershipManager.find(filters);
			
			for(Membership membership : memberships){
				try {
					if(membership.isActive() && membership.getPlan() != null 
							&& !membership.getPlan().isFree() 
							&& Strings.isNullOrEmpty(membership.getProfile().getPlanSubscription().getExternalSubscriptionID())){
						logger.info(membership.getEmail() + " | " + membership.getFullName() + " | " + membership.getPlan().getTitle() + " | " + membership.getProfile().getPlanSubscription().getCreationDate());
						
						subscriptionManager.cancelSubscription(membership.getProfile().getPlanSubscription().getUuid());
						
						PlanSubscription freeSubscription = subscriptionManager.createDefaultFreeSubscription(membership.getProfile());
						membership.getProfile().setPlanSubscription(freeSubscription);
						
						membershipManager.disableAdditionalMembersFromProfile(profileManager.update(membership.getProfile()));
					}
				}
				catch(Exception ex){
					logger.warn("ProfileDowngradeJob failed! ProfileUUID: " + membership.getProfileUUID(), ex);
				}
			}
		}
		catch(Exception ex){
			logger.warn("ProfileDowngradeJob failed!", ex);
		}
		
//		JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
//
//		String negotiationUUID = data
//				.getString(NegotiationFilters.NEGOTIATION_UUID);
//		String notificationName = data
//				.getString(NotificationConstants.NOTIFICATION_NAME);
//		String notificationType = data
//				.getString(NotificationConstants.NOTIFICATION_TYPE);
//		
//		INegotiationManager negotiationManager = new NegotiationManager();
//		Negotiation negotiation = negotiationManager.getNegotiation(negotiationUUID);
//		setupContext(negotiation.getOwner().getIdentifier());
//		
//		logger.info(
//				"Sending notification with params - NegotiationID: {}, NotificationType: {}, NotificationName: {}.",
//				negotiationUUID, notificationType, notificationName);
//
//		try {
//			NotificationMessageProcessor notificationProcessor = new NotificationMessageProcessor(
//					notificationType, notificationName);
//			notificationProcessor.process(negotiationUUID);
//		} catch (Exception e) {
//			logger.error(
//					"Sending notification failed because of the error - {}",
//					e.getMessage(), e);
//			throw new ExScheduleJobException(ErrorKeys.JOB_EXECUTION_FAIL, e);
//		}

		logger.info("ProfileDowngradeJob ending...");
	}
	
	private Date getDate(int calendarField, int period, boolean withTime){
		Calendar startTimeCal = Calendar.getInstance();
		startTimeCal.setTime(TimeProvider.now());
		startTimeCal.add(calendarField, period);
		
		if(!withTime){
			startTimeCal.set(Calendar.HOUR_OF_DAY, 0);
			startTimeCal.set(Calendar.MINUTE, 0);
			startTimeCal.set(Calendar.SECOND, 0);
			startTimeCal.set(Calendar.MILLISECOND, 0);
		}
		
		return startTimeCal.getTime();
	}
}

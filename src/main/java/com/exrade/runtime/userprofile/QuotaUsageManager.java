package com.exrade.runtime.userprofile;

import com.exrade.Messages;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.PrivacyLevel;
import com.exrade.models.template.TemplateType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.security.ExResourcePermission;
import com.exrade.models.userprofile.usage.UsageDetail;
import com.exrade.models.userprofile.usage.UsageSummary;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security.ProfilePermissions;
import com.exrade.runtime.filemanagement.FileStorageProvider;
import com.exrade.runtime.filemanagement.IFileStorageController;
import com.exrade.runtime.negotiation.persistence.NegotiationPersistenceManager;
import com.exrade.runtime.negotiation.persistence.NegotiationQuery.NegotiationQFilters;
import com.exrade.runtime.rest.RestParameters.MembershipFilters;
import com.exrade.runtime.rest.RestParameters.NegotiationFilters;
import com.exrade.runtime.template.ITemplateManager;
import com.exrade.runtime.template.TemplateManager;
import com.exrade.runtime.template.persistence.TemplatePersistenceManager.TemplateQFilters;
import com.exrade.util.DateUtil;
import com.exrade.util.PlanSubscriptionUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QuotaUsageManager {

	public static final String	USAGE_NEGOTIATIONS_STARTED="USAGE_NEGOTIATIONS_STARTED";
	public static final String	USAGE_NEGOTIATIONS_JOINED="USAGE_NEGOTIATIONS_JOINED";
	public static final String	USAGE_STORAGE_USED="USAGE_STORAGE_USED";
	public static final String  USAGE_PROFILE_MEMBERS="USAGE_PROFILE_MEMBERS";
	public static final String  USAGE_HEADED_PAPER_TEMPLATES="USAGE_HEADED_PAPER_TEMPLATES";
//	private static final String USAGE_NEGOTIATIONS_STARTED_PUBLIC = "USAGE_NEGOTIATIONS_STARTED_PUBLIC";
//	private static final String USAGE_NEGOTIATIONS_STARTED_PUBLIC_RESTRICTED = "USAGE_NEGOTIATIONS_STARTED_PUBLIC_RESTRICTED";
	//private static final String USAGE_NEGOTIATIONS_STARTED_PRIVATE = "USAGE_NEGOTIATIONS_STARTED_PRIVATE";
	protected static final IFileStorageController fileStorageController = FileStorageProvider.getFileStorageController();

	/**
	 * Get number of negotiations started in the current month
	 * @param iActor
	 * @param privacyLevel   optionally get number started at this privacy Level
	 * @return
	 */
	public static Integer startedNegotiationsThisMonth(Negotiator iActor, PrivacyLevel privacyLevel){
		return startedNegotiationsSinceDate(iActor, getMonthBeginning(), privacyLevel);
	}

	public static Integer startedNegotiationsThisBillingCycle(Negotiator iActor, PrivacyLevel privacyLevel){
		return startedNegotiationsSinceDate(iActor, PlanSubscriptionUtil.getCurrentBillingCycleStartDate(iActor), privacyLevel);
	}

	/**
	 * Get number of negotiations started in the current month
	 * @param iActor
	 * @param privacyLevel   optionally get number started at this privacy Level
	 * @return
	 */
	public static Integer startedNegotiationsSinceDate(Negotiator iActor, Date startDate, PrivacyLevel privacyLevel){
		NegotiationPersistenceManager negotiationPM = new NegotiationPersistenceManager();
		QueryFilters filters = QueryFilters.create(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
		filters.put(NegotiationFilters.OWNED, iActor.getIdentifier());
		filters.put(NegotiationQFilters.CREATED_AFTER_INCLUSIVE, startDate);

		if(privacyLevel!=null){
			filters.put(NegotiationFilters.PRIVACY_LEVEL, Arrays.asList(privacyLevel));
		}

		List<Negotiation> negotiations = negotiationPM.listNegotiations(filters,iActor);

		return negotiations.size();
	}


	public static Integer joinedNegotiationsThisMonth(Negotiator iActor){
		return joinedNegotiationsSinceDate(iActor, getMonthBeginning());
	}

	public static Integer joinedNegotiationsThisBillingCycle(Negotiator iActor){
		return joinedNegotiationsSinceDate(iActor, PlanSubscriptionUtil.getCurrentBillingCycleStartDate(iActor));
	}

	public static Integer joinedNegotiationsSinceDate(Negotiator iActor, Date startDate){

		NegotiationPersistenceManager negotiationPM = new NegotiationPersistenceManager();
		QueryFilters filters = QueryFilters.create(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
		filters.put(NegotiationFilters.PARTICIPATED, iActor.getId());
		filters.put(NegotiationQFilters.CREATED_AFTER_INCLUSIVE,getMonthBeginning());
		List<Negotiation> negotiations = negotiationPM.listNegotiations(filters,iActor);

		if(negotiations == null) {
			return 0;
		}
		return negotiations.size();
	}

	/**
	 * Return used space for all the members of the profile in Bytes
	 * @param iActor
	 * @return bytes
	 */
	public static Integer getProfileSpaceUsage(Negotiator iActor){
		MembershipManager membershipManager = new MembershipManager();
		QueryFilters filters = QueryFilters.create(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
		filters.put(MembershipFilters.PROFILE, iActor.getProfile().getUuid());

		List<Membership> memberships = membershipManager.find(filters);

		Integer profileSpaceUsage = 0;
		for (Membership membership : memberships) {
			profileSpaceUsage += fileStorageController.getMembershipSpaceUsage(membership);
		}

		return profileSpaceUsage;
	}

	/**
	 * Return used space for all the members of the profile in MegaBytes
	 * @param iActor
	 * @return Magabytes
	 */
	public static Integer getProfileSpaceUsageMB(Negotiator iActor){
		return  getProfileSpaceUsage(iActor)/1048576;
	}

	public static Integer getProfileMembersCount(Negotiator iActor){
		MembershipManager membershipManager = new MembershipManager();
		QueryFilters filters = QueryFilters.create(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
		filters.put(MembershipFilters.PROFILE, iActor.getProfile().getUuid());
		filters.put(MembershipFilters.IS_ACTIVE, true);

		List<Membership> memberships = membershipManager.find(filters);

		return memberships.size();
	}

	public static Integer getHeadedPaperTemplateCount(Negotiator iActor){
		ITemplateManager templateManager = new TemplateManager();
		QueryFilters filters = QueryFilters.create(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
		filters.put(TemplateQFilters.TEMPLATE_TYPE, TemplateType.NegotiationAgreement);

		filters.put(TemplateQFilters.OWNER_PROFILE, iActor.getProfile().getUuid());

		try {
			return templateManager.listTemplates(filters).size();
		}
		catch(Exception ex) {
			return 0;
		}
	}

	/**
	 * Creates a summary of the users current usage amounts and limits of their current plan
	 *  //TODO Persist and update only when needed
	 * @param iActor
	 * @return
	 */
	public static UsageSummary getUsageSummary(Negotiator iActor){
		UsageSummary summary=new UsageSummary(iActor.getProfile());

		//Negotiations started
		ExResourcePermission negotiationCreate = (ExResourcePermission)iActor.getPlan().getPermission(ProfilePermissions.NEGOTIATIONS_MAX_CREATE);
		UsageDetail negStartedDetails=new UsageDetail(Messages.get(USAGE_NEGOTIATIONS_STARTED),ProfilePermissions.NEGOTIATIONS_MAX_CREATE,negotiationCreate.getLimit().toString(),
				startedNegotiationsThisMonth(iActor,null).toString());

		summary.getUsageDetails().add(negStartedDetails);

		//removed as not required now
		//Negotiations joined
//		ExResourcePermission negotiationJoined = (ExResourcePermission)iActor.getPlan().
//				getPermission(ProfilePermissions.NEGOTIATIONS_MAX_JOIN);
//		UsageDetail negJoinedDetails=new UsageDetail(Messages.get(USAGE_NEGOTIATIONS_JOINED),ProfilePermissions.NEGOTIATIONS_MAX_JOIN,
//				negotiationJoined.getLimit().toString(),joinedNegotiationsThisMonth(iActor).toString());
//
//		summary.getUsageDetails().add(negJoinedDetails);


		//Storage Used
		ExResourcePermission negotiationStorage = (ExResourcePermission)iActor.getPlan().
				getPermission(ProfilePermissions.FILES_STORAGE_SPACE);
		UsageDetail storageUsed=new UsageDetail(Messages.get(USAGE_STORAGE_USED),ProfilePermissions.FILES_STORAGE_SPACE,
				negotiationStorage.getLimit().toString(),getProfileSpaceUsage(iActor).toString());

		summary.getUsageDetails().add(storageUsed);

		//Profile memberships
		ExResourcePermission maxProfileMembers = (ExResourcePermission)iActor.getPlan().
				getPermission(ProfilePermissions.PROFILE_MAX_MEMBER);
		UsageDetail maxProfileMembersUsed = new UsageDetail(Messages.get(USAGE_PROFILE_MEMBERS), ProfilePermissions.PROFILE_MAX_MEMBER,
				maxProfileMembers.getLimit().toString(), getProfileMembersCount(iActor).toString());

		summary.getUsageDetails().add(maxProfileMembersUsed);

		//Headed Paper Template
		ExResourcePermission maxHeadedPaperTemplates = (ExResourcePermission)iActor.getPlan().
				getPermission(ProfilePermissions.NEGOTIATIONS_MAX_HEADED_PAPER_TEMPLATE);
		UsageDetail maxHeadedPaperTemplatesUsed = new UsageDetail(Messages.get(USAGE_HEADED_PAPER_TEMPLATES), ProfilePermissions.NEGOTIATIONS_MAX_HEADED_PAPER_TEMPLATE,
				maxHeadedPaperTemplates.getLimit().toString(), getHeadedPaperTemplateCount(iActor).toString());

		summary.getUsageDetails().add(maxHeadedPaperTemplatesUsed);

		return summary;
	}

	private static Date getMonthBeginning(){
		Calendar calendar = getCalendarForNow();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return DateUtil.toBeginningOfTheDay(calendar.getTime());
	}


	private static Calendar getCalendarForNow() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar;
	}
}

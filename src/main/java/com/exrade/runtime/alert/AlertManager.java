package com.exrade.runtime.alert;

import com.exrade.core.ExLogger;
import com.exrade.models.activity.Activity;
import com.exrade.models.activity.ObjectType;
import com.exrade.models.activity.Verb;
import com.exrade.models.authorisation.AuthorisationRequest;
import com.exrade.models.authorisation.AuthorisationStatus;
import com.exrade.models.contract.Contract;
import com.exrade.models.contract.ContractUserMember;
import com.exrade.models.contract.IContractMember;
import com.exrade.models.invitations.InvitationStatus;
import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.payment.PaymentStatus;
import com.exrade.models.review.Review;
import com.exrade.models.review.ReviewRequest;
import com.exrade.models.review.ReviewStatus;
import com.exrade.models.signatures.NegotiationSignatureContainer;
import com.exrade.models.upcomingevent.UpcomingLifecycleEvent;
import com.exrade.models.upcomingevent.dto.UpcomingLifecycleEventNotificationDTO;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.security.MemberRole;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.activity.ActivityManager;
import com.exrade.runtime.activity.IActivityManager;
import com.exrade.runtime.activity.persistence.ActivityQuery.ActivityQFilters;
import com.exrade.runtime.authorisation.AuthorisationManager;
import com.exrade.runtime.authorisation.IAuthorisationManager;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.contract.IContractManager;
import com.exrade.runtime.invitation.IMemberInvitationManager;
import com.exrade.runtime.invitation.INegotiationInvitationManager;
import com.exrade.runtime.invitation.MemberInvitationManager;
import com.exrade.runtime.invitation.NegotiationInvitationManager;
import com.exrade.runtime.invitation.persistence.InvitationQuery.InvitationQFilters;
import com.exrade.runtime.negotiation.persistence.NegotiationPersistenceManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.*;
import com.exrade.runtime.payment.PaymentManager;
import com.exrade.runtime.rest.RestParameters.*;
import com.exrade.runtime.review.IReviewManager;
import com.exrade.runtime.review.ReviewManager;
import com.exrade.runtime.signatures.ISignatureManager;
import com.exrade.runtime.signatures.SignatureManager;
import com.exrade.runtime.signatures.persistence.SignatureQuery.SignatureQFilters;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.upcomingevent.IUpcomingLifecycleEventManager;
import com.exrade.runtime.upcomingevent.UpcomingLifecycleEventManager;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.IProfileManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.runtime.userprofile.ProfileManager;
import com.exrade.runtime.userprofile.persistence.query.MemberProfileQuery.MemberProfileQFilters;
import com.exrade.runtime.userprofile.persistence.query.ProfileQuery.ProfileQFilters;
import com.exrade.util.DateUtil;
import com.exrade.util.ExCollections;

import java.util.*;


public class AlertManager {

    private static String REMINDER = "reminder";

    private IMembershipManager membershipManager = new MembershipManager();
    private IProfileManager profileManager = new ProfileManager();
    private ISignatureManager signatureManager = new SignatureManager();
    private INegotiationInvitationManager negotiationInvitationManager = new NegotiationInvitationManager();
    private IMemberInvitationManager memberInvitationManager = new MemberInvitationManager();
    private NotificationManager notificationManager = new NotificationManager();
    private IActivityManager activityManager = new ActivityManager();
    private IContractManager contractManager = new ContractManager();
    private IReviewManager reviewManager = new ReviewManager();
    private IAuthorisationManager authorisationManager = new AuthorisationManager();
    private final IUpcomingLifecycleEventManager upcomingLifecycleEventManager = new UpcomingLifecycleEventManager();
    private final NegotiationPersistenceManager negotiationPersistenceManager = new NegotiationPersistenceManager();

    public AlertManager() {

    }


    public void generateAlerts() {
        generateExpiringTrialPeriodAlert();
        generatePendingSignatureAlert();
        generatePendingNegotiationInvitationAlert();
        generateContractEndingAlert();
        generateContractTerminationNoticeDeadlineAlert();
        generatePendingReviewRequestAlert();
        generatePendingAuthorisationRequestAlert();
        generateUpcomingContractLifeCycleAlert();
        generatePendingPaymentAlert();

        ExLogger.get().info("Generated alerts!");

        // system activity can be checked to stop sending multiple reminder [filter by object, verb, extraContext]

        //generateNotLoggedInAlert(); //2 weeks, 4 weeks, 12 weeks
        //generateCreateDealAlert(); //1 month
        //generateIncompleteProfileAlert(); //1 week
        //generatePendingNegotiationInvitationAlert(); // 1day, 3days, 7days
        //generatePendingMembershipInvitationAlert(); // 1days, 3days, 7days
        //generateExpiringMembershipAlert(); // 2weeks, 7 day, 1day
        // review + authorisation pending
        // review + authorisation deadline approaching
        // offer received... evaluation pending decision
    }

    public void generateNotLoggedInAlert() {
        QueryFilters filters = QueryFilters.create(MemberProfileQFilters.NOT_LOGGEDIN_AFTER, DateUtil.addWithCurrentDate(Calendar.MONTH, -1, true));
        List<Membership> memberships = membershipManager.find(filters);

        for (Membership membership : memberships) {
            if (membership.isActive() && membership.equals(membership.getUser().getCurrentMembership())) {
                Map<String, String> extraContext = new HashMap<String, String>();
                extraContext.put(REMINDER, NotificationType.PROFILE_NOT_LOGGEDIN_REMINDER.toString());
                //ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, (Negotiator)membership, Arrays.asList((Negotiator)membership), extraContext);
                // Email only
            }
        }
    }

    public void generateCreateDealAlert() {

        QueryFilters filters = QueryFilters.create(MemberProfileQFilters.NOT_PUBLISHED_NEGOTIATION, true);
        filters.put(MemberProfileQFilters.PUBLISHED_NEGOTIATION_AFTER, DateUtil.addWithCurrentDate(Calendar.MONTH, -1, true));
        List<Membership> memberships = membershipManager.find(filters);

        for (Membership membership : memberships) {
            if (membership.isActive()) {
                Map<String, String> extraContext = new HashMap<String, String>();
                extraContext.put(REMINDER, NotificationType.NEGOTIATION_TO_CREATE_REMINDER.toString());
                ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, (Negotiator) membership, Arrays.asList((Negotiator) membership), extraContext);
            }
        }
    }

    public void generateIncompleteProfileAlert() {
        QueryFilters filters = QueryFilters.create(ProfileQFilters.INCOMPLETE, true);
        List<Profile> profiles = profileManager.getProfiles(filters);

        for (Profile profile : profiles) {
            Membership membership = membershipManager.getOwnerMembership(profile.getUuid());
            if (membership.isActive()) {
                Map<String, String> extraContext = new HashMap<String, String>();
                extraContext.put(REMINDER, NotificationType.PROFILE_NOT_COMPLETED_REMINDER.toString());
                ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, (Negotiator) membership, Arrays.asList((Negotiator) membership), extraContext);
            }
        }
    }

    public void generatePendingSignatureAlert() {
        // every 3days for one month??
        // signature date of last one or agreement date
        // agreed within 30 days || last person signed in 30 days
        // should filter multiple signature container [if signer change then there could be multiple signature container]
        ExLogger.get().info("Generating PendingSignatureAlert!");

        QueryFilters filters = QueryFilters.create(SignatureQFilters.SIGNATURE_PENDING, true);
        filters.put(SignatureQFilters.CREATED_AFTER_INCLUSIVE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -30, false));
        filters.put(QueryParameters.SORT, "-@rid");
        filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);

        List<NegotiationSignatureContainer> signatureContainers = signatureManager.find(filters);

        ExLogger.get().info("{} PendingSignature from last 30 days!", signatureContainers.size());

        List<String> processNegotiationIds = new ArrayList<>();

        for (NegotiationSignatureContainer signatureContainer : signatureContainers) {
            ExLogger.get().info("Generating alert for PendingSignature. NegotiationSignatureContainer: {}, Negotiation: {}", signatureContainer.getUuid(), signatureContainer.getNegotiationID());

            if (!processNegotiationIds.contains(signatureContainer.getNegotiationID())) {
                Map<String, String> extraContextQueryParams = new HashMap<String, String>();
                extraContextQueryParams.put(ObjectType.NEGOTIATION.toString(), signatureContainer.getNegotiationID());
                extraContextQueryParams.put(REMINDER, NotificationType.SIGNATURE_PENDING_REMINDER.toString());

                QueryFilters activityFilter = QueryFilters.create("verb", Verb.REMIND);
                activityFilter.put(ActivityQFilters.PUBLISHED_FROM_DATETIME, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -3, false));
                activityFilter.put(ActivityQFilters.EXTRA_CONTEXT, extraContextQueryParams);
                activityFilter.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
                List<Activity> activities = activityManager.listActivities(activityFilter);

                if (ExCollections.isEmpty(activities)) {
                    Membership membership = membershipManager.findByUUID(signatureContainer.getNextToSign().getSignerUUID(), false);
                    if (membership != null && membership.isActive()) {
                        Map<String, String> extraContext = new HashMap<String, String>();
                        extraContext.put(REMINDER, NotificationType.SIGNATURE_PENDING_REMINDER.toString());
                        extraContext.put(ObjectType.NEGOTIATION.toString(), signatureContainer.getNegotiationID());
                        ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, (Negotiator) membership, Arrays.asList((Negotiator) membership), extraContext);
                        notificationManager.process(new SignatureNotificationEvent(NotificationType.SIGNATURE_PENDING_REMINDER, signatureContainer));

                        ExLogger.get().info("Generated alert for PendingSignature. NegotiationSignatureContainer: {}, Negotiation: {}", signatureContainer.getUuid(), signatureContainer.getNegotiationID());
                    }
                }
                processNegotiationIds.add(signatureContainer.getNegotiationID());
            }

        }

        ExLogger.get().info("Generated PendingSignatureAlert!");
    }

    public void generatePendingNegotiationInvitationAlert() {
        ExLogger.get().info("Generating PendingNegotiationInvitationAlert!");

        QueryFilters filters = QueryFilters.create(InvitationFields.INVITATION_STATUS, InvitationStatus.PENDING);
        filters.put(InvitationQFilters.CREATED_AFTER_INCLUSIVE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -30, false));
        filters.put(QueryParameters.SORT, "-@rid");
        filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);

        List<NegotiationInvitation> invitations = negotiationInvitationManager.find(filters);
        ExLogger.get().info("{} PendingNegotiationInvitation from last 30 days!", invitations.size());

        for (NegotiationInvitation invitation : invitations) {
            ExLogger.get().info("Generating alert for PendingNegotiationInvitation. NegotiationInvitation: {}, Negotiation: {}", invitation.getUuid(), invitation.getInvitedNegotiationUUID());
            if (invitation.getInvitedMembership() != null && invitation.getInvitedMembership().isActive()
                    && invitation.getInvitedNegotiation() != null && invitation.getInvitedNegotiation().isAdmissionOpen()) {

                Map<String, String> extraContextQueryParams = new HashMap<String, String>();
                extraContextQueryParams.put(ObjectType.NEGOTIATION.toString(), invitation.getInvitedNegotiationUUID());
                extraContextQueryParams.put(REMINDER, NotificationType.NEGOTIATION_PENDING_INVITATION_REMINDER.toString());

                QueryFilters activityFilter = QueryFilters.create("verb", Verb.REMIND);
                activityFilter.put(ActivityQFilters.PUBLISHED_FROM_DATETIME, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -3, false));
                activityFilter.put(ActivityQFilters.EXTRA_CONTEXT, extraContextQueryParams);
                activityFilter.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
                List<Activity> activities = activityManager.listActivities(activityFilter);

                if (ExCollections.isEmpty(activities)) {
                    Map<String, String> extraContext = new HashMap<String, String>();
                    extraContext.put(REMINDER, NotificationType.NEGOTIATION_PENDING_INVITATION_REMINDER.toString());
                    extraContext.put(ObjectType.NEGOTIATION.toString(), invitation.getInvitedNegotiationUUID());
                    ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, invitation.getInvitedMembership(), Arrays.asList(invitation.getInvitedMembership()), extraContext);
                    notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_PENDING_INVITATION_REMINDER, invitation.getInvitedNegotiation(), invitation));

                    ExLogger.get().info("Generated alert for PendingNegotiationInvitation. NegotiationInvitation: {}, Negotiation: {}", invitation.getUuid(), invitation.getInvitedNegotiationUUID());
                }
            }
        }

        ExLogger.get().info("Generated PendingNegotiationInvitationAlert!");
    }

    public void generatePendingNegotiationJoinRequestAlert() {
        // TODO:
    }

    public void generateContractEndingAlert() {
        // if has renewal, send next renewal alert
        // else send expiration alert
        //
        ExLogger.get().info("Generating ContractEndingAlert!");
        // 2weeks, 1 week, 3days, 1day
        List<Contract> contracts = getContractsExpiringInDays(30);
        contracts.addAll(getContractsExpiringInDays(14));
        contracts.addAll(getContractsExpiringInDays(7));
        contracts.addAll(getContractsExpiringInDays(3));
        contracts.addAll(getContractsExpiringInDays(1));

        Date now = TimeProvider.now();
        for (Contract contract : contracts) {
            ExLogger.get().info("Generating alert for ContractEnding. Contract: {}", contract.getUuid());

            List<Activity> activities = getRemindActivities(1, NotificationType.CONTRACT_EXPIRING_REMINDER, ObjectType.CONTRACT, contract.getUuid());
            activities.addAll(getRemindActivities(1, NotificationType.CONTRACT_NEXT_RENEWAL_REMINDER, ObjectType.CONTRACT, contract.getUuid()));

            boolean hasNextRenewal = contractManager.nextRenewalEvent(contract, now) != null;

            if (ExCollections.isEmpty(activities)) {
                for (IContractMember member : contract.getContractMembers()) {
                    if (member.getMemberObjectType().equals(ContractUserMember.MEMBER_OBJECT_TYPE)) {
                        Membership membership = (Membership) ((ContractUserMember) member).getNegotiator();
                        if (membership.isActive()) {
                            Map<String, String> extraContext = new HashMap<String, String>();
                            extraContext.put(ObjectType.CONTRACT.toString(), contract.getUuid());

                            if (hasNextRenewal)
                                extraContext.put(REMINDER, NotificationType.CONTRACT_NEXT_RENEWAL_REMINDER.toString());
                            else
                                extraContext.put(REMINDER, NotificationType.CONTRACT_EXPIRING_REMINDER.toString());
                            ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, (Negotiator) membership, Arrays.asList((Negotiator) membership), extraContext);
                        }
                    }
                }

                if (hasNextRenewal)
                    notificationManager.process(new ContractNotificationEvent(NotificationType.CONTRACT_NEXT_RENEWAL_REMINDER, contract));
                else
                    notificationManager.process(new ContractNotificationEvent(NotificationType.CONTRACT_EXPIRING_REMINDER, contract));

                ExLogger.get().info("Generated alert for ContractEnding. Contract: {}", contract.getUuid());
            }
        }
        ExLogger.get().info("Generated ContractEndingAlert!");
    }

    public void generateContractTerminationNoticeDeadlineAlert() {
        // if has renewal, send next renewal alert
        // else send expiration alert
        //
        ExLogger.get().info("Generating ContractTerminationNoticeDeadlineAlert!");
        // 2weeks, 1 week, 3days, 1day
        List<Contract> contracts = getContractsTerminationNoticeDeadlineInDays(30);
        contracts.addAll(getContractsTerminationNoticeDeadlineInDays(14));
        contracts.addAll(getContractsTerminationNoticeDeadlineInDays(7));
        contracts.addAll(getContractsTerminationNoticeDeadlineInDays(3));
        contracts.addAll(getContractsTerminationNoticeDeadlineInDays(1));

        for (Contract contract : contracts) {
            ExLogger.get().info("Generating alert for ContractTerminationNoticeDeadline. Contract: {}", contract.getUuid());

            List<Activity> activities = getRemindActivities(1, NotificationType.CONTRACT_TERMINATION_NOTICE_DEADLINE_REMINDER, ObjectType.CONTRACT, contract.getUuid());
            if (ExCollections.isEmpty(activities)) {
                for (IContractMember member : contract.getContractMembers()) {
                    if (member.getMemberObjectType().equals(ContractUserMember.MEMBER_OBJECT_TYPE)) {
                        Membership membership = (Membership) ((ContractUserMember) member).getNegotiator();
                        if (membership.isActive()) {
                            Map<String, String> extraContext = new HashMap<String, String>();
                            extraContext.put(REMINDER, NotificationType.CONTRACT_TERMINATION_NOTICE_DEADLINE_REMINDER.toString());
                            extraContext.put(ObjectType.CONTRACT.toString(), contract.getUuid());
                            ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, (Negotiator) membership, Arrays.asList((Negotiator) membership), extraContext);
                        }
                    }
                }
                notificationManager.process(new ContractNotificationEvent(NotificationType.CONTRACT_TERMINATION_NOTICE_DEADLINE_REMINDER, contract));
                ExLogger.get().info("Generated alert for ContractTerminationNoticeDeadline. Contract: {}", contract.getUuid());
            }
        }
        ExLogger.get().info("Generated ContractEndingAlert!");
    }

    public void generatePendingReviewRequestAlert() {
        ExLogger.get().info("Generating PendingReviewRequestAlert!");

        QueryFilters filters = QueryFilters.create(ReviewRequestFilters.STATUS, ReviewStatus.PENDING);
        filters.put(ReviewRequestFilters.CREATED_AFTER_INCLUSIVE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -30, false));
        filters.put(QueryParameters.SORT, "-@rid");
        filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);

        List<ReviewRequest> reviewRequests = reviewManager.listReviewRequests(filters);
        ExLogger.get().info("{} PendingReviewRequest from last 30 days!", reviewRequests.size());

        for (ReviewRequest reviewRequest : reviewRequests) {
            ExLogger.get().info("Generating alert for PendingReviewRequest. ReviewRequest: {}, Negotiation: {}", reviewRequest.getUuid(), reviewRequest.getNegotiationUUID());

            if (!reviewRequest.getNegotiation().isClosed()) {
                Map<String, String> extraContextParams = new HashMap<String, String>();
                extraContextParams.put(ObjectType.REVIEW_REQUEST.toString(), reviewRequest.getUuid());
                extraContextParams.put(ObjectType.NEGOTIATION.toString(), reviewRequest.getNegotiationUUID());
                extraContextParams.put(REMINDER, NotificationType.REVIEW_PENDING_REMINDER.toString());

                QueryFilters activityFilter = QueryFilters.create("verb", Verb.REMIND);
                activityFilter.put(ActivityQFilters.PUBLISHED_FROM_DATETIME, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -3, false));
                activityFilter.put(ActivityQFilters.EXTRA_CONTEXT, extraContextParams);
                activityFilter.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
                List<Activity> activities = activityManager.listActivities(activityFilter);

                if (ExCollections.isEmpty(activities)) {
                    for (Review review : reviewRequest.getReviews()) {
                        if (review.getStatus() == ReviewStatus.PENDING) {
                            extraContextParams.put(ObjectType.REVIEW.toString(), review.getUuid());
                            ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, review.getReviewer(), Arrays.asList(review.getReviewer()), extraContextParams);
                        }
                    }
                    notificationManager.process(new ReviewNotificationEvent(NotificationType.REVIEW_PENDING_REMINDER, reviewRequest));
                    ExLogger.get().info("Generated alert for PendingReviewRequest. ReviewRequest: {}, Negotiation: {}", reviewRequest.getUuid(), reviewRequest.getNegotiationUUID());
                }
            }
        }

        ExLogger.get().info("Generated PendingReviewRequestAlert!");
    }

    public void generatePendingAuthorisationRequestAlert() {
        ExLogger.get().info("Generating PendingAuthorisationRequestAlert!");

        QueryFilters filters = QueryFilters.create(AuthorisationFields.STATUS, AuthorisationStatus.PENDING);
        filters.put(AuthorisationFilters.CREATED_AFTER_INCLUSIVE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -30, false));
        filters.put(QueryParameters.SORT, "-@rid");
        filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);

        List<AuthorisationRequest> authorisationRequests = authorisationManager.listAuthorisationRequests(filters);

        ExLogger.get().info("{} PendingAuthorisationRequest from last 30 days!", authorisationRequests.size());

        for (AuthorisationRequest authorisationRequest : authorisationRequests) {
            ExLogger.get().info("Generating alert for PendingReviewRequest. AuthorisationRequest: {}, {}: {}", authorisationRequest.getUuid(), authorisationRequest.getObjectType(), authorisationRequest.getObjectID());

            Map<String, String> extraContextParams = new HashMap<String, String>();
            extraContextParams.put(ObjectType.AUTHORISATION_REQUEST.toString(), authorisationRequest.getUuid());
            extraContextParams.put(REMINDER, NotificationType.AUTHORISATION_PENDING_REMINDER.toString());

            QueryFilters activityFilter = QueryFilters.create("verb", Verb.REMIND);
            activityFilter.put(ActivityQFilters.PUBLISHED_FROM_DATETIME, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -3, false));
            activityFilter.put(ActivityQFilters.EXTRA_CONTEXT, extraContextParams);
            activityFilter.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
            List<Activity> activities = activityManager.listActivities(activityFilter);

            if (ExCollections.isEmpty(activities)) {
                for (Negotiator authoriser : authorisationRequest.getAuthorisersWithPendingRequest()) {
                    ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, authoriser, Arrays.asList(authoriser), extraContextParams);
                }
                notificationManager.process(new AuthorisationNotificationEvent(NotificationType.AUTHORISATION_PENDING_REMINDER, authorisationRequest));
                ExLogger.get().info("Generated alert for PendingReviewRequest. AuthorisationRequest: {}, {}: {}", authorisationRequest.getUuid(), authorisationRequest.getObjectType(), authorisationRequest.getObjectID());
            }
        }

        ExLogger.get().info("Generated PendingAuthorisationRequestAlert!");
    }

    public void generatePendingMembershipInvitationAlert() {
        QueryFilters filters = QueryFilters.create(InvitationFields.INVITATION_STATUS, InvitationStatus.PENDING);
        List<MemberInvitation> invitations = memberInvitationManager.find(filters);

        for (MemberInvitation invitation : invitations) {
            if (invitation.getInvitedProfile().isActive() && invitation.getInvitedUser() != null && invitation.getInvitedUser().getCurrentMembership().isActive()) {
                Map<String, String> extraContext = new HashMap<String, String>();
                extraContext.put(REMINDER, NotificationType.PROFILE_PENDING_MEMBERSHIP_INVITATION_REMINDER.toString());
                ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, (Negotiator) invitation.getInvitedUser().getCurrentMembership(), Arrays.asList((Negotiator) invitation.getInvitedUser().getCurrentMembership()), extraContext);
            }
        }
    }

    public void generateExpiringMembershipAlert() {
        int expirationInDay = 7;
        QueryFilters filters = QueryFilters.create(MembershipFields.EXPIRATION_DATE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, expirationInDay, false));
        List<Membership> memberships = membershipManager.find(filters);

        for (Membership membership : memberships) {
            if (membership.isActive()) {
                Map<String, String> extraContext = new HashMap<String, String>();
                extraContext.put(REMINDER, NotificationType.PROFILE_MEMBERSHIP_EXPIRING_REMINDER.toString());
                ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, (Negotiator) membership, Arrays.asList((Negotiator) membership), extraContext);
            }
        }
    }

    public void generateQuotaFinishingAlert() {
        // TODO:
    }

    public void generateExpiringTrialPeriodAlert() {
        // send who trial is expiring in 7days, 3days and 1day
        // trial expiring in 7 days
        // get memberships with role profile.owner who has an empty profile.planSubscription.plan.externalSubscriptionID and expiration date is x
        // check for not free also
        ExLogger.get().info("Generating ExpiringTrialPeriodAlert!");

        List<Membership> memberships = getOwnerMembershipForEndingTrials(7);
        memberships.addAll(getOwnerMembershipForEndingTrials(3));
        memberships.addAll(getOwnerMembershipForEndingTrials(1));

        for (Membership membership : memberships) {
            ExLogger.get().info("Generating alert for ExpiringTrialPeriod. OwnerMembership: {}, Profile: {}", membership.getUuid(), membership.getProfileUUID());

            if (membership.isActive() && membership.getPlan() != null && !membership.getPlan().isFree()) {
                ExLogger.get().info(membership.getEmail() + " | " + membership.getFullName() + " | " + membership.getPlan().getTitle() + " | " + membership.getProfile().getPlanSubscription().getCreationDate());
                Map<String, String> extraContext = new HashMap<String, String>();
                extraContext.put(REMINDER, NotificationType.PROFILE_TRIAL_FINISHING_REMINDER.toString());
                ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, (Negotiator) membership, Arrays.asList((Negotiator) membership), extraContext);
                notificationManager.process(new ProfileNotificationEvent(NotificationType.PROFILE_TRIAL_FINISHING_REMINDER, membership.getProfile()));

                ExLogger.get().info("Generated alert for ExpiringTrialPeriod. OwnerMembership: {}, Profile: {}", membership.getUuid(), membership.getProfileUUID());
            }
        }
        ExLogger.get().info("Generated ExpiringTrialPeriodAlert!");
    }

    private List<Activity> getRemindActivities(int publishedWithinDays, NotificationType notificationType, ObjectType objectType, String objectUuid) {
        Map<String, String> extraContextQueryParams = new HashMap<String, String>();
        extraContextQueryParams.put(objectType.toString(), objectUuid);
        extraContextQueryParams.put(REMINDER, notificationType.toString());

        QueryFilters activityFilter = QueryFilters.create("verb", Verb.REMIND);
        activityFilter.put(ActivityQFilters.PUBLISHED_FROM_DATETIME, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -publishedWithinDays, false));
        activityFilter.put(ActivityQFilters.EXTRA_CONTEXT, extraContextQueryParams);
        activityFilter.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
        List<Activity> activities = activityManager.listActivities(activityFilter);

        return activities;
    }

    private List<Contract> getContractsExpiringInDays(int expirationInDay) {
        ExLogger.get().info("Loading contracts expiring in {} days!", expirationInDay);
        // expiryDate == currentDate + expirationInDay; 29/11/2020 == 22/11/2020 + 7
        QueryFilters filters = QueryFilters.create(ContractFields.EXPIRY_DATE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, expirationInDay, false));
        filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
        List<Contract> contracts = contractManager.listContracts(filters);

        ExLogger.get().info("{} contracts expiring in {} days!", contracts.size(), expirationInDay);

        return contracts;
    }

    private List<Contract> getContractsTerminationNoticeDeadlineInDays(int terminationNoticeDeadlineInDay) {
        ExLogger.get().info("Loading contracts termination notice deadline in {} days!", terminationNoticeDeadlineInDay);
        //expiryDate - terminationNoticeInDay == currentDate + terminationNoticeDeadlineInDay; 29/11/2020 - 15 == 07/11/2020 + 7
        QueryFilters filters = QueryFilters.create(ContractFilters.TERMINATION_NOTICE_DATE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, terminationNoticeDeadlineInDay, false));
        filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
        List<Contract> contracts = contractManager.listContracts(filters);

        ExLogger.get().info("{} contracts termination notice deadline in {} days!", contracts.size(), terminationNoticeDeadlineInDay);

        return contracts;
    }

    private List<Membership> getOwnerMembershipForEndingTrials(int expirationInDay) {
        ExLogger.get().info("Loading owner membership whose trial expiring in {} days!", expirationInDay);
        QueryFilters filters = QueryFilters.create(MemberProfileQFilters.PROFILE_TRIAL_EXPIRATION_DATE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, expirationInDay, false));
        filters.put(MembershipFilters.ROLENAME, MemberRole.OWNER);
        filters.put(MemberProfileQFilters.PROFILE_PAYMENT_NOT_SET, true);
        filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
        List<Membership> memberships = membershipManager.find(filters);

        ExLogger.get().info("{} trial expiring in {} days!", memberships.size(), expirationInDay);

        return memberships;
    }

    public void generateUpcomingContractLifeCycleAlert() {
        ExLogger.get().info("Generating UpcomingContractLifeCycleAlert!");

        HashMap<String, String> filters = new HashMap<>();
        filters.put(UpcomingLifecycleEventFields.DATE, DateUtil
                .addDayWithDate(new Date(), 0, false).toString());
        List<UpcomingLifecycleEvent> events = upcomingLifecycleEventManager
                .list(filters);

        ExLogger.get().info("{} UpcomingContractLifeCycle from today!",
                events.size());

        Object object = null;
        NotificationType type = null;
        for (UpcomingLifecycleEvent event : events) {
            if (event.getObjectType().equals(ObjectType.CONTRACT)) {
                //as there is only one events now as Contract
                //so just need to working with contract
                Contract contract = contractManager
                        .getContractByUUID(event.getEventUUID());
                if (contract == null)
                    break;
                type = NotificationType.UPCOMING_LIFE_CYCLE_EVENTS;
                ExLogger.get().info("Generating UpcomingContractLifeCycle" +
                                " alert for contract. Contract Title: {}",
                        contract.getTitle());
                object = contract;

            }

            UpcomingLifecycleEventNotificationDTO dto =
                    new UpcomingLifecycleEventNotificationDTO(
                            event.getObjectType(), event.getEventType(),
                            object,
                            event.getMembers()
                    );
            UpcomingLifeCycleEventNotificationEvent e =
                    new UpcomingLifeCycleEventNotificationEvent(type, dto);
            //send template
            notificationManager.process(e);
        }

        ExLogger.get().info("Generated UpcomingContractLifeCycleAlert!");
    }

    public void generatePendingPaymentAlert() {
        // every 3days for one month??
        // signature date of last one or agreement date
        // agreed within 30 days || last person signed in 30 days
        // should filter multiple signature container [if signer change then there could be multiple signature container]
        ExLogger.get().info("Generating PendingPaymentAlert!");

        QueryFilters filters = QueryFilters.create(NegotiationFilters.PAYMENT_STATUS, PaymentStatus.PENDING);
        filters.put(NegotiationFilters.CREATED_AFTER_INCLUSIVE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -30, false));
        filters.put(QueryParameters.SORT, "-@rid");
        filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);

        List<Negotiation> pendingPayment = negotiationPersistenceManager.listPaymentPendingNegotiations(filters);

        ExLogger.get().info("{} PendingPayment from last 30 days!", pendingPayment.size());

        for (Negotiation negotiation : pendingPayment) {
            ExLogger.get().info("Generating alert for PendingPayment: {}, Negotiation: {}", negotiation.getPayment().getUuid(), negotiation.getUuid());

            //getting activity data for that pending negotiation
            Map<String, String> extraContextParams = new HashMap<>();
            extraContextParams.put(ObjectType.PAYMENT.toString(), negotiation.getPayment().getUuid());
            extraContextParams.put(ObjectType.NEGOTIATION.toString(), negotiation.getUuid());
            extraContextParams.put(REMINDER, NotificationType.PAYMENT_PENDING.toString());
            QueryFilters activityFilter = QueryFilters.create("verb", Verb.REMIND);
            activityFilter.put(ActivityQFilters.PUBLISHED_FROM_DATETIME, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -3, false));
            activityFilter.put(ActivityQFilters.EXTRA_CONTEXT, extraContextParams);
            activityFilter.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
            List<Activity> activities = activityManager.listActivities(activityFilter);

            if (ExCollections.isEmpty(activities)) {
                PaymentManager.createPaymentLogAndNotification(negotiation, NotificationType.PAYMENT_PENDING, extraContextParams);
                ExLogger.get().info("Generated alert for PendingPayment: {}, Negotiation: {}", negotiation.getPayment().getUuid(), negotiation.getUuid());
            }
        }

        ExLogger.get().info("Generated PendingPaymentAlert!");
    }
}

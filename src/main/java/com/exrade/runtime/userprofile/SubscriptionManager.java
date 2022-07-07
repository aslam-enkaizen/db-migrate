package com.exrade.runtime.userprofile;

import com.exrade.core.ExLogger;
import com.exrade.models.userprofile.*;
import com.exrade.models.userprofile.security.SubscriptionStatus;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.payment.StripeManager;
import com.exrade.runtime.rest.RestParameters.PlanSubscriptionFields;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.userprofile.persistence.query.SubscriptionQuery;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;
import org.slf4j.Logger;

import java.util.List;

public class SubscriptionManager {

	private static final Logger LOGGER = ExLogger.get();

	private PersistentManager persistenceManager = new PersistentManager();

	public PlanSubscription createSubscription(String profileUUID, String planNameOrUUID, BillingAddress billingAddress, String paymentToken, String promotionCode) {
		Plan plan = PlanManager.getInstance().findByNameOrUUID(planNameOrUUID);

		IProfileManager profileManager = new ProfileManager();
		Profile profile = profileManager.findByUUID(profileUUID);

		return createSubscription(profile, plan, billingAddress, paymentToken, promotionCode);
	}

	public PlanSubscription createSubscription(Profile profile, Plan plan, BillingAddress billingAddress, String paymentToken, String promotionCode) {
		BillingAddress subscriptionBillingAddress = null;
		if(billingAddress == null) {
			subscriptionBillingAddress = buildFromProfile(profile);
		}
		else {
			subscriptionBillingAddress = billingAddress;
		}

		PlanSubscription planSubscription = PlanSubscription.create(plan, profile, subscriptionBillingAddress);

		if(!Strings.isNullOrEmpty(paymentToken))
			StripeManager.getInstance().createSubscription(planSubscription, paymentToken, promotionCode);

		planSubscription = persistenceManager.create(planSubscription);

		return planSubscription;
	}

	public PlanSubscription createDefaultFreeSubscription(Profile profile) {
		Plan plan = PlanManager.getInstance().findByName(ExConfiguration.getStringProperty("DEFAULT_FREE_PLAN"));

		PlanSubscription planSubscription = createSubscription(profile, plan, null, null, null);

		return planSubscription;
	}

	public PlanSubscription updateSubscription(String subscriptionUUID, String planNameOrUUID) {
		PlanSubscription subscription = getSubscriptionByUUID(subscriptionUUID);
		LOGGER.info("Updating subscription [{}]: Plan - [{}], ExternalClientID[{}], ExternalSubscriptionID[{}], Status[{}]",
				subscriptionUUID, subscription.getPlanUUID(), subscription.getExternalClientID(), subscription.getExternalSubscriptionID(), subscription.getStatus());

		Plan plan = PlanManager.getInstance().findByNameOrUUID(planNameOrUUID);
		if(plan != null && !subscription.getPlanUUID().equals(plan.getUuid())) {
			if(!Strings.isNullOrEmpty(subscription.getExternalClientID()) && !Strings.isNullOrEmpty(subscription.getExternalSubscriptionID())) {
				try {
					StripeManager.getInstance().unsubscribePlan(subscription);
				} catch (Exception e) {
					LOGGER.error("Failed to cancel subscription in Stripe", e);
					throw new ExException("Failed to cancel subscription in Stripe");
				}
			}

			subscription.setPlan(plan);
			subscription = persistenceManager.update(subscription);
		}


		LOGGER.info("Updating subscription [{}]: Plan - [{}], ExternalClientID[{}], ExternalSubscriptionID[{}], Status[{}]",
				subscriptionUUID, subscription.getPlanUUID(), subscription.getExternalClientID(), subscription.getExternalSubscriptionID(), subscription.getStatus());
		return subscription;
	}

	public PlanSubscription updateSubscription(String subscriptionUUID, String planNameOrUUID, String paymentToken, String promotionCode, SubscriptionStatus status, BillingAddress billingAddress) {
		PlanSubscription subscription = getSubscriptionByUUID(subscriptionUUID);
		LOGGER.info("Updating subscription [{}]: Plan - [{}], ExternalClientID[{}], ExternalSubscriptionID[{}], Status[{}]",
				subscriptionUUID, subscription.getPlanUUID(), subscription.getExternalClientID(), subscription.getExternalSubscriptionID(), subscription.getStatus());

		Plan plan = PlanManager.getInstance().findByNameOrUUID(planNameOrUUID);
		if(plan != null && !subscription.getPlanUUID().equals(plan.getUuid())) { // changing plan
			BillingAddress subscriptionBillingAddress = null;
			if(billingAddress == null) {
				subscriptionBillingAddress = buildFromProfile(subscription.getProfile());
			}
			else {
				subscriptionBillingAddress = billingAddress;
			}

			if(subscription.getPlan().isFree() && !plan.isFree()) { // free to paid
				subscription.setPlan(plan);
				subscription.setBillingAddress(subscriptionBillingAddress);
				if(!Strings.isNullOrEmpty(subscription.getExternalClientID()) && !Strings.isNullOrEmpty(subscription.getExternalSubscriptionID()))
					StripeManager.getInstance().updateSubscription(subscription, paymentToken, promotionCode);
				else
					StripeManager.getInstance().createSubscription(subscription, paymentToken, promotionCode);
			}
			else if(!subscription.getPlan().isFree() && !plan.isFree()) { // paid to paid
				subscription.setPlan(plan);
				subscription.setBillingAddress(subscriptionBillingAddress);
				if(!Strings.isNullOrEmpty(subscription.getExternalClientID()) && !Strings.isNullOrEmpty(subscription.getExternalSubscriptionID()))
					StripeManager.getInstance().updateSubscription(subscription, paymentToken, promotionCode);
				else
					StripeManager.getInstance().createSubscription(subscription, paymentToken, promotionCode);
			}
			else if(!subscription.getPlan().isFree() && plan.isFree()) { // paid to free
				cancelSubscription(subscriptionUUID);
				subscription.setPlan(plan);
				subscription.setBillingAddress(subscriptionBillingAddress);
			}
			else { // free to free
				subscription.setPlan(plan);
				subscription.setBillingAddress(subscriptionBillingAddress);
			}
		}
		else if(!Strings.isNullOrEmpty(paymentToken) || billingAddress != null) { // update payment method and/or billing address
			BillingAddress subscriptionBillingAddress = null;
			if(billingAddress == null) {
				subscriptionBillingAddress = buildFromProfile(subscription.getProfile());
			}
			else {
				subscriptionBillingAddress = billingAddress;
			}
			subscription.setBillingAddress(subscriptionBillingAddress);
			if(!Strings.isNullOrEmpty(subscription.getExternalClientID()) && !Strings.isNullOrEmpty(subscription.getExternalSubscriptionID()))
				StripeManager.getInstance().updateSubscription(subscription, paymentToken, promotionCode);
			else
				StripeManager.getInstance().createSubscription(subscription, paymentToken, promotionCode);
		}
		else if(status != null) { // update only status TODO: allow status update only by superadmin/webhook/scheduler
			if(!Strings.isNullOrEmpty(subscription.getExternalSubscriptionID())
					&& subscription.getStatus() == SubscriptionStatus.INCOMPLETE
					&& status == SubscriptionStatus.ACTIVE) {
				StripeManager.getInstance().checkAndUpdateSubscriptionStatus(subscription);
			}
			else {
				subscription.setStatus(status);
			}
		}

		subscription = persistenceManager.update(subscription);

		LOGGER.info("Updating subscription [{}]: Plan - [{}], ExternalClientID[{}], ExternalSubscriptionID[{}], Status[{}]",
				subscriptionUUID, subscription.getPlanUUID(), subscription.getExternalClientID(), subscription.getExternalSubscriptionID(), subscription.getStatus());
		return subscription;
	}

	public PlanSubscription updateSubscriptionStatus(String externalClientID, String externalSubscriptionID, SubscriptionStatus status) {
		QueryFilters filters = QueryFilters.create(PlanSubscriptionFields.EXTERNAL_CLIENTID, externalClientID);
		filters.put(PlanSubscriptionFields.EXTERNAL_SUBSCRIPTIONID, externalSubscriptionID);

		List<PlanSubscription> subscriptions = listSubscriptions(filters);

		if(ExCollections.isEmpty(subscriptions))
			throw new ExNotFoundException("externalClientID: " + externalClientID + ", externalSubscriptionID: " + externalSubscriptionID );

		if(subscriptions.size() > 1)
			throw new ExException("Multiple subscription found! " + "externalClientID: " + externalClientID + ", externalSubscriptionID: " + externalSubscriptionID);

		PlanSubscription subscription = subscriptions.get(0);
		subscription.setStatus(status);
		return persistenceManager.update(subscription);
	}

	public PlanSubscription updateSubscriptionStatus(String subscriptionUUID, SubscriptionStatus status) {
		PlanSubscription subscription = getSubscriptionByUUID(subscriptionUUID);
		subscription.setStatus(status);
		return persistenceManager.update(subscription);
	}

	public PlanSubscription getSubscriptionByUUID(String uuid) {
		return persistenceManager.readObjectByUUID(PlanSubscription.class, uuid);
	}

	public PlanSubscription cancelSubscription(String uuid) {
		PlanSubscription subscription = getSubscriptionByUUID(uuid);

		if(!Strings.isNullOrEmpty(subscription.getExternalClientID()) && !Strings.isNullOrEmpty(subscription.getExternalSubscriptionID())) {
			try {
				StripeManager.getInstance().unsubscribePlan(subscription);
			} catch (Exception e) {
				LOGGER.error("Failed to cancel subscription in Stripe", e);
				throw new ExException("Failed to cancel subscription in Stripe");
			}
		}
		subscription.setCancelDate(TimeProvider.now());
		subscription.setStatus(SubscriptionStatus.CANCELLED);
		subscription = persistenceManager.update(subscription);

		LOGGER.info("Subscription cancelled. subscription: {}, profile: {}", subscription.getUuid(), subscription.getProfile().getUuid());

		return subscription;
	}

	public List<PlanSubscription> listSubscriptions(QueryFilters iFilters){
		return persistenceManager.listObjects(new SubscriptionQuery(), iFilters);
	}

	public List<Invoice> listInvoices(String uuid){
		PlanSubscription subscription = getSubscriptionByUUID(uuid);
		return StripeManager.getInstance().getInvoices(subscription);
	}

	private BillingAddress buildFromProfile(Profile profile) {
		BillingAddress subscriptionBillingAddress = new BillingAddress();
		subscriptionBillingAddress.setCity(profile.getCity());
		subscriptionBillingAddress.setCountry(profile.getCountry());
		subscriptionBillingAddress.setLine1(profile.getAddress());
		subscriptionBillingAddress.setName(profile.getName());
		subscriptionBillingAddress.setPostcode(profile.getPostcode());

		return subscriptionBillingAddress;
	}
}

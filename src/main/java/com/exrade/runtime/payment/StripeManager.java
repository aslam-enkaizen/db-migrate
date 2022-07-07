package com.exrade.runtime.payment;

import com.exrade.core.ExLogger;
import com.exrade.models.userprofile.*;
import com.exrade.models.userprofile.security.SubscriptionStatus;
import com.exrade.platform.exception.ExException;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.conf.ExConfiguration.configKeys;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ObjectsUtil;
import com.google.common.base.Strings;
import com.stripe.Stripe;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.*;

public class StripeManager {
	private static final Logger logger = ExLogger.get();

	public final static boolean FAKE_PAYMENT = ExConfiguration.getPropertyAsBoolean(configKeys.PAYMENT_DUMMY);

	static {
		final String PAYMENT_KEY = ExConfiguration.getStringProperty(configKeys.PAYMENT_KEY);
		Stripe.apiKey = PAYMENT_KEY;
	}

	private static final StripeManager INSTANCE = new StripeManager();

	private StripeManager() {
	}

	public static StripeManager getInstance() {
		return INSTANCE;
	}

	public String createPlan(Plan iPlan) throws Exception {
		int amount = iPlan.getAmount().multiply(new BigDecimal(100)).intValue();

		Map<String, Object> productParams = new HashMap<String, Object>();
		productParams.put("name", iPlan.getName());
		productParams.put("type", "service");
		productParams.put("statement_descriptor", iPlan.getName());

		Map<String, Object> planParams = new HashMap<String, Object>();
		planParams.put("amount", amount);
		planParams.put("interval", iPlan.getPaymentIntervalUnit().getValue().toLowerCase());
		planParams.put("interval_count", iPlan.getPaymentInterval());
		planParams.put("product", productParams);
		planParams.put("currency", "eur");
		planParams.put("id", iPlan.getName());
		planParams.put("trial_period_days", iPlan.getTrialPeriodDays());

		com.stripe.model.Plan.create(planParams);

		return iPlan.getName();
	}

	public void createSubscription(PlanSubscription planSubscription, String paymentToken, String promotionCode) {
		Subscription subscription = null;
		try {
			if (FAKE_PAYMENT) {
				subscription = generateFakeSubscription(planSubscription.getBillingAddress().getEmail());
			} else {
				Customer client = createCustomer(paymentToken, planSubscription.getBillingAddress());

				Map<String, Object> params = new HashMap<String, Object>();
				params.put("plan", planSubscription.getPlan().getOfferID());
				params.put("customer", client.getId());
				params.put("payment_behavior", "allow_incomplete");
				params.put("expand", Arrays.asList("latest_invoice.payment_intent", "pending_setup_intent"));

				if (planSubscription.getTrialEndDate() != null
						&& planSubscription.getTrialEndDate().after(TimeProvider.now())) {
					params.put("trial_end", planSubscription.getTrialEndDate().toInstant().getEpochSecond());
				}

				if (!Strings.isNullOrEmpty(promotionCode)) {
					params.put("promotion_code", getPromotionCodeId(promotionCode));
				}

				subscription = Subscription.create(params);
			}
			planSubscription.setExternalClientID(subscription.getCustomer());
			planSubscription.setExternalSubscriptionID(subscription.getId());

			checkIfRequiresActionAndUpdateStatus(planSubscription, subscription);

		} catch (Exception e) {
			logger.error("Stripe createSubscription error - " + e.getMessage(), e);
			throw new ExException("Stripe error", e);
		}
	}

	public void updateSubscription(PlanSubscription iPlanSubscription, String paymentToken, String promotionCode) {

		try {
			updateCustomer(iPlanSubscription.getExternalClientID(), paymentToken,
					iPlanSubscription.getBillingAddress());

			Subscription subscription = Subscription.retrieve(iPlanSubscription.getExternalSubscriptionID());

			Map<String, Object> params = new HashMap<>();
			String stripePlanId = subscription.getItems().getData().get(0).getPlan().getId();
			if (!iPlanSubscription.getPlan().getOfferID().equals(stripePlanId)) {
				Map<String, Object> item = new HashMap<>();
				item.put("id", subscription.getItems().getData().get(0).getId());
				item.put("plan", iPlanSubscription.getPlan().getOfferID());

				List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
				items.add(item);

				params.put("items", items);
			}

			if (!Strings.isNullOrEmpty(promotionCode)) {
				params.put("promotion_code", getPromotionCodeId(promotionCode));
			}

			if (!params.isEmpty()) {
				if (iPlanSubscription.getTrialEndDate() != null
						&& iPlanSubscription.getTrialEndDate().after(TimeProvider.now())) {
					params.put("trial_end", iPlanSubscription.getTrialEndDate().toInstant().getEpochSecond());
				}
				params.put("payment_behavior", "allow_incomplete");
				params.put("cancel_at_period_end", false);
				params.put("expand", Arrays.asList("latest_invoice.payment_intent", "pending_setup_intent"));

				subscription = subscription.update(params);
				
				checkIfRequiresActionAndUpdateStatus(iPlanSubscription, subscription);
			}
		} catch (Exception e) {
			logger.error("Stripe updateSubscription error - " + e.getMessage(), e);
			throw new ExException("Stripe error", e);
		}
	}

	public void checkAndUpdateSubscriptionStatus(PlanSubscription iPlanSubscription) {
		try {
			Subscription subscription = Subscription.retrieve(iPlanSubscription.getExternalSubscriptionID());
			checkIfRequiresActionAndUpdateStatus(iPlanSubscription, subscription);
		} catch (Exception e) {
			logger.error("Stripe checkAndUpdateSubscriptionStatus error - " + e.getMessage(), e);
			throw new ExException("Stripe error", e);
		}
	}

	public List<Invoice> getInvoices(PlanSubscription iPlanSubscription) {
		List<Invoice> invoices = new ArrayList<Invoice>();

		try {
			if (iPlanSubscription != null && !Strings.isNullOrEmpty(iPlanSubscription.getExternalClientID())
					&& !Strings.isNullOrEmpty(iPlanSubscription.getExternalSubscriptionID())) {
				Map<String, Object> params = new HashMap<>();
				params.put("limit", 12);
				params.put("customer", iPlanSubscription.getExternalClientID());
				params.put("subscription", iPlanSubscription.getExternalSubscriptionID());

				List<com.stripe.model.Invoice> stripeInvoices = com.stripe.model.Invoice.list(params).getData();

				for (com.stripe.model.Invoice stripeInvoice : stripeInvoices) {
					Invoice invoice = new Invoice();
					invoice.setCurrency(stripeInvoice.getCurrency());
					invoice.setTotalAmount(stripeInvoice.getTotal());
					invoice.setInvoicePdfUrl(stripeInvoice.getInvoicePdf());
					invoice.setIssueDate(stripeInvoice.getCreated());
					invoice.setNumber(stripeInvoice.getNumber());

					try {
						invoice.setPeriodEnd(stripeInvoice.getLines().getData().get(0).getPeriod().getEnd());
					} catch (Exception ex) {
					}

					try {
						invoice.setPeriodStart(stripeInvoice.getLines().getData().get(0).getPeriod().getStart());
					} catch (Exception ex) {
					}

					invoice.setStatus(stripeInvoice.getStatus());

					invoices.add(invoice);
				}
			}
		} catch (Exception e) {
			logger.error("Stripe getInvoices error - " + e.getMessage(), e);
		}

		return invoices;
	}

	private Subscription generateFakeSubscription(String iEmail) {
		Subscription fakeSubscription = new Subscription();
		fakeSubscription.setId(ObjectsUtil.generateUniqueID());
		fakeSubscription.setCreated(TimeProvider.now().getTime());

		Customer fakeClient = new Customer();
		fakeClient.setEmail(iEmail);
		fakeClient.setId(ObjectsUtil.generateUniqueID());
		fakeSubscription.setCustomer(fakeClient.getId());
		return fakeSubscription;
	}

	private String getPromotionCodeId(String promotionCode) throws StripeException {
		Map<String, Object> promotionCodeParams = new HashMap<>();
		promotionCodeParams.put("limit", 1);
		promotionCodeParams.put("code", promotionCode);

		PromotionCodeCollection promotionCodes = PromotionCode.list(promotionCodeParams);
		return promotionCodes.getData().get(0).getId();
	}

	private Customer createCustomer(String paymentToken, BillingAddress address) throws Exception {
		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("source", paymentToken);

		if (!Strings.isNullOrEmpty(address.getEmail()))
			customerParams.put("email", address.getEmail());

		if (!Strings.isNullOrEmpty(address.getName()))
			customerParams.put("name", address.getName());

		if (address != null && !Strings.isNullOrEmpty(address.getLine1())) {
			Map<String, String> customerAddress = new HashMap<>();
			customerAddress.put("line1", address.getLine1());

			if (!Strings.isNullOrEmpty(address.getLine2()))
				customerAddress.put("line2", address.getLine2());

			if (!Strings.isNullOrEmpty(address.getCountry()))
				customerAddress.put("country", address.getCountry());

			if (!Strings.isNullOrEmpty(address.getPostcode()))
				customerAddress.put("postal_code", address.getPostcode());

			if (!Strings.isNullOrEmpty(address.getCity()))
				customerAddress.put("city", address.getCity());

			if (!Strings.isNullOrEmpty(address.getState()))
				customerAddress.put("state", address.getState());

			customerParams.put("address", customerAddress);
		}

		return Customer.create(customerParams);
	}

	public void updateCustomer(String customerId, String paymentToken, BillingAddress address) throws Exception {
		if (!Strings.isNullOrEmpty(paymentToken) || address != null) {
			Customer customer = Customer.retrieve(customerId);
			Map<String, Object> customerParams = new HashMap<String, Object>();
			if (!Strings.isNullOrEmpty(paymentToken))
				customerParams.put("source", paymentToken);

			if (address != null) {
				if (!Strings.isNullOrEmpty(address.getEmail()) && !address.getEmail().equals(customer.getEmail()))
					customerParams.put("email", address.getEmail());

				if (!Strings.isNullOrEmpty(address.getName()))
					customerParams.put("name", address.getName());

				if (!Strings.isNullOrEmpty(address.getLine1()) && (customer.getAddress() == null
						|| !address.getLine1().equals(customer.getAddress().getLine1()))) {
					Map<String, String> customerAddress = new HashMap<>();
					customerAddress.put("line1", address.getLine1());

					if (!Strings.isNullOrEmpty(address.getLine2()))
						customerAddress.put("line2", address.getLine2());

					if (!Strings.isNullOrEmpty(address.getCountry()))
						customerAddress.put("country", address.getCountry());

					if (!Strings.isNullOrEmpty(address.getPostcode()))
						customerAddress.put("postal_code", address.getPostcode());

					if (!Strings.isNullOrEmpty(address.getCity()))
						customerAddress.put("city", address.getCity());

					if (!Strings.isNullOrEmpty(address.getState()))
						customerAddress.put("state", address.getState());

					customerParams.put("address", customerAddress);
				}
			}

			if (!customerParams.isEmpty())
				customer.update(customerParams);
		}
	}

	public Plan getPlan(String iOfferID) throws Exception {
		com.stripe.model.Plan stripePlan = com.stripe.model.Plan.retrieve(iOfferID);

		Plan plan = new Plan();
		plan.setName(stripePlan.getProduct());
		plan.setOfferID(stripePlan.getId());
		plan.setAmount(new BigDecimal(stripePlan.getAmount() / 100));
		plan.setPaymentInterval(stripePlan.getIntervalCount());
		plan.setPaymentIntervalUnit(IntervalUnit.create(stripePlan.getInterval()));
		// plan.setTrialPeriodDays(stripePlan.getTrialPeriodDays());

		return plan;
	}

	public void unsubscribePlan(PlanSubscription planSubscription) throws Exception {
		// Excluding free plans
		if (!FAKE_PAYMENT && !planSubscription.getPlan().isFree()) {
			Map<String, Object> params = new HashMap<String, Object>();
			Subscription sub = null;
			try {
				sub = Subscription.retrieve(planSubscription.getExternalSubscriptionID());
				if (sub.getStatus().equals("active"))
					sub.cancel(params);
			} catch (InvalidRequestException ex) {
				logger.error("Stripe unsubscribe error - " + ex.getMessage(), ex);
				if (ex.getStatusCode() != 404)
					throw ex;
			}
		}
	}
	
	private void checkIfRequiresActionAndUpdateStatus(PlanSubscription planSubscription, Subscription subscription) {
		PaymentIntent paymentIntent = subscription.getLatestInvoiceObject().getPaymentIntentObject();
		if (paymentIntent != null) {
			planSubscription.setExternalPaymentConfirmationType("paymentIntent");
			if (paymentIntent.getStatus().equals("requires_action")
					&& paymentIntent.getNextAction().getType().equals("use_stripe_sdk")) {
				planSubscription.setStatus(SubscriptionStatus.INCOMPLETE);
				planSubscription.setExternalPaymentConfirmationToken(paymentIntent.getClientSecret());
			} else if (paymentIntent.getStatus().equals("succeeded")) {
				planSubscription.setStatus(SubscriptionStatus.ACTIVE);
				planSubscription.setExternalPaymentConfirmationToken(null);
			}
		} else {
			SetupIntent setupIntent = subscription.getPendingSetupIntentObject();
			if (setupIntent != null) {
				planSubscription.setExternalPaymentConfirmationType("setupIntent");
				if (setupIntent.getStatus().equals("requires_action")
						&& setupIntent.getNextAction().getType().equals("use_stripe_sdk")) {
					planSubscription.setStatus(SubscriptionStatus.INCOMPLETE);
					planSubscription.setExternalPaymentConfirmationToken(setupIntent.getClientSecret());
				} else if (setupIntent.getStatus().equals("succeeded")) {
					planSubscription.setStatus(SubscriptionStatus.ACTIVE);
					planSubscription.setExternalPaymentConfirmationToken(null);
				}
			}
		}
		
		// TODO : handle other payment intents -
		// https://stripe.com/docs/payments/intents#intent-statuses
	}

}

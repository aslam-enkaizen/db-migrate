package com.exrade.models.userprofile;

import com.exrade.models.Permission;
import com.exrade.platform.persistence.TimeStampable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IPlan extends TimeStampable {

	String getOfferID(); // associated payment provider's Plan/Offer id

	String getName(); // unique name of the plan

	String getTitle(); // user readable title of the plan

	String getDescription(); // description of the plan

	//List<? extends Role> getRoles();

	List<? extends Permission> getPermissions(); // allowed permissions to this plan

	Permission getPermission(String iValue);

	boolean isBusiness(); // is the plan is for business profile

	boolean isFree(); // is it a free plan? if free then payment is not required

	boolean isUponRequest(); // is it allowed only upon request

	boolean isActive(); // subscription not possible if not active

	boolean isDefaultPlan();

	String getCouponCode(); // if not null then subscription will not be allowed if the coupon cod does not match

	Date getExpirationDate(); // subscription to this plan will not be allowed after this date

	Integer getTrialPeriodDays(); // trial period days

	BigDecimal getAmount(); // price of the plan

	String getCurrency(); // currency of the payment

	Long getPaymentInterval(); // interval of the payment

	IntervalUnit getPaymentIntervalUnit(); // unit for the interval of the payment

	Integer getMaxAllowedSubscriptions(); // max number of subscriptions allowed by this plan, when subscriptions reaches to this number none can subscribe to this plan

	Integer getGrade();

	Set<String> getTags();

}

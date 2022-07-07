package com.exrade.util;

import com.exrade.models.userprofile.IntervalUnit;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.PlanSubscriber;
import com.exrade.models.userprofile.PlanSubscription;
import com.exrade.runtime.timer.TimeProvider;

import java.util.Calendar;
import java.util.Date;

public class PlanSubscriptionUtil {

	public static Date getCurrentBillingCycleStartDate(Negotiator iActor) {
		PlanSubscription subscription = ((PlanSubscriber)iActor.getProfile()).getPlanSubscription();
		IntervalUnit paymentIntervalUnit = IntervalUnit.MONTH;

		if(subscription != null) {
			if(subscription.getPlan().getPaymentIntervalUnit() != null)
				paymentIntervalUnit = subscription.getPlan().getPaymentIntervalUnit();

			Calendar fromDateCal = Calendar.getInstance();
			fromDateCal.setTime(subscription.getCreationDate());

			Calendar todayCal = Calendar.getInstance();
			todayCal.setTime(TimeProvider.now());
			Calendar lastRenewDate = null;

			int day = fromDateCal.get(Calendar.DAY_OF_MONTH);

			if(IntervalUnit.MONTH == paymentIntervalUnit) {
				int month = todayCal.get(Calendar.MONTH);
				int year = todayCal.get(Calendar.YEAR);

				lastRenewDate = Calendar.getInstance();
				lastRenewDate.set(year, month, day);

				if (lastRenewDate.after(todayCal)) {
					lastRenewDate.add(Calendar.MONTH, -1);
				}
			}
			else if(IntervalUnit.YEAR == paymentIntervalUnit) {
				int month = fromDateCal.get(Calendar.MONTH);
				int year = todayCal.get(Calendar.YEAR);

				lastRenewDate = Calendar.getInstance();
				lastRenewDate.set(year, month, day);

				if (lastRenewDate.after(todayCal)) {
					lastRenewDate.add(Calendar.YEAR, -1);
				}
			}

			if(lastRenewDate != null)
				return DateUtil.toBeginningOfTheDay(lastRenewDate.getTime());
		}

		return null;
	}

}

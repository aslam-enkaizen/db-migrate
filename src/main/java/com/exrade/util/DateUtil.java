package com.exrade.util;

import com.exrade.models.contract.DurationUnit;
import com.exrade.runtime.timer.TimeProvider;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
	public static Date toBeginningOfTheDay(Date date) {
		if (date == null)
			return null;

		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//Date dateWithoutTime = sdf.parse(sdf.format(new Date()));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	public static Date toEndOfTheDay(Date date) {
		if (date == null)
			return null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		return calendar.getTime();
	}

	public static Long durationInMilliseconds(Integer value, DurationUnit unit) {
		Long valueInMilliseconds = null;

		if(value != null && unit != null) {
			switch(unit){
			case DAY:
				valueInMilliseconds = value.longValue() * 86400000;
				break;
			case WEEK:
				valueInMilliseconds = value.longValue() * 7 * 86400000;
				break;
			case MONTH:
				valueInMilliseconds = value.longValue() * 30 * 86400000;
				break;
			case YEAR:
				valueInMilliseconds = value.longValue() * 365 * 86400000;
				break;
			}
		}

		return valueInMilliseconds;
	}

	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null && date2 == null)
			return true;

		if(date1 != null && date2 != null)
			return DateUtils.isSameDay(date1, date2);

		return false;
	}

	public static Date addWithCurrentDate(int calendarField, int period, boolean withTime){
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

	public static Date addWithCurrentDate(int days){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public static Date addDayWithDate(Date date, int day, boolean withTime){
		Calendar startTimeCal = Calendar.getInstance();
		startTimeCal.setTime(date);
		startTimeCal.add(Calendar.DATE, day);

		if(!withTime){
			startTimeCal.set(Calendar.HOUR_OF_DAY, 0);
			startTimeCal.set(Calendar.MINUTE, 0);
			startTimeCal.set(Calendar.SECOND, 0);
			startTimeCal.set(Calendar.MILLISECOND, 0);
		}

		return startTimeCal.getTime();
	}
	
	public static long daysDiff(Date firstDate, Date secondDate) {
		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
	    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	    
	    return diff;
	}

	public static Date parseDate(String date) throws ParseException {
		DateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm zzz");
		return simpleDateFormat.parse(date);
	}
}

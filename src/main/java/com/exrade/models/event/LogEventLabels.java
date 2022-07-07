package com.exrade.models.event;

/**
 * This is aplaceholder for mulitlingual LogEvent messages
 * @author jasonfinnegan
 *
 */
public class LogEventLabels {
	
	
	public static final class ContentLabels {
		public static final String START="LogEvent.START";
		public static final String	AGREED="LogEvent.AGREED";
		public static final String	NOTAGREED="LogEvent.NOTAGREED";
		public static final String	CANCELLED="LogEvent.CANCELLED";
		public static final String	ADMISSION_REQUEST_RECEIVED="LogEvent.ADMISSION_REQUEST_RECEIVED";
		public static final String	PARTICIPANT_JOINED="LogEvent.PARTICIPANT_JOINED";
		public static final String	MESSAGE_RECEIVED="LogEvent.MESSAGE_RECEIVED";
		public static final String	MESSAGE_SENT="LogEvent.MESSAGE_SENT";
		public static final String	OFFER_SENT="LogEvent.OFFER_SENT";
		
		public static final String CREATED_AGREEMENT = "LogEvent.CREATED_AGREEMENT";
		
		
		//User moved from STATE to STATE via TRANSITION
		public static final String	USER_MOVED_FROM="LogEvent.USER_MOVED_FROM";
		public static final String	TO_STAGE="LogEvent.TO_STAGE";
		public static final String	VIA_TRANSITION="LogEvent.VIA_TRANSITION";
		
		

	}

}

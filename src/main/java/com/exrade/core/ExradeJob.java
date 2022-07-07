package com.exrade.core;

public class ExradeJob {
	
	protected void setupContext(String iNegotiatorUUID){
		//todo update it if required as it's calling from job
//		if (Http.Context.current.get() == null ){
//			initContext();
//		}
//		IMembershipManager membershipManager = new MembershipManager();
//
//		Negotiator negotiator = membershipManager.findByUUID(iNegotiatorUUID, false);
//
//		if(negotiator != null){
//			ContextHelper.put(ContextHelper.MEMBERSHIP, negotiator);
//			Context.current().changeLang(((Membership)negotiator).getLanguage());
//		}
	}
	
	public void initContext() {
//		Request requestMock = mock(Request.class);
//		RequestHeader requestHeaderMock = mock(RequestHeader.class);
//		Http.Cookies cookiesMock = mock(Http.Cookies.class);
//		when(requestMock.cookies()).thenReturn(cookiesMock);
//		Context.current.set(new Context(new Random().nextLong(), requestHeaderMock, requestMock, new HashMap<String,String>(), new HashMap<String,String>(), new HashMap<String,Object>()));
	}
}

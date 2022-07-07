package com.exrade.models.userprofile.security;




public class ExResourcePermission extends ExPermission {

		private long limit;

		public static ExResourcePermission create(String iValue,Integer iLimit) {
			ExResourcePermission permission = new ExResourcePermission();
			permission.value = iValue;
			permission.limit = iLimit;
			return permission;
		}

		public static ExResourcePermission create(String iValue, Long iLimit) {
			ExResourcePermission permission = new ExResourcePermission();
			permission.value = iValue;
			permission.limit = iLimit;
			return permission;
		}

		public Long getLimit() {
			return limit;
		}
		
		public boolean checkLimit(long iValue){
			return getLimit() < 0 || iValue <= getLimit(); 
		}

}

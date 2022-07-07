package com.exrade.models.kyc.qii;

public enum QiiRequestStatus {
	COMPLETED, // all member completed qii
	COMPLETED_MAIN_MEMBER, // only main member completed qii but others are pending
	PENDING // all members pending qii
}

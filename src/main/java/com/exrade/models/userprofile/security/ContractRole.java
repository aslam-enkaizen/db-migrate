package com.exrade.models.userprofile.security;


public class ContractRole extends ExRole {

	public final static String MANAGER = "contract.manager";
	public final static String NEGOTIATOR = "contract.negotiator";
	public final static String APPROVER = "contract.approver";
	public final static String SIGNER = "contract.signer";
	public final static String VIEWER = "contract.viewer";

	public ContractRole(){} 
	
	public ContractRole(String iName) {
		super(iName);
	}
	
}

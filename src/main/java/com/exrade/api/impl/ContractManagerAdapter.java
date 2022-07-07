package com.exrade.api.impl;

import com.exrade.api.ContractAPI;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.contract.IContractManager;

public class ContractManagerAdapter implements ContractAPI {

	private IContractManager manager = new ContractManager();
	
	

}

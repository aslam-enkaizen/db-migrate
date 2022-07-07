package com.exrade.runtime.contract;

import com.exrade.models.contract.Contract;
import com.exrade.models.contract.ContractLifecycleEvent;
import com.exrade.models.contract.IContractMember;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.Date;
import java.util.List;


public interface IContractManager {
	Contract createContract(Contract iContract);

	void createContract(Negotiation negotiation);

	Contract updateContract(Contract iContract);

	void deleteContract(String uuid);

	Contract getContractByUUID(String iContractUUID);

	List<Contract> listContracts(QueryFilters iFilters);

	IContractMember addContractMember(String iContractUUID, String membershipUUID, String roleName);

	IContractMember getContractMember(String iContractUUID, String membershipUUID);

	IContractMember updateContractMember(String iContractUUID, String membershipUUID, String roleName);

	void removeContractMember(String iContractUUID, String membershipUUID);

	List<SearchResultSummary> listSearchResultSummary(QueryFilters iFilters);

	void validateLifecycleSetting(Contract contract);

	ContractLifecycleEvent nextRenewalEvent(Contract contract, Date now);

	void generateLifecycleEvents(Contract contract);

	void updateContractStatusAndCurrentExecutionDuration(Contract contract, Date now);

	void createEndEvent(Contract contract, Date now);

	Contract getParentContractForNegotiationTemplate(String negotiationTemplateUUID);
}
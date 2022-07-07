package com.exrade.models.contract;

import com.exrade.models.informationmodel.IInformationModel;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.NegotiationTemplate;
import com.exrade.models.userprofile.IProfile;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampableSequenceable;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;

import javax.persistence.Embedded;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Contract extends BaseEntityUUIDTimeStampableSequenceable {

	private String title;

	private String description;

	private Date effectiveDate;

	private Date expiryDate;

	private String currencyCode;

	private BigDecimal value;

	private ContractStatus status;

	private Negotiation negotiation;

	private Negotiator creator;

	private IProfile ownerProfile;

	private String governingLaw;

	private List<String> contractFiles = new ArrayList<>();

	private List<String> attachments = new ArrayList<>();

	@OneToMany(orphanRemoval = true)
	private List<ContractingParty> contractingParties = new ArrayList<>();

	private String agreementUUID;

	private String signatureContainerUUID;

	private List<NegotiationTemplate> negotiationTemplates = new ArrayList<NegotiationTemplate>();

	private List<Negotiation> supportingDocumentNegotiations = new ArrayList<Negotiation>();
	
	private List<Negotiation> amendmentContractNegotiations = new ArrayList<Negotiation>();

	private ContractType contractType = ContractType.REGULAR;

	private IInformationModel agreementInformationModel;

	private ContractLifecycleSetting lifecycleSetting;

	@Embedded
	private List<ContractLifecycleEvent> lifecycleEvents = new ArrayList<>();

	private boolean blockchainEnabled;

	private String parentContractUUID; // parent of sub-contract
	
	private String sourceContractUUID; // related/source contract

	public Contract(){}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	@JsonIgnore
	public Negotiation getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(Negotiation negotiation) {
		this.negotiation = negotiation;
	}

	public String getNegotiationUUID() {
		return getNegotiation() != null ? getNegotiation().getUuid() : null;
	}

	public String getCreatedFromNegotiationTemplateUUID() {
		return getNegotiation() != null ? getNegotiation().getNegotiationTemplateUUID() : null;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	@JsonIgnore
	public List<ContractingParty> getContractingParties() {
		return contractingParties;
	}

	public void setContractingParties(List<ContractingParty> contractingParties) {
		this.contractingParties = contractingParties;
	}

	public List<String> getContractFiles() {
		return contractFiles;
	}

	public void setContractFiles(List<String> contractFiles) {
		this.contractFiles = contractFiles;
	}

	public Negotiator getCreator() {
		return creator;
	}

	public void setCreator(Negotiator creator) {
		this.creator = creator;
	}

	@JsonIgnore
	public IProfile getOwnerProfile() {
		return ownerProfile;
	}

	public void setOwnerProfile(IProfile ownerProfile) {
		this.ownerProfile = ownerProfile;
	}

	public String getOwnerProfileUUID() {
		return getOwnerProfile().getUuid();
	}

	public ContractStatus getStatus() {
		return status;
	}

	public void setStatus(ContractStatus status) {
		this.status = status;
	}

	public String getGoverningLaw() {
		return governingLaw;
	}

	public void setGoverningLaw(String governingLaw) {
		this.governingLaw = governingLaw;
	}

	public boolean isRenewable() {
		return getLifecycleSetting() == null ? false : getLifecycleSetting().isRenewable();
	}

	public List<IContractMember> getOwnPartyMembers(){
		ContractingParty party = findMyContractingParty();
		if(party != null)
			return party.getMembers();

		return null;
	}

	public List<IContractMember> getOtherPartyMembers(){
		List<IContractMember> members = new ArrayList<>();
		Negotiator requestor = ContextHelper.getMembership();

		if(requestor != null) {
			for(ContractingParty party : getContractingParties()) {
				if(party.getProfile() == null
						|| (party.getProfile() != null && !requestor.getProfile().equals(party.getProfile())))
					members.addAll(party.getMembers());
			}
		}

		return members;
	}

	@JsonIgnore
	public List<IContractMember> getContractMembers(){
		List<IContractMember> members = new ArrayList<>();
		for(ContractingParty party : getContractingParties()) {
			members.addAll(party.getMembers());
		}

		return members;
	}

	public boolean isOwner() {
		ContractingParty party = findMyContractingParty();
		if(party != null)
			return party.getPartyType() == ContractingPartyType.OWNER;

		return false;
	}

	public String getCategory() {
		ContractingParty party = findMyContractingParty();
		if(party != null)
			return party.getCategory();

		return null;
	}

	public Set<String> getTags() {
		ContractingParty party = findMyContractingParty();
		if(party != null)
			return party.getTags();

		return null;
	}

	public boolean isArchived() {
		ContractingParty party = findMyContractingParty();
		if(party != null)
			return party.isArchived();

		return false;
	}

	public ContractRisk getRisk() {
		ContractingParty party = findMyContractingParty();
		if(party != null)
			return party.getRisk();

		return null;
	}

	public String getReferenceId() {
		ContractingParty party = findMyContractingParty();
		if(party != null)
			return party.getReferenceId();

		return null;
	}

	public String getNote() {
		ContractingParty party = findMyContractingParty();
		if(party != null)
			return party.getNote();

		return null;
	}

	public ContractingParty findMyContractingParty() {
		Negotiator requestor = ContextHelper.getMembership();
		if(requestor == null)
			return null;

		for(ContractingParty party : getContractingParties()) {
			if(party.getProfile() != null && requestor.getProfile().equals(party.getProfile()))
				return party;
		}

		return null;
	}

	public IContractMember findMyContractMember() {
		ContractingParty party = findMyContractingParty();
		if(party != null) {
			Negotiator requestor = ContextHelper.getMembership();
			for(IContractMember member : party.getMembers()) {
				if(member.getUuid().equals(requestor.getIdentifier()))
					return member;
			}
		}
		return null;
	}

	public IContractMember findMyContractMember(String uuid) {
		ContractingParty party = findMyContractingParty();
		if(party != null) {
			for(IContractMember member : party.getMembers()) {
				if(member.getUuid().equals(uuid))
					return member;
			}
		}
		return null;
	}

	public String getAgreementUUID() {
		return agreementUUID;
	}

	public void setAgreementUUID(String agreementUUID) {
		this.agreementUUID = agreementUUID;
	}

	public String getSignatureContainerUUID() {
		return signatureContainerUUID;
	}

	public void setSignatureContainerUUID(String signatureContainerUUID) {
		this.signatureContainerUUID = signatureContainerUUID;
	}

	public ContractType getContractType() {
		return contractType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

	public List<NegotiationTemplate> getNegotiationTemplates() {
		return negotiationTemplates;
	}

	public void setNegotiationTemplates(List<NegotiationTemplate> negotiationTemplates) {
		this.negotiationTemplates = negotiationTemplates;
	}

	@JsonIgnore
	public List<Negotiation> getSupportingDocumentNegotiations() {
		return supportingDocumentNegotiations;
	}

	public void setSupportingDocumentNegotiations(List<Negotiation> subNegotiations) {
		this.supportingDocumentNegotiations = subNegotiations;
	}
	
	@JsonIgnore
	public List<Negotiation> getAmendmentContractNegotiations() {
		return amendmentContractNegotiations;
	}

	public void setAmendmentContractNegotiations(List<Negotiation> amendmentContractNegotiations) {
		this.amendmentContractNegotiations = amendmentContractNegotiations;
	}

	public List<String> getNegotiationTemplateUUIDs() {
		List<String> negotiationTemplateUUIDs = new ArrayList<String>();

		if(ExCollections.isNotEmpty(getNegotiationTemplates())) {
			for(NegotiationTemplate template : getNegotiationTemplates())
				negotiationTemplateUUIDs.add(template.getUuid());
		}

		return negotiationTemplateUUIDs;
	}

	public List<String> getSupportingDocumentNegotiationUUIDs() {
		List<String> supportingDocumentNegotiationUUIDs = new ArrayList<String>();

		if(ExCollections.isNotEmpty(getSupportingDocumentNegotiations())) {
			for(Negotiation supportingDocumentNegotiation : getSupportingDocumentNegotiations())
				supportingDocumentNegotiationUUIDs.add(supportingDocumentNegotiation.getUuid());
		}

		return supportingDocumentNegotiationUUIDs;
	}
	
	public List<String> getAmendmentContractNegotiationUUIDs() {
		List<String> amendmentContractNegotiationUUIDs = new ArrayList<String>();

		if(ExCollections.isNotEmpty(getAmendmentContractNegotiations())) {
			for(Negotiation amendmentContractNegotiation : getAmendmentContractNegotiations())
				amendmentContractNegotiationUUIDs.add(amendmentContractNegotiation.getUuid());
		}

		return amendmentContractNegotiationUUIDs;
	}

	public String getContractNumber() {
		//return String.format("{}", getSequenceNumber());
		if(getId() != null)
			return getId().replace("#", "").replace(":", "-");
		return null;
	}

	public IInformationModel getAgreementInformationModel() {
		return agreementInformationModel;
	}

	public void setAgreementInformationModel(IInformationModel agreementInformationModel) {
		this.agreementInformationModel = agreementInformationModel;
	}

	@Override
	public String getSequenceName() {
		return String.format("{}_Contract", getOwnerProfileUUID());
	}

	public ContractLifecycleSetting getLifecycleSetting() {
		return lifecycleSetting;
	}

	public void setLifecycleSetting(ContractLifecycleSetting lifecycleSetting) {
		this.lifecycleSetting = lifecycleSetting;
	}

	public List<ContractLifecycleEvent> getLifecycleEvents() {
		return lifecycleEvents;
	}

	public void setLifecycleEvents(List<ContractLifecycleEvent> lifecycleEvents) {
		this.lifecycleEvents = lifecycleEvents;
	}

	public boolean isBlockchainEnabled() {
		return this.blockchainEnabled;
	}

	public void setBlockchainEnabled(boolean blockchainEnabled) {
		this.blockchainEnabled = blockchainEnabled;
	}

	public boolean isTerminationNoticeRequired() {
		return getLifecycleSetting() == null ? false : getLifecycleSetting().isTerminationNoticeRequired();
	}

	public String getParentContractUUID() {
		return parentContractUUID;
	}

	public void setParentContractUUID(String parentContractUUID) {
		this.parentContractUUID = parentContractUUID;
	}

	public boolean isImported() {
		if(getNegotiation() == null
				&& (getContractType() == ContractType.MASTER || getContractType() == ContractType.REGULAR))
			return true;

		return false;
	}

	public boolean isChangingContractTypeAllowed() {
		if(getContractType() == ContractType.MASTER || getContractType() == ContractType.REGULAR)
			return true;

		return false;
	}
	
	@JsonRawValue
	public String getAgreementData() {
		if(getAgreementInformationModel() != null)
			return getAgreementInformationModel().getModelData();
		
		return null;
	}

	public String getSourceContractUUID() {
		return sourceContractUUID;
	}

	public void setSourceContractUUID(String sourceContractUUID) {
		this.sourceContractUUID = sourceContractUUID;
	}
}

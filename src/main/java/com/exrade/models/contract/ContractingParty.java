package com.exrade.models.contract;

import com.exrade.models.userprofile.IProfile;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContractingParty extends BaseEntityUUIDTimeStampable  {

	private ContractingPartyType partyType;

	private IProfile profile;

	@OneToMany(orphanRemoval = true)
	private List<IContractMember> members = new ArrayList<>();

	private String category;

	private Set<String> tags = new HashSet<String>();

	private boolean archived = false;

	private ContractRisk risk;

	private String referenceId;

	private String note;

	public ContractingPartyType getPartyType() {
		return partyType;
	}

	public void setPartyType(ContractingPartyType partyType) {
		this.partyType = partyType;
	}

	public List<IContractMember> getMembers() {
		return members;
	}

	public void setMembers(List<IContractMember> members) {
		this.members = members;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public ContractRisk getRisk() {
		return risk;
	}

	public void setRisk(ContractRisk risk) {
		this.risk = risk;
	}

	public IProfile getProfile() {
		return profile;
	}

	public void setProfile(IProfile profile) {
		this.profile = profile;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}

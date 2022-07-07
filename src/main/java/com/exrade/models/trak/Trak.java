package com.exrade.models.trak;

import com.exrade.models.contract.Contract;
import com.exrade.models.contract.IContractMember;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.*;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.models.trak
 * <p>
 * This class represent Contract Tracking. Where creator is the trak owner, he can set assignee
 * and approver by setting check-list. Assignee will works on check-list as Trak Response and
 * submit for approve.
 * Approver will approved/reject the assignee response by adding approver data.
 */
public class Trak extends BaseEntityUUIDTimeStampable {
    private TrakType type;
    private String title;
    private String description;
    private TrakStatus status;
    private Contract contract;
    private Negotiator creator;
    private Negotiator assignee;
    private Negotiator approver;
    private Date dueDate;
    private Date startDate;
    private Integer progress;
    private boolean approvalRequired;
    private List<TrakCheckList> checkList = new ArrayList<>();
    private List<String> files = new ArrayList<>();
    private Double value;
    private boolean proofRequired;
    private Trak parent;
    private String externalId;
    private Boolean blockchainEnabled = false;
    private Boolean isInternal = false;
    private Map<String, Object> customFields = new HashMap<>();

    public Trak() {
    }

    public TrakType getType() {
        return type;
    }

    public void setType(TrakType type) {
        this.type = type;
    }

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

    public TrakStatus getStatus() {
        return status;
    }

    public void setStatus(TrakStatus status) {
        this.status = status;
    }

    public Negotiator getCreator() {
        return creator;
    }

    public void setCreator(Negotiator creator) {
        this.creator = creator;
    }

    public Negotiator getAssignee() {
        return assignee;
    }

    public void setAssignee(Negotiator assignee) {
        this.assignee = assignee;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public List<TrakCheckList> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<TrakCheckList> checkList) {
        this.checkList = checkList;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Negotiator getApprover() {
        return approver;
    }

    public void setApprover(Negotiator approver) {
        this.approver = approver;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public boolean isProofRequired() {
        return proofRequired;
    }

    public void setProofRequired(boolean proofRequired) {
        this.proofRequired = proofRequired;
    }

    public String getCreatorUUID() {
        return getCreator() != null ? getCreator().getIdentifier() : null;
    }

    public String getAssigneeUUID() {
        return getAssignee() != null ? getAssignee().getIdentifier() : null;
    }

    public String getApproverUUID() {
        return getApprover() != null ? getApprover().getIdentifier() : null;
    }

    public String getContractUUID() {
        return getContract() != null ? getContract().getUuid() : null;
    }

    public List<String> getTrakContributorProfileUUIDs() {
        List<String> list = new ArrayList<>();
        list.add(getCreator().getProfile().getUuid());
        if (getAssignee() != null)
            list.add(getAssignee().getProfile().getUuid());
        if (getApprover() != null)
            list.add(getApprover().getProfile().getUuid());
        return list;
    }

    public List<IContractMember> getContractMembers() {
        return getContract().getContractMembers();
    }

    public Trak getParent() {
        return parent;
    }

    public void setParent(Trak parent) {
        this.parent = parent;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Boolean getBlockchainEnabled() {
        return blockchainEnabled;
    }

    public void setBlockchainEnabled(Boolean blockchainEnabled) {
        this.blockchainEnabled = blockchainEnabled;
    }

    public Boolean getInternal() {
        return isInternal;
    }

    public void setInternal(Boolean internal) {
        isInternal = internal;
    }

    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }
}

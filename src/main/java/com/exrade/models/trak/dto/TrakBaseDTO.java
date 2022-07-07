package com.exrade.models.trak.dto;

import com.exrade.models.trak.TrakCheckList;
import com.exrade.models.trak.TrakType;

import java.util.Date;
import java.util.List;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.models.trak
 */
public abstract class TrakBaseDTO {
    private TrakType type;
    private String title;
    private String description;
    private String creatorUUID;
    private String assigneeUUID;
    private String approverUUID;
    private Date startDate;
    private Date dueDate;
    private Integer progress;
    private boolean approvalRequired;
    private List<TrakCheckList> checkList;
    private List<String> files;
    private Double value;
    private boolean proofRequired;
    private String contractUUID;
    private String externalId;

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

    public String getCreatorUUID() {
        return creatorUUID;
    }

    public void setCreatorUUID(String creatorUUID) {
        this.creatorUUID = creatorUUID;
    }

    public String getAssigneeUUID() {
        return assigneeUUID;
    }

    public void setAssigneeUUID(String assigneeUUID) {
        this.assigneeUUID = assigneeUUID;
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

    public String getContractUUID() {
        return contractUUID;
    }

    public void setContractUUID(String contractUUID) {
        this.contractUUID = contractUUID;
    }

    public String getApproverUUID() {
        return approverUUID;
    }

    public void setApproverUUID(String approverUUID) {
        this.approverUUID = approverUUID;
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

	public boolean isProofRequired() {
		return proofRequired;
	}

	public void setProofRequired(boolean isProofRequired) {
		this.proofRequired = isProofRequired;
	}

    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
}

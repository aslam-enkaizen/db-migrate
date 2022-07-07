package com.exrade.models.trak.dto;

import com.exrade.models.trak.TrakStatus;
import com.exrade.models.trak.TrakType;

import java.util.Date;

/**
 * @author Rhidoy
 * @created 19/10/2021
 * @package com.exrade.models.trak.dto
 */
public class TrakFilterDTO {
    private String uuid;
    private String keywords;
    private String creatorUuid;
    private String assigneeUuid;
    private String contractUuid;
    private Date dueDate;
    private Date startDate;
    private TrakStatus status;
    private TrakType type;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCreatorUuid() {
        return creatorUuid;
    }

    public void setCreatorUuid(String creatorUuid) {
        this.creatorUuid = creatorUuid;
    }

    public String getAssigneeUuid() {
        return assigneeUuid;
    }

    public void setAssigneeUuid(String assigneeUuid) {
        this.assigneeUuid = assigneeUuid;
    }

    public String getContractUuid() {
        return contractUuid;
    }

    public void setContractUuid(String contractUuid) {
        this.contractUuid = contractUuid;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public TrakStatus getStatus() {
        return status;
    }

    public void setStatus(TrakStatus status) {
        this.status = status;
    }

    public TrakType getType() {
        return type;
    }

    public void setType(TrakType type) {
        this.type = type;
    }
}

package com.exrade.models.trak.dto;

import com.exrade.models.trak.TrakApproval;

/**
 * @author Rhidoy
 * @created 19/10/2021
 * @package com.exrade.models.trak.dto
 * <p>
 * This mutable class represent the creation/update of Trak Approve from Approver
 */
public class TrakApprovalCreateDTO {
    private final String trakUUID;
    private final String memberUuid;
    private final String responseUUID;
    private final String approvalUUID;
    private final TrakApproval approval;

    public TrakApprovalCreateDTO(String trakUUID, String memberUuid, String approvalUUID, TrakApproval approval) {
        this(trakUUID, memberUuid, approvalUUID, null, approval);
    }

    public TrakApprovalCreateDTO(String trakUUID, String memberUuid, String responseUUID, String approvalUUID, TrakApproval approval) {
        this.trakUUID = trakUUID;
        this.memberUuid = memberUuid;
        this.responseUUID = responseUUID;
        this.approvalUUID = approvalUUID;
        this.approval = approval;
    }

    public String getTrakUUID() {
        return trakUUID;
    }

    public String getMemberUuid() {
        return memberUuid;
    }

    public TrakApproval getApproval() {
        return approval;
    }

    public String getApprovalUUID() {
        return approvalUUID;
    }

    public String getResponseUUID() {
        return responseUUID;
    }
}

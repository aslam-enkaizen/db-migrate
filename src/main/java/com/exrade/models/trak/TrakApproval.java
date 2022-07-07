package com.exrade.models.trak;

import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.List;

/**
 * @author Rhidoy
 * @created 19/10/2021
 * @package com.exrade.models.trak
 */
public class TrakApproval extends BaseEntityUUIDTimeStampable {
    private String note;
    private List<String> files;
    private ApprovalResponseType approvalResponseType;
    private TrakResponse trakResponse;

	public TrakApproval() {}
	
    public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public ApprovalResponseType getApprovalResponseType() {
		return approvalResponseType;
	}

	public void setApprovalResponseType(ApprovalResponseType approvalResponseType) {
		this.approvalResponseType = approvalResponseType;
	}

	public TrakResponse getTrakResponse() {
		return trakResponse;
	}

	public void setTrakResponse(TrakResponse trakResponse) {
		this.trakResponse = trakResponse;
	}
}

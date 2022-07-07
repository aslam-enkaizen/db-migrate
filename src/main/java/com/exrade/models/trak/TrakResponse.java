package com.exrade.models.trak;

import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.Date;
import java.util.List;

/**
 * @author Rhidoy
 * @created 19/10/2021
 * @package com.exrade.models.trak
 */
public class TrakResponse extends BaseEntityUUIDTimeStampable {
    private String note;
    private List<String> files;
    private List<String> proofFiles;
    private Integer progress;
    private List<TrakResponseCheckList> checkList;
    private Date completionDate;
    private Trak trak;

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

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public List<TrakResponseCheckList> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<TrakResponseCheckList> checkList) {
        this.checkList = checkList;
    }

    public List<String> getProofFiles() {
        return proofFiles;
    }

    public void setProofFiles(List<String> proofFiles) {
        this.proofFiles = proofFiles;
    }

    public Trak getTrak() {
        return trak;
    }

    public void setTrak(Trak trak) {
        this.trak = trak;
    }

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}
	
	public String getTrakUUID() {
		return getTrak().getUuid();
	}
}

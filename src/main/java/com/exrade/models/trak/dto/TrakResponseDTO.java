package com.exrade.models.trak.dto;

import com.exrade.models.trak.TrakResponseCheckList;

import java.util.List;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.models.trak
 */
public class TrakResponseDTO {
    private String note;
    private List<String> files;
    private List<String> proofFiles;
    private Integer progress;
    private List<TrakResponseCheckList> checkList;
    private String trakUUID;

	public TrakResponseDTO(String note, List<String> files, List<String> proofFiles, Integer progress, List<TrakResponseCheckList> checkList, String trakUUID) {
		this.note = note;
		this.files = files;
		this.proofFiles = proofFiles;
		this.progress = progress;
		this.checkList = checkList;
		this.trakUUID = trakUUID;
	}

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

    public List<String> getProofFiles() {
        return proofFiles;
    }

    public void setProofFiles(List<String> proofFiles) {
        this.proofFiles = proofFiles;
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

    public String getTrakUUID() {
        return trakUUID;
    }

    public void setTrakUUID(String trakUUID) {
        this.trakUUID = trakUUID;
    }

}

package com.exrade.models.trak;

import java.util.List;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.models.trak
 */
public class TrakResponseCheckList extends TrakCheckList {
    private boolean completed;
    private List<String> proofFiles;

    public TrakResponseCheckList() {
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
    	this.completed = completed;
    }

	public List<String> getProofFiles() {
		return proofFiles;
	}

	public void setProofFiles(List<String> proofFiles) {
		this.proofFiles = proofFiles;
	}
}

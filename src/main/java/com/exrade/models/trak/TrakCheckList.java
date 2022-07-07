package com.exrade.models.trak;

import com.exrade.platform.persistence.BaseEntityUUID;

/**
 * @author Rhidoy
 * @created 19/10/2021
 * @package com.exrade.models.trak
 *
 * This class Represent Check-List for both Trak and Trak Response
 */
public class TrakCheckList extends BaseEntityUUID {
    private String item;
    private boolean proofRequired;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

	public boolean isProofRequired() {
		return proofRequired;
	}

	public void setProofRequired(boolean proofRequired) {
		this.proofRequired = proofRequired;
	}
}

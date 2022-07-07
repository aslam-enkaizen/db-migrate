package com.exrade.models.trak.dto;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.models.trak.dto
 */
public class TrakCreateDTO extends TrakBaseDTO {
    private String parentUUID;
    private Boolean blockchainEnabled = false;

    public String getParentUUID() {
        return parentUUID;
    }

    public void setParentUUID(String parentUUID) {
        this.parentUUID = parentUUID;
    }

	public Boolean getBlockchainEnabled() {
		return blockchainEnabled;
	}

	public void setBlockchainEnabled(Boolean blockchainEnabled) {
		this.blockchainEnabled = blockchainEnabled;
	}
}

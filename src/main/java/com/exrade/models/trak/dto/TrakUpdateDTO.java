package com.exrade.models.trak.dto;

import com.exrade.models.trak.TrakStatus;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.models.trak.dto
 */
public class TrakUpdateDTO extends TrakBaseDTO {
    private String uuid;
    private TrakStatus status;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public TrakStatus getStatus() {
        return status;
    }

    public void setStatus(TrakStatus status) {
        this.status = status;
    }
}

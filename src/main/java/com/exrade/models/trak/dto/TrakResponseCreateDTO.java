package com.exrade.models.trak.dto;

import com.exrade.models.trak.TrakResponse;

/**
 * @author Rhidoy
 * @created 19/10/2021
 * @package com.exrade.models.trak.dto
 * <p>
 * This mutable class represent the creation of Trak Response from Assignee
 */
public class TrakResponseCreateDTO {
    private final String trakUUID;
    private final String memberUuid;
    private final String responseUUID;
    private final TrakResponse response;

    public TrakResponseCreateDTO(String trakUUID, String memberUuid, TrakResponse response) {
        this(trakUUID, memberUuid, null, response);
    }

    public TrakResponseCreateDTO(String trakUUID, String memberUuid, String responseUUID, TrakResponse response) {
        this.trakUUID = trakUUID;
        this.memberUuid = memberUuid;
        this.responseUUID = responseUUID;
        this.response = response;
    }

    public String getTrakUUID() {
        return trakUUID;
    }


    public String getMemberUuid() {
        return memberUuid;
    }

    public TrakResponse getResponse() {
        return response;
    }

    public String getResponseUUID() {
        return responseUUID;
    }
}

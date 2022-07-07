package com.exrade.runtime.trak;

import com.exrade.models.trak.Trak;
import com.exrade.models.trak.TrakApproval;
import com.exrade.models.trak.TrakResponse;
import com.exrade.models.trak.dto.TrakApprovalCreateDTO;
import com.exrade.models.trak.dto.TrakCreateDTO;
import com.exrade.models.trak.dto.TrakResponseCreateDTO;
import com.exrade.models.trak.dto.TrakUpdateDTO;

import java.util.List;
import java.util.Map;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.runtime.trak
 */
public interface ITrakManager {
    Trak createTrak(TrakCreateDTO iTrak);

    List<Trak> listTraks(Map<String, String> iFilters);

    Trak updateTrak(TrakUpdateDTO iTrak);

    Trak getTrak(String iTrakUUID);

    void deleteTrak(String iTrakUUID);

    TrakResponse createTrakResponse(TrakResponseCreateDTO dto);

    List<TrakResponse> listTrakResponse(String trakUUID, String membershipUUID);

    TrakResponse updateTrakResponse(TrakResponseCreateDTO dto);

    TrakResponse getTrakResponseByUUID(String trakUUID, String responseUUID);

    List<TrakApproval> listTrakApproval(String trakUUID, String responseUUID, String membershipUUID);

    TrakApproval createTrakApproval(TrakApprovalCreateDTO trakApprovalCreateDTO);

    TrakApproval updateTrakApproval(TrakApprovalCreateDTO trakApprovalCreateDTO);

    TrakApproval getTrakApprovalByUUID(String trakUUID, String responseUUID, String approvalUUID);
}

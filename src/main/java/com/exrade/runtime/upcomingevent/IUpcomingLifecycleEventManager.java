package com.exrade.runtime.upcomingevent;

import com.exrade.models.upcomingevent.UpcomingLifecycleEvent;
import com.exrade.models.upcomingevent.dto.UpcomingLifecycleEventCreateDTO;

import java.util.List;
import java.util.Map;

/**
 * @author Rhidoy
 * @created 29/10/2021
 * @package com.exrade.runtime.upcomingevent
 */
public interface IUpcomingLifecycleEventManager {
    void createFromManager(UpcomingLifecycleEventCreateDTO dto);

    UpcomingLifecycleEvent create(UpcomingLifecycleEventCreateDTO dto);

    List<UpcomingLifecycleEvent> list(Map<String, String> iFilters);

    UpcomingLifecycleEvent update(String uuid, UpcomingLifecycleEvent value);

    UpcomingLifecycleEvent getByUUID(String UUID);

    List<UpcomingLifecycleEvent> getByMemberUUID(String UUID);

    void delete(String UUID);

//   List<UpcomingLifecycleEvent> get(String eventUUID, String type);
//
//    List<UpcomingLifecycleEvent> getBy(String eventUUID, String type, String membersUUID);
}

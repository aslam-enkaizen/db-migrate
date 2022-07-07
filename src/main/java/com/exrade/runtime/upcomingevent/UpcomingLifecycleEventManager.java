package com.exrade.runtime.upcomingevent;

import com.exrade.core.ExLogger;
import com.exrade.models.notification.Frequency;
import com.exrade.models.upcomingevent.UpcomingLifecycleEvent;
import com.exrade.models.upcomingevent.dto.UpcomingLifecycleEventCreateDTO;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExDuplicateKeyException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.exception.ExValidationException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.notification.IUserSettingsManager;
import com.exrade.runtime.notification.UserSettingsManager;
import com.exrade.runtime.upcomingevent.persistence.UpcomingLifecycleEventQuery;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.util.DateUtil;

import java.util.*;

import static com.exrade.runtime.rest.RestParameters.UUID;
import static com.exrade.runtime.rest.RestParameters.UpcomingLifecycleEventFields.*;

/**
 * @author Rhidoy
 * @created 29/10/2021
 * @package com.exrade.runtime.upcomingevent
 * <p>
 * This class create upcoming lifecycle event as one day, three day, one week,
 * two week and one month for users of specific event type and that event uuid
 * by getting user default notification settings from notification manager.
 */
public class UpcomingLifecycleEventManager implements
        IUpcomingLifecycleEventManager {

    private final PersistentManager persistenceManager = new PersistentManager();
    private final IUserSettingsManager userSettingsManager = new UserSettingsManager();
    private final IMembershipManager membershipManager;

    public UpcomingLifecycleEventManager() {
        this.membershipManager = userSettingsManager.getMembershipManager();
    }

    @Override
    public void createFromManager(UpcomingLifecycleEventCreateDTO dto) {
        if (!dto.getDate().after(new Date()))
            return; //not throwing exception as it called from manager

        UpcomingLifecycleEvent event = new UpcomingLifecycleEvent();
        event.setEventUUID(dto.getEventUUID());
        event.setObjectType(dto.getObjectType());
        event.setEventType(dto.getEventType());
        event.setDate(dto.getDate());
        event.setMembers(getMembers(dto.getMembers()));
        createEvent(event);
    }

    @Override
    public UpcomingLifecycleEvent create(UpcomingLifecycleEventCreateDTO dto) {
        //throw exception when date less then current date
        if (!dto.getDate().after(new Date()))
            throw new ExValidationException(ErrorKeys.VALIDATION_ERRORS);

        UpcomingLifecycleEvent event = new UpcomingLifecycleEvent();
        event.setEventUUID(dto.getEventUUID());
        event.setObjectType(dto.getObjectType());
        event.setEventType(dto.getEventType());
        event.setDate(dto.getDate());
        event.setMembers(getMembers(dto.getMembers()));
        createEvent(event);

        return event;
    }

    @Override
    public List<UpcomingLifecycleEvent> list(Map<String, String> iFilters) {
        QueryFilters filters = QueryFilters.create(iFilters);
        filters.putIfNotEmpty(UUID, iFilters.get(UUID));
        filters.putIfNotEmpty(EVENT_UUID, iFilters.get(EVENT_UUID));
        filters.putIfNotEmpty(EVENT_TYPE, iFilters.get(EVENT_TYPE));
        filters.putIfNotEmpty(MEMBERS, iFilters.get(MEMBERS));
        filters.putIfNotEmpty(DATE, iFilters.get(DATE));
        return persistenceManager
                .listObjects(new UpcomingLifecycleEventQuery(),
                        filters);
    }

    @Override
    public UpcomingLifecycleEvent update(String uuid, UpcomingLifecycleEvent value) {
        UpcomingLifecycleEvent data = get(uuid);
        data.setEventUUID(value.getEventUUID());
        data.setEventType(value.getEventType());
        data.setDate(value.getDate());
        data.setMembers(value.getMembers());
        data = persistenceManager.update(data);

        ExLogger.get().info("Updated upcoming lifecycle event: {} - {}",
                data.getUuid(), data.getEventType());
        return data;
    }

    @Override
    public UpcomingLifecycleEvent getByUUID(String UUID) {
        return get(UUID);
    }

    @Override
    public List<UpcomingLifecycleEvent> getByMemberUUID(String UUID) {
        Map<String, String> filter = new HashMap<>();
        filter.put(MEMBERS, UUID);
        return list(filter);
    }

    @Override
    public void delete(String UUID) {
        UpcomingLifecycleEvent data = this.getByUUID(UUID);
        persistenceManager.delete(data);
        ExLogger.get().info("Deleted upcoming lifecycle event: {}", UUID);
    }

//    @Override
//    public List<UpcomingLifecycleEvent> get(String eventUUID, String type) {
//        Map<String, String> filter = new HashMap<>();
//        filter.put(EVENT_UUID, eventUUID);
//        filter.put(EVENT_TYPE, type);
//        return list(filter);
//    }
//
//    @Override
//    public List<UpcomingLifecycleEvent> getBy(String eventUUID, String type, String membersUUID) {
//        Map<String, String> filter = new HashMap<>();
//        filter.put(EVENT_UUID, eventUUID);
//        filter.put(EVENT_TYPE, type);
//        filter.put(MEMBERS_UUID, membersUUID);
//        return list(filter);
//    }

    private UpcomingLifecycleEvent get(String UUID) {
        UpcomingLifecycleEvent data = persistenceManager
                .readObjectByUUID(UpcomingLifecycleEvent.class, UUID);
        if (data == null)
            throw new ExNotFoundException("Upcoming lifecycle event not " +
                    "found for Id " + UUID);
        return data;
    }

    private List<Negotiator> getMembers(List<String> membersList) {
        List<Negotiator> members = new ArrayList<>();
        for (String s : membersList) {
            members.add(membershipManager
                    .findByUUID(s, true));
        }
        return members;
    }

    private void createEvent(UpcomingLifecycleEvent event) {
        //first checking event existence for current event id
        HashMap<String, String> filter = new HashMap<>();
        filter.put(EVENT_UUID, event.getEventUUID());
        filter.put(EVENT_TYPE, event.getEventType().toString());
        if (list(filter).size() > 0)
            throw new ExDuplicateKeyException(
                    ErrorKeys.UPCOMING_EVENTS_ALREADY_EXISTS);

        //create 5 event with only selected users
        List<Negotiator> oneDay = new ArrayList<>();
        List<Negotiator> threeDay = new ArrayList<>();
        List<Negotiator> oneWeek = new ArrayList<>();
        List<Negotiator> twoWeek = new ArrayList<>();
        List<Negotiator> month = new ArrayList<>();

        for (Negotiator member : event.getMembers()) {
            for (Frequency settingType :
                    userSettingsManager.getUpcomingUserSettingFrequency(member.getIdentifier())) {
                switch (settingType) {
                    case ONE_DAY:
                        oneDay.add(member);
                        break;
                    case THREE_DAYS:
                        threeDay.add(member);
                        break;
                    case ONE_WEEK:
                        oneWeek.add(member);
                        break;
                    case TWO_WEEKS:
                        twoWeek.add(member);
                        break;
                    case ONE_MONTH:
                        month.add(member);
                        break;
                    default:  //add current user to all list
                        oneDay.add(member);
                        threeDay.add(member);
                        oneWeek.add(member);
                        twoWeek.add(member);
                        month.add(member);
                }
            }
        }

        Date currentDate = new Date();
        int day;
        for (int i = 0; i < 5; i++) {
            switch (i) {
                case 0:
                    //create for one day
                    day = -1;
                    event.setMembers(oneDay);
                    break;
                case 1:
                    //create for three day
                    day = -3;
                    event.setMembers(threeDay);
                    break;
                case 2:
                    //create for one week
                    day = -7;
                    event.setMembers(oneWeek);
                    break;
                case 3:
                    //create for two week
                    day = -14;
                    event.setMembers(twoWeek);
                    break;
                default:
                    //create for month
                    day = 30;
                    event.setMembers(month);
                    break;
            }

            Date date = DateUtil.
                    addDayWithDate(event.getDate(), day, false);
            //when notification sent date is after current date
            if (date.after(currentDate)) {
                event.setDate(date);
                //create new event for different generated id
                UpcomingLifecycleEvent data=new UpcomingLifecycleEvent();
                data.setEventUUID(event.getEventUUID());
                data.setObjectType(event.getObjectType());
                data.setEventType(event.getEventType());
                data.setDate(event.getDate());
                data.setMembers(event.getMembers());
                data = persistenceManager.create(data);
                ExLogger.get().info("Created upcoming lifecycle event for " +
                                day + " day: {} - {}",
                        data.getUuid(), data.getEventType());
            }
        }
    }
}

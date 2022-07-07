package com.exrade.runtime.notification;

import com.exrade.core.ExLogger;
import com.exrade.models.notification.*;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.notification.persistence.UserSettingQuery;
import com.exrade.runtime.rest.RestParameters.UserSettingFields;
import com.exrade.runtime.rest.RestParameters.UserSettingFilters;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserSettingsManager implements IUserSettingsManager {

    private static final Logger LOGGER = ExLogger.get();
    private final PersistentManager persistenceManager = new PersistentManager();
    private final INotificationSettingsManager notificationSettingManager = new NotificationSettingsManager();
    private final IMembershipManager membershipManager = new MembershipManager();

    @Override
    public IMembershipManager getMembershipManager() {
        return membershipManager;
    }

    @Override
    public UserSetting saveUserSetting(UserSetting iUserSetting) {
        UserSetting userSetting = persistenceManager.update(iUserSetting);
        LOGGER.info("Saved user settings {}", userSetting);
        return getUserSetting(iUserSetting.getMembership().getUuid());
    }

    @Override
    public UserSetting getUserSetting(String iMembershipUUID) {
        QueryFilters filters = QueryFilters.create(UserSettingFields.MEMBERSHIP, iMembershipUUID);
        UserSetting userSetting = persistenceManager.readObject(new UserSettingQuery(), filters);

        // if no settings exists then create from currently active notification-settings
        if (userSetting == null) {
            userSetting = UserSetting.create(membershipManager.findByUUID(iMembershipUUID, true));
        }

        for (NotificationSetting notificationSetting : notificationSettingManager.getEnabledNotificationSettings()) {
            if (userSetting.getUserNotificationSetting(notificationSetting.getUuid()) == null) {
                UserNotificationSetting userNotificationSetting = UserNotificationSetting.create(notificationSetting, Frequency.IMMEDIATELY);
                userSetting.getUserNotificationSettings().add(userNotificationSetting);
            }
        }

        return userSetting;
    }

    @Override
    public UserSetting saveUserSetting(String iMembershipUUID, Map<String, Frequency> notificationSettings) {
        UserSetting userSetting = getUserSetting(iMembershipUUID);

        for (String notificationSettingUUID : notificationSettings.keySet()) {
            UserNotificationSetting userNotificationSetting = userSetting.getUserNotificationSetting(notificationSettingUUID);
            userNotificationSetting.setFrequency(notificationSettings.get(notificationSettingUUID));
        }
        return saveUserSetting(userSetting);
    }

    @Override
    public List<Membership> getUsersWithSetting(ChannelType iChannelType, NotificationType iNotificationType) {
        NotificationSetting notificationSetting = notificationSettingManager.getNotificationSetting(iChannelType, iNotificationType);
        QueryFilters filters = QueryFilters.create(UserSettingFilters.NOTIFICATION_SETTING, notificationSetting);
        List<UserSetting> userSettings = persistenceManager.listObjects(new UserSettingQuery(), filters);

        List<Membership> memberships = new ArrayList<>();
        for (UserSetting setting : userSettings) {
            memberships.add(setting.getMembership());
        }

        return memberships;
    }

    @Override
    public boolean isNotificationActive(String iMembershipUUID, ChannelType iChannelType, NotificationType iNotificationType,
                                        Frequency iFrequency) {
        UserSetting setting = getUserSetting(iMembershipUUID);
        if (setting != null) {
            NotificationSetting notificationSetting = notificationSettingManager.getNotificationSetting(iChannelType, iNotificationType);
            UserNotificationSetting userNotificationSetting = setting.getUserNotificationSetting(notificationSetting.getUuid());
            if (userNotificationSetting.getFrequency() == iFrequency)
                return true;
        }
        return false;
    }

    @Override
    public List<Frequency> getUpcomingUserSettingFrequency(String iMembershipUUID) {
        QueryFilters filters = QueryFilters.create(UserSettingFields.MEMBERSHIP, iMembershipUUID);
        UserSetting userSetting = persistenceManager.readObject(new UserSettingQuery(), filters);
        if (userSetting != null) {
            for (UserNotificationSetting userNotificationSetting : userSetting.getUserNotificationSettings()) {
                NotificationSetting notificationSetting = userNotificationSetting.getNotificationSetting();
                if (notificationSetting != null &&
                        notificationSetting.getNotificationType()
                                .equals(NotificationType
                                        .UPCOMING_LIFE_CYCLE_EVENTS)) {
                    return notificationSetting.getAllowedFrequencies();
                }
            }
        }
        return Collections.singletonList(Frequency.ALL);
    }

    @Override
    public List<Frequency> createUpcomingEventSettings(List<Frequency> frequencyList, String iMembershipUUID) {
        UserSetting userSettings = getUserSetting(iMembershipUUID);
        //now create new setting for upcoming notification
        NotificationSetting setting = NotificationSetting.create(NotificationType.UPCOMING_LIFE_CYCLE_EVENTS, ChannelType.EMAIL, frequencyList);
        UserNotificationSetting userSetting = UserNotificationSetting.create(setting);

        userSettings.getUserNotificationSettings().add(userSetting);
        //save setting
        saveUserSetting(userSettings);
        return frequencyList;
    }

    @Override
    public List<Frequency> updateUpcomingEventSettings(List<Frequency> frequencyList, String iMembershipUUID) {
        UserSetting userSettings = getUserSetting(iMembershipUUID);
        if (userSettings == null)
            throw new ExNotFoundException(ErrorKeys.USER_SETTING_NOT_FOUND.toString());
        boolean notFound = true;

        for (UserNotificationSetting setting : userSettings
                .getUserNotificationSettings()) {
            NotificationSetting notificationSetting = setting
                    .getNotificationSetting();
            if (notificationSetting != null &&
                    notificationSetting.getNotificationType()
                            .equals(NotificationType
                                    .UPCOMING_LIFE_CYCLE_EVENTS)) {
                //now update frequency
                notificationSetting.setAllowedFrequencies(frequencyList);
                notFound = false;
                break;
            }
        }

        if (notFound)
            throw new ExNotFoundException(ErrorKeys.USER_SETTING_NOT_FOUND.toString());

        saveUserSetting(userSettings);
        return frequencyList;
    }


}

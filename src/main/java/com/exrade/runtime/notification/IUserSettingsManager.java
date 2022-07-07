package com.exrade.runtime.notification;

import com.exrade.models.notification.ChannelType;
import com.exrade.models.notification.Frequency;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.notification.UserSetting;
import com.exrade.models.userprofile.Membership;
import com.exrade.runtime.userprofile.IMembershipManager;

import java.util.List;
import java.util.Map;

public interface IUserSettingsManager {

    IMembershipManager getMembershipManager();

    UserSetting saveUserSetting(UserSetting iUserSetting);

    UserSetting saveUserSetting(String iMembershipUUID, Map<String, Frequency> notificationSettings);

    UserSetting getUserSetting(String iMembershipUUID);

    List<Membership> getUsersWithSetting(ChannelType iChannelType, NotificationType iNotificationType);

    boolean isNotificationActive(String iMembershipUUID, ChannelType iChannelType, NotificationType iNotificationType, Frequency iFrequency);

    List<Frequency> getUpcomingUserSettingFrequency(String iMembershipUUID);

    List<Frequency> createUpcomingEventSettings(List<Frequency> frequencyList, String iMembershipUUID);

    List<Frequency> updateUpcomingEventSettings(List<Frequency> frequencyList, String iMembershipUUID);
}

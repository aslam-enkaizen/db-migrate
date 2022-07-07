package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.notification.Frequency;
import com.exrade.models.notification.UserSetting;

import java.util.List;
import java.util.Map;

public interface UserSettingsAPI {

    UserSetting saveUserSetting(ExRequestEnvelope request, String iMembershipUUID, Map<String, Frequency> notificationSettings);

    UserSetting getUserSetting(ExRequestEnvelope request, String iMembershipUUID);

    List<Frequency> createUpcomingEventSettings(List<Frequency> frequencyList, String iMembershipUUID);

    List<Frequency> updateUpcomingEventSettings(List<Frequency> frequencyList, String iMembershipUUID);

    List<Frequency> getUpcomingUserSettingFrequency(String iMembershipUUID);

}

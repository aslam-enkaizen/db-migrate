package com.exrade.api.impl;

import com.exrade.api.UserSettingsAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.notification.Frequency;
import com.exrade.models.notification.UserSetting;
import com.exrade.runtime.notification.IUserSettingsManager;
import com.exrade.runtime.notification.UserSettingsManager;
import com.exrade.util.ContextHelper;

import java.util.List;
import java.util.Map;

public class UserSettingsManagerAdapter implements UserSettingsAPI {

    private final IUserSettingsManager manager = new UserSettingsManager();

    @Override
    public UserSetting saveUserSetting(ExRequestEnvelope request, String iMembershipUUID,
                                       Map<String, Frequency> notificationSettings) {
        ContextHelper.initContext(request);
        return manager.saveUserSetting(iMembershipUUID, notificationSettings);
    }

    @Override
    public List<Frequency> createUpcomingEventSettings(List<Frequency> frequencyList, String iMembershipUUID) {
        return manager.createUpcomingEventSettings(frequencyList, iMembershipUUID);
    }

    @Override
    public List<Frequency> updateUpcomingEventSettings(List<Frequency> frequencyList, String iMembershipUUID) {
        return manager.updateUpcomingEventSettings(frequencyList, iMembershipUUID);
    }

    @Override
    public List<Frequency> getUpcomingUserSettingFrequency(String iMembershipUUID) {
        return manager.getUpcomingUserSettingFrequency(iMembershipUUID);
    }

    @Override
    public UserSetting getUserSetting(ExRequestEnvelope request, String iMembershipUUID) {
        ContextHelper.initContext(request);
        return manager.getUserSetting(iMembershipUUID);
    }

}

package com.exrade.models.notification;

import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.runtime.timer.TimeProvider;
import com.google.common.base.MoreObjects;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class UserSetting extends BaseEntityUUIDTimeStampable {

	private Membership membership;
	private List<UserNotificationSetting> userNotificationSettings = new ArrayList<>();

	public static UserSetting create(Membership iMembership){
		Assert.notNull(iMembership, "iMembership");

		UserSetting userSetting = new UserSetting();
		userSetting.setMembership(iMembership);
		userSetting.setCreationDate(TimeProvider.now());
		return userSetting;
	}

	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

	public List<UserNotificationSetting> getUserNotificationSettings() {
		return userNotificationSettings;
	}

	public void setUserNotificationSettings(List<UserNotificationSetting> notificationSettings) {
		this.userNotificationSettings = notificationSettings;
	}

	public UserNotificationSetting getUserNotificationSetting(String iNotificationSettingUUID){
		for(UserNotificationSetting setting : getUserNotificationSettings()){
			if(setting.getNotificationSetting().getUuid().equals(iNotificationSettingUUID))
				return setting;
		}

		return null;
	}

	@Override
	public String toString(){
		return MoreObjects.toStringHelper(getClass().getSimpleName()).add("uuid", getUuid()).add("membership", getMembership()).toString();
	}
}

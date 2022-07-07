package com.exrade.runtime.userprofile.persistence;

import com.exrade.models.userprofile.Profile;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.userprofile.persistence.query.ProfileQuery;

import java.util.ArrayList;
import java.util.List;

public class ProfilePersistenceManager extends PersistentManager {

	public List<Profile> listProfiles(QueryFilters filters){
		List<Profile> profiles = new ArrayList<>();
		profiles = listObjects(new ProfileQuery(), filters);
		return profiles;
	}
	
}
package com.exrade.runtime.userprofile.persistence;

import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.userprofile.persistence.query.MemberProfileQuery;

import java.util.List;

public class MemberProfilePersistence extends PersistentManager {

	
	public List<Membership> find(QueryFilters iFilters){
		OrientSqlBuilder queryBuilder = new MemberProfileQuery();
		return listObjects(queryBuilder, iFilters);
	}
	
	public Membership findMembership(QueryFilters iFilters){
		OrientSqlBuilder queryBuilder = new MemberProfileQuery();
		return readObject(queryBuilder, iFilters);
	}
	
}

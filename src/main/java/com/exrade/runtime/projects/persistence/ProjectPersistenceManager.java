package com.exrade.runtime.projects.persistence;

import com.exrade.models.projects.Project;
import com.exrade.platform.persistence.ConnectionManager;
import com.exrade.platform.persistence.IConnectionManager;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public class ProjectPersistenceManager extends PersistentManager {

	
	
	public ProjectPersistenceManager() {
		this(ConnectionManager.getInstance());
	}

	public ProjectPersistenceManager(IConnectionManager iConnectionManager) {
		super(iConnectionManager);
	}
	
	public Project readByUUID(String iProjectUUID){
		QueryFilters filters = QueryFilters.create(QueryParameters.UUID,iProjectUUID);
		return readObject(new ProjectQuery(), filters);
	}
	
	public List<Project> listProjects(QueryFilters iFilters){
		ProjectQuery projectQuery = new ProjectQuery();
		return listObjects(projectQuery,iFilters); 
	}
}
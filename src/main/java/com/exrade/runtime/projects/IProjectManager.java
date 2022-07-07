package com.exrade.runtime.projects;

import com.exrade.models.negotiation.INegotiationSummary;
import com.exrade.models.projects.Priority;
import com.exrade.models.projects.Project;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IProjectManager {
	
	public Project read(String projectUUID);
	
	/**
	 * Create a new Project for the user, project Name must be unique
	 * @param userID 
	 * @param projectName  
	 * @param iPriority
	 * @return A project summary contian name and UUID of the new Project
	 */
	public String createProject(String projectName,Priority iPriority);
	
	/**
	 *  Delete a project
	 * @param userID
	 * @param projectUUID  
	 * @return void
	 */
	public void deleteProject(String projectUUID);
	
	/**
	 * Adds a negotiation to a project
	 * @param userID
	 * @param negotiationID
	 * @param projectUUID
	 * @return void
	 */
	public void addNegotiation(String negotiationID,String projectUUID);
	
	/**
	 * Lists all Negotiations in a psecified Project
	 * @param userID
	 * @param projectUUID
	 * @return
	 */
	public List<INegotiationSummary> listNegotiations(String projectUUID);

	/**
	 * List all projects belonging to a user
	 * @param userID
	 * @return
	 */
	public List<Project> listProjects(QueryFilters iFilters);

}

package com.exrade.runtime.projects;

import com.exrade.models.negotiation.INegotiationSummary;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.projects.Priority;
import com.exrade.models.projects.Project;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExDuplicateKeyException;
import com.exrade.platform.persistence.query.PagedList;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.negotiation.persistence.NegotiationPersistenceManager;
import com.exrade.runtime.projects.persistence.ProjectPersistenceManager;
import com.exrade.util.ContextHelper;

import java.util.List;

public class ProjectManager implements IProjectManager {
	
	private ProjectPersistenceManager projectPersistentManager;
	private static NegotiationPersistenceManager negPersistanceManager= new NegotiationPersistenceManager();

	public ProjectManager() {
		this(new ProjectPersistenceManager());
	}

	public ProjectManager(ProjectPersistenceManager iProjectPersistentManager) {
		this.projectPersistentManager = iProjectPersistentManager;
	}

	@Override
	public String createProject(String projectName,Priority iPriority) {
		Negotiator owner = ContextHelper.getMembership();
		
		Project newProject=new Project(owner,projectName,iPriority);
		String projectUUID = null;
		try {
			projectPersistentManager.create(newProject);
			projectUUID = newProject.getUuid();
		}
		catch(ExDuplicateKeyException due){
			throw new ExDuplicateKeyException(ErrorKeys.PROJECT_ALREADY_EXISTS);
		}
		return projectUUID;
	}

	@Override
	public void deleteProject(String projectUUID) {
		Project proj= projectPersistentManager.readByUUID(projectUUID);
		projectPersistentManager.delete(proj);
	}

	@Override
	public void addNegotiation(String negotiationID,String projectUUID) {
		Project project= projectPersistentManager.readByUUID(projectUUID);
		Negotiation neg=negPersistanceManager.readByUUID(negotiationID);
		project.getNegotiations().add(neg);
		projectPersistentManager.update(project);
	}

	@Override
	public List<INegotiationSummary> listNegotiations(String projectUUID) {
		Project project=projectPersistentManager.readByUUID(projectUUID);
		return new NegotiationManager().wrapNegotiations((PagedList<Negotiation>)project.getNegotiations(), ContextHelper.getMembership());
	}
	
	@Override
	public List<Project> listProjects(QueryFilters iFilters) {
		return projectPersistentManager.listProjects(iFilters);
	}

	@Override
	public Project read(String projectUUID) {
		return projectPersistentManager.readByUUID(projectUUID);
	}

}

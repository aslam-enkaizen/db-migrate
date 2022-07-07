package com.exrade.api.impl;

import com.exrade.api.ProjectAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.negotiation.INegotiationSummary;
import com.exrade.models.projects.Priority;
import com.exrade.models.projects.Project;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.projects.IProjectManager;
import com.exrade.runtime.projects.ProjectManager;
import com.exrade.util.ContextHelper;

import java.util.List;

public class ProjectManagerAdapter implements ProjectAPI {

	private IProjectManager manager = new ProjectManager();
	
	@Override
	public Project read(ExRequestEnvelope request, String projectUUID) {
		ContextHelper.initContext(request);
		return manager.read(projectUUID);
	}

	@Override
	public String createProject(ExRequestEnvelope request, String projectName,
			Priority iPriority) {
		ContextHelper.initContext(request);
		return manager.createProject(projectName, iPriority);
	}

	@Override
	public void deleteProject(ExRequestEnvelope request, String projectUUID) {
		ContextHelper.initContext(request);
		manager.deleteProject(projectUUID);
	}

	@Override
	public void addNegotiation(ExRequestEnvelope request, String negotiationID,
			String projectUUID) {
		ContextHelper.initContext(request);
		manager.addNegotiation(negotiationID, projectUUID);
	}

	@Override
	public List<INegotiationSummary> listNegotiations(
			ExRequestEnvelope request, String projectUUID) {
		ContextHelper.initContext(request);
		return manager.listNegotiations(projectUUID);
	}

	@Override
	public List<Project> listProjects(ExRequestEnvelope request,
			QueryFilters iFilters) {
		ContextHelper.initContext(request);
		return manager.listProjects(iFilters);
	}

}

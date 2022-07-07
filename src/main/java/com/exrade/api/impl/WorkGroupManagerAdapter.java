package com.exrade.api.impl;

import com.exrade.api.WorkGroupAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.negotiation.INegotiationSummary;
import com.exrade.models.workgroup.Post;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.models.workgroup.WorkGroupComment;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.PostFields;
import com.exrade.runtime.rest.RestParameters.WorkGroupCommentFields;
import com.exrade.runtime.rest.RestParameters.WorkGroupFields;
import com.exrade.runtime.workgroup.IWorkGroupManager;
import com.exrade.runtime.workgroup.WorkGroupManager;
import com.exrade.runtime.workgroup.persistence.WorkGroupPersistenceManager.WorkGroupQFilters;
import com.exrade.util.ContextHelper;

import java.util.List;
import java.util.Map;

public class WorkGroupManagerAdapter implements WorkGroupAPI {

	private IWorkGroupManager manager = new WorkGroupManager();
	
	@Override
	public WorkGroup createWorkGroup(ExRequestEnvelope request, WorkGroup iWorkGroup) {
		ContextHelper.initContext(request);
		return manager.createWorkGroup(iWorkGroup);
	}

	@Override
	public void deleteWorkGroup(ExRequestEnvelope request, String iWorkGroupUUID) {
		ContextHelper.initContext(request);
		manager.deleteWorkGroup(iWorkGroupUUID);
	}

	@Override
	public WorkGroup updateWorkGroup(ExRequestEnvelope request, WorkGroup iWorkGroup) {
		ContextHelper.initContext(request);
		return manager.updateWorkGroup(iWorkGroup);
	}
	
	@Override
	public List<WorkGroup> listWorkGroups(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		
		filters.putIfNotNull(RestParameters.KEYWORDS, iFilters.get(RestParameters.KEYWORDS));
		filters.putIfNotNull(WorkGroupQFilters.TAGS, iFilters.get(WorkGroupQFilters.TAGS));
		filters.putIfNotNull(WorkGroupFields.NEGOTIATION_UUID, iFilters.get(WorkGroupFields.NEGOTIATION_UUID));
		
		if (filters.isNullOrEmpty(QueryParameters.SORT)){
			filters.put(QueryParameters.SORT, OrientSqlBuilder.DESC_SORT+WorkGroupFields.UPDATE_DATE);
		}
		
		return manager.listWorkGroups(filters);
	}

	@Override
	public WorkGroup getWorkGroupByUUID(ExRequestEnvelope request, String iWorkGroupUUID) {
		ContextHelper.initContext(request);
		return manager.getWorkGroupByUUID(iWorkGroupUUID);
	}

	@Override
	public void addWorkGroupMember(ExRequestEnvelope request, String iWorkGroupUUID, String membershipUUID) {
		ContextHelper.initContext(request);
		manager.addWorkGroupMember(iWorkGroupUUID, membershipUUID);
	}

	@Override
	public void removeWorkGroupMember(ExRequestEnvelope request, String iWorkGroupUUID, String membershipUUID) {
		ContextHelper.initContext(request);
		manager.removeWorkGroupMember(iWorkGroupUUID, membershipUUID);
	}

	@Override
	public void addNegotiation(ExRequestEnvelope request, String iWorkGroupUUID, String iNegotiationUUID) {
		ContextHelper.initContext(request);
		manager.addNegotiation(iWorkGroupUUID, iNegotiationUUID);
	}

	@Override
	public void removeNegotiation(ExRequestEnvelope request, String iWorkGroupUUID, String iNegotiationUUID) {
		ContextHelper.initContext(request);
		manager.removeNegotiation(iWorkGroupUUID, iNegotiationUUID);
	}

	@Override
	public List<INegotiationSummary> listNegotiations(ExRequestEnvelope request, String iWorkGroupUUID,
			Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		return manager.listNegotiations(iWorkGroupUUID, QueryFilters.create(iFilters));
	}

	@Override
	public Post createPost(ExRequestEnvelope request, String iWorkGroupUUID, Post iPost) {
		ContextHelper.initContext(request);
		return manager.createPost(iWorkGroupUUID, iPost);
	}

	@Override
	public void deletePost(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID) {
		ContextHelper.initContext(request);
		manager.deletePost(iWorkGroupUUID, iPostUUID);
	}

	@Override
	public Post updatePost(ExRequestEnvelope request, String iWorkGroupUUID, Post iPost) {
		ContextHelper.initContext(request);
		return manager.updatePost(iWorkGroupUUID, iPost);
	}

	@Override
	public List<Post> listPosts(ExRequestEnvelope request, String iWorkGroupUUID, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putIfNotNull(RestParameters.KEYWORDS, iFilters.get(RestParameters.KEYWORDS));
		if (filters.isNullOrEmpty(QueryParameters.SORT)){
			filters.put(QueryParameters.SORT, OrientSqlBuilder.DESC_SORT+PostFields.UPDATE_DATE);
		}
		
		return manager.listPosts(iWorkGroupUUID, filters);
	}

	@Override
	public Post getPostByUUID(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID) {
		ContextHelper.initContext(request);
		return manager.getPostByUUID(iWorkGroupUUID, iPostUUID);
	}
	
	@Override
	public WorkGroupComment createComment(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, WorkGroupComment iComment) {
		ContextHelper.initContext(request);
		return manager.createComment(iWorkGroupUUID, iPostUUID, iComment);
	}

	@Override
	public void deleteComment(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, String iCommentUUID) {
		ContextHelper.initContext(request);
		manager.deleteComment(iWorkGroupUUID, iPostUUID, iCommentUUID);
	}

	@Override
	public WorkGroupComment updateComment(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, WorkGroupComment iComment) {
		ContextHelper.initContext(request);
		return manager.updateComment(iWorkGroupUUID, iPostUUID, iComment);
	}

	@Override
	public List<WorkGroupComment> listComments(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putIfNotNull(RestParameters.KEYWORDS, iFilters.get(RestParameters.KEYWORDS));
		if (filters.isNullOrEmpty(QueryParameters.SORT)){
			filters.put(QueryParameters.SORT, OrientSqlBuilder.DESC_SORT+WorkGroupCommentFields.UPDATE_DATE);
		}
		
		return manager.listComments(iWorkGroupUUID, iPostUUID, filters);
	}

	@Override
	public WorkGroupComment getCommentByUUID(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, String iCommentUUID) {
		ContextHelper.initContext(request);
		return manager.getCommentByUUID(iWorkGroupUUID, iPostUUID, iCommentUUID);
	}
	
	@Override
	public List<SearchResultSummary> listSearchResultSummary(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		return manager.listSearchResultSummary();
	}
	
	@Override
	public List<Map<String, Object>> listWorkGroupFiles(ExRequestEnvelope request, String iWorkGroupUUID) {
		ContextHelper.initContext(request);
		return manager.listFiles(iWorkGroupUUID);
	}
	
	@Override
	public List<Map<String, Object>> listPostFiles(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID) {
		ContextHelper.initContext(request);
		return manager.listFiles(iWorkGroupUUID, iPostUUID);
	}
}

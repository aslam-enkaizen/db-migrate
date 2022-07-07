package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.negotiation.INegotiationSummary;
import com.exrade.models.workgroup.Post;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.models.workgroup.WorkGroupComment;
import com.exrade.platform.persistence.SearchResultSummary;

import java.util.List;
import java.util.Map;

public interface WorkGroupAPI {

	WorkGroup createWorkGroup(ExRequestEnvelope request, WorkGroup iWorkGroup);
	
	void deleteWorkGroup(ExRequestEnvelope request, String iWorkGroupUUID);
	
	WorkGroup updateWorkGroup(ExRequestEnvelope request, WorkGroup iWorkGroup);
	
	List<WorkGroup> listWorkGroups(ExRequestEnvelope request, Map<String, String> iFilters);
	
	WorkGroup getWorkGroupByUUID(ExRequestEnvelope request, String iWorkGroupUUID);

	void addWorkGroupMember(ExRequestEnvelope request, String iWorkGroupUUID, String membershipUUID);
	
	void removeWorkGroupMember(ExRequestEnvelope request, String iWorkGroupUUID, String membershipUUID);
	
	Post createPost(ExRequestEnvelope request, String iWorkGroupUUID, Post iPost);
	
	void deletePost(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID);
	
	Post updatePost(ExRequestEnvelope request, String iWorkGroupUUID, Post iPost);
	
	List<Post> listPosts(ExRequestEnvelope request, String iWorkGroupUUID, Map<String, String> iFilters);
	
	Post getPostByUUID(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID);
	
	WorkGroupComment createComment(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, WorkGroupComment iComment);
	
	void deleteComment(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, String iCommentUUID);
	
	WorkGroupComment updateComment(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, WorkGroupComment iComment);
	
	List<WorkGroupComment> listComments(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, Map<String, String> iFilters);
	
	WorkGroupComment getCommentByUUID(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID, String iCommentUUID);

	List<SearchResultSummary> listSearchResultSummary(ExRequestEnvelope request, Map<String, String> iFilters);

	void addNegotiation(ExRequestEnvelope requestEnvelope, String iWorkGroupUUID, String iNegotiationUUID);

	void removeNegotiation(ExRequestEnvelope requestEnvelope, String iWorkGroupUUID, String iNegotiationUUID);
	
	List<INegotiationSummary> listNegotiations(ExRequestEnvelope request, String iWorkGroupUUID, Map<String, String> iFilters);

	List<Map<String, Object>> listWorkGroupFiles(ExRequestEnvelope request, String iWorkGroupUUID);

	List<Map<String, Object>> listPostFiles(ExRequestEnvelope request, String iWorkGroupUUID, String iPostUUID);
}

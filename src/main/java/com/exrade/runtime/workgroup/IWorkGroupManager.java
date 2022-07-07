package com.exrade.runtime.workgroup;

import com.exrade.models.negotiation.INegotiationSummary;
import com.exrade.models.workgroup.Post;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.models.workgroup.WorkGroupComment;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;
import java.util.Map;

public interface IWorkGroupManager {

	WorkGroup createWorkGroup(WorkGroup iWorkGroup);

	WorkGroup updateWorkGroup(WorkGroup iWorkGroup);
	
	void deleteWorkGroup(String uuid);
	
	WorkGroup getWorkGroupByUUID(String iWorkGroupUUID);
	
	List<WorkGroup> listWorkGroups(QueryFilters iFilters);
	
	void addWorkGroupMember(String iWorkGroupUUID, String membershipUUID);
	
	void removeWorkGroupMember(String iWorkGroupUUID, String membershipUUID);
	
	Post createPost(String iWorkGroupUUID, Post iPost);
	
	void deletePost(String iWorkGroupUUID, String iPostUUID);
	
	Post updatePost(String iWorkGroupUUID, Post iPost);
	
	List<Post> listPosts(String iWorkGroupUUID, QueryFilters iFilters);
	
	Post getPostByUUID(String iWorkGroupUUID, String iPostUUID);
	
	WorkGroupComment createComment(String iWorkGroupUUID, String iPostUUID, WorkGroupComment iComment);
	
	void deleteComment(String iWorkGroupUUID, String iPostUUID, String iCommentUUID);
	
	WorkGroupComment updateComment(String iWorkGroupUUID, String iPostUUID, WorkGroupComment iComment);
	
	List<WorkGroupComment> listComments(String iWorkGroupUUID, String iPostUUID, QueryFilters iFilters);
	
	WorkGroupComment getCommentByUUID(String iWorkGroupUUID, String iPostUUID, String iCommentUUID);

	List<SearchResultSummary> listSearchResultSummary();

	void addNegotiation(String iWorkGroupUUID, String iNegotiationUUID);

	void removeNegotiation(String iWorkGroupUUID, String iNegotiationUUID);
	
	List<INegotiationSummary> listNegotiations(String iWorkGroupUUID, QueryFilters iFilters);
	
	List<Map<String, Object>> listFiles(String iWorkGroupUUID);
	
	List<Map<String, Object>> listFiles(String iWorkGroupUUID, String iPostUUID);
}

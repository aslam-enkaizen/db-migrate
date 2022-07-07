package com.exrade.runtime.workgroup;

import com.exrade.core.ExLogger;
import com.exrade.models.activity.Verb;
import com.exrade.models.negotiation.INegotiationSummary;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.workgroup.Post;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.models.workgroup.WorkGroupComment;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.PagedList;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.filemanagement.FileManager;
import com.exrade.runtime.filemanagement.FileMetadata;
import com.exrade.runtime.filemanagement.IFileManager;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.WorkGroupNotificationEvent;
import com.exrade.runtime.rest.RestParameters.PostFields;
import com.exrade.runtime.rest.RestParameters.WorkGroupCommentFields;
import com.exrade.runtime.rest.RestParameters.WorkGroupFields;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.runtime.workgroup.persistence.*;
import com.exrade.runtime.workgroup.persistence.WorkGroupPersistenceManager.WorkGroupQFilters;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import org.slf4j.Logger;

import java.util.*;

import static com.exrade.platform.security.Security.hasAccessPermission;

public class WorkGroupManager implements IWorkGroupManager {

	private static final Logger LOGGER = ExLogger.get();

	private WorkGroupPersistenceManager workGroupPersistentManager;

	private NotificationManager notificationManager = new NotificationManager();

	private IMembershipManager membershipManager = new MembershipManager();

	private IFileManager fileManager = new FileManager();

	public WorkGroupManager() {
		this(new WorkGroupPersistenceManager());
	}

	public WorkGroupManager(WorkGroupPersistenceManager iWorkGroupPersistenceManager) {
		this.workGroupPersistentManager = iWorkGroupPersistenceManager;
	}

	@Override
	public WorkGroup createWorkGroup(WorkGroup iWorkGroup) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(requestorMembership == null)
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		if(iWorkGroup == null)
			throw new ExParamException(ErrorKeys.PARAM_INVALID);

		iWorkGroup.setOwner(requestorMembership);
		iWorkGroup.setTags(new HashSet<String>(ExCollections.toLowerCase(iWorkGroup.getTags())));
		iWorkGroup.setCreated(TimeProvider.now());
		iWorkGroup.setUpdated(iWorkGroup.getCreated());

		WorkGroup createdWorkGroup = workGroupPersistentManager.create(iWorkGroup);

		fileManager.updateFileMetadata(createdWorkGroup);
		notificationManager.process(new WorkGroupNotificationEvent(NotificationType.WORKGROUP_CREATED, createdWorkGroup));
		ActivityLogger.log(createdWorkGroup.getOwner(), Verb.CREATE, createdWorkGroup, Arrays.asList((Negotiator)membershipManager.getOwnerMembership(createdWorkGroup.getOwner().getProfileUUID())));
		return createdWorkGroup;
	}

	@Override
	public WorkGroup updateWorkGroup(WorkGroup iWorkGroup) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		checkWorkgroupModificationAuthorization(iWorkGroup);

		iWorkGroup.setTags(new HashSet<String>(ExCollections.toLowerCase(iWorkGroup.getTags())));
		iWorkGroup.setUpdated(TimeProvider.now());

		workGroupPersistentManager.update(iWorkGroup);
		fileManager.updateFileMetadata(iWorkGroup);

		return this.getWorkGroupByUUID(iWorkGroup.getUuid());
	}

	@Override
	public void deleteWorkGroup(String uuid) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroup workGroup = this.getWorkGroupByUUID(uuid);
		checkWorkgroupModificationAuthorization(workGroup);
		workGroupPersistentManager.delete(workGroup);
	}

	@Override
	public WorkGroup getWorkGroupByUUID(String iWorkGroupUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroup workGroup = workGroupPersistentManager.readObjectByUUID(WorkGroup.class, iWorkGroupUUID);
		if(workGroup == null)
			throw new ExNotFoundException(iWorkGroupUUID);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(!workGroup.isOwnerOrMember(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		return workGroup;
	}

	@Override
	public List<WorkGroup> listWorkGroups(QueryFilters iFilters) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		if(!iFilters.isNullOrEmpty(WorkGroupQFilters.OWNER_PROFILE) &&
				!iFilters.get(WorkGroupQFilters.OWNER_PROFILE).equals(ContextHelper.getMembership().getProfile().getUuid())){
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
		}

		return workGroupPersistentManager.listObjects(new WorkGroupQuery(), iFilters);
	}

	@Override
	public void addWorkGroupMember(String iWorkGroupUUID, String iMembershipUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroup workGroup = getWorkGroupByUUID(iWorkGroupUUID);

		if(workGroup == null)
			throw new ExNotFoundException(iWorkGroupUUID);

		checkWorkgroupModificationAuthorization(workGroup);


		Membership membershipToBeAdded = membershipManager.findByUUID(iMembershipUUID, true);

		if(membershipToBeAdded == null || !membershipToBeAdded.getProfile().equals(ContextHelper.getMembership().getProfile()))
			throw new ExNotFoundException(iWorkGroupUUID);

		if(workGroup.getOwner().equals(membershipToBeAdded) || workGroup.getMembers().contains(membershipToBeAdded))
			throw new ExParamException(ErrorKeys.PARAM_DUPLICATE, WorkGroupFields.MEMBERSHIP_UUID);

		workGroup.getMembers().add(membershipToBeAdded);
		workGroup.setUpdated(TimeProvider.now());
		workGroupPersistentManager.update(workGroup);

		notificationManager.process(new WorkGroupNotificationEvent(NotificationType.WORKGROUP_MEMBER_ADDED, workGroup, membershipToBeAdded));
		ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.ADD, membershipToBeAdded, workGroup, getMembersWithOwner(workGroup));
	}

	@Override
	public void removeWorkGroupMember(String iWorkGroupUUID, String membershipUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroup workGroup = this.getWorkGroupByUUID(iWorkGroupUUID);
		checkWorkgroupModificationAuthorization(workGroup);

		for(Membership membership : workGroup.getMembers()){
			if(membership.getUuid().equals(membershipUUID)){
				workGroup.getMembers().remove(membership);
				workGroupPersistentManager.update(workGroup);
				notificationManager.process(new WorkGroupNotificationEvent(NotificationType.WORKGROUP_MEMBER_REMOVED, workGroup, membership));
				ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.REMOVE, membership, workGroup, getMembersWithOwner(workGroup));
				break;
			}
		}
	}

	@Override
	public void addNegotiation(String iWorkGroupUUID, String iNegotiationUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroup workGroup = this.getWorkGroupByUUID(iWorkGroupUUID);
		Membership requestorMembership = (Membership)ContextHelper.getMembership();

		if(requestorMembership == null || workGroup == null || !workGroup.isOwnerOrMember(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		INegotiationManager negotiationManager = new NegotiationManager();
		Negotiation negotiation = negotiationManager.getNegotiation(iNegotiationUUID);

		if(workGroup.getNegotiations().contains(negotiation))
			throw new ExParamException(ErrorKeys.PARAM_DUPLICATE, WorkGroupFields.NEGOTIATION_UUID);

		if(negotiationManager.getInvolvedNegotiator(negotiation, requestorMembership) == null)
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		workGroup.getNegotiations().add(negotiation);
		workGroup.setUpdated(TimeProvider.now());
		workGroupPersistentManager.update(workGroup);

		notificationManager.process(new WorkGroupNotificationEvent(NotificationType.WORKGROUP_NEGOTIATION_ADDED, workGroup, negotiation));
		ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.ADD, negotiation, workGroup, getMembersWithOwner(workGroup));
	}

	@Override
	public void removeNegotiation(String iWorkGroupUUID, String iNegotiationUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroup workGroup = this.getWorkGroupByUUID(iWorkGroupUUID);
		Membership requestorMembership = (Membership)ContextHelper.getMembership();

		if(requestorMembership == null || workGroup == null || !workGroup.isOwnerOrMember(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		INegotiationManager negotiationManager = new NegotiationManager();
		Negotiation negotiation = negotiationManager.getNegotiation(iNegotiationUUID);

		if(negotiationManager.getInvolvedNegotiator(negotiation, requestorMembership) == null)
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		workGroup.getNegotiations().remove(negotiation);
		workGroupPersistentManager.update(workGroup);
		notificationManager.process(new WorkGroupNotificationEvent(NotificationType.WORKGROUP_NEGOTIATION_REMOVED, workGroup, negotiation));
		ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.REMOVE, negotiation, workGroup, getMembersWithOwner(workGroup));
	}

	@Override
	public List<INegotiationSummary> listNegotiations(String iWorkGroupUUID, QueryFilters iFilters) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroup workGroup = this.getWorkGroupByUUID(iWorkGroupUUID);
		NegotiationManager negotiationManager = new NegotiationManager();

		Set<Negotiation> negotiations = new HashSet<Negotiation>();

		for(Negotiation negotiation : workGroup.getNegotiations()){
			try{
				//Security.checkNegotiationAccess(negotiation, ContextHelper.getMembership());
				if(negotiation != null)
					negotiations.add(negotiation);
			}
			catch(ExAuthorizationException e){}
		}

		return negotiationManager.wrapNegotiations(PagedList.create(negotiations, 1, workGroup.getNegotiations().size(), workGroup.getNegotiations().size()),
																	ContextHelper.getMembership());
	}

	@Override
	public WorkGroupComment createComment(String iWorkGroupUUID, String iPostUUID, WorkGroupComment iComment) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		Post post = this.getPostByUUID(iWorkGroupUUID, iPostUUID);

		if(requestorMembership == null || !post.getWorkGroup().isOwnerOrMember(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		if(iComment == null)
			throw new ExParamException(ErrorKeys.PARAM_INVALID);

		iComment.setPost(post);
		iComment.setCreator(requestorMembership);
		iComment.setCreated(TimeProvider.now());
		iComment.setUpdated(iComment.getCreated());
		iComment.getPost().setUpdated(iComment.getCreated());
		iComment.getPost().getWorkGroup().setUpdated(iComment.getCreated());

		WorkGroupComment comment = workGroupPersistentManager.create(iComment);

		fileManager.updateFileMetadata(comment);

		notificationManager.process(new WorkGroupNotificationEvent(NotificationType.WORKGROUP_COMMENT_CREATED, comment));
		ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.COMMENT, comment, getMembersWithOwner(comment.getPost().getWorkGroup()));

		return comment;
	}

	@Override
	public void deleteComment(String iWorkGroupUUID, String iPostUUID, String iCommentUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroupComment comment = this.getCommentByUUID(iWorkGroupUUID, iPostUUID, iCommentUUID);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(requestorMembership == null || !comment.getPost().isCreatorOrWorkGroupOwner(requestorMembership) || !comment.isCreator(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		workGroupPersistentManager.delete(comment);
	}

	@Override
	public WorkGroupComment updateComment(String iWorkGroupUUID, String iPostUUID, WorkGroupComment iComment) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		if(iComment == null || !iComment.getPost().getUuid().equals(iPostUUID))
			throw new ExNotFoundException(iWorkGroupUUID);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(requestorMembership == null || !iComment.isCreator(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		iComment.setUpdated(TimeProvider.now());

		workGroupPersistentManager.update(iComment);

		fileManager.updateFileMetadata(iComment);
		return this.getCommentByUUID(iWorkGroupUUID, iPostUUID, iComment.getUuid());
	}

	@Override
	public List<WorkGroupComment> listComments(String iWorkGroupUUID, String iPostUUID, QueryFilters iFilters) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		Post post = this.getPostByUUID(iWorkGroupUUID, iPostUUID);
		Membership requestorMembership = (Membership)ContextHelper.getMembership();

		if(requestorMembership == null || !post.getWorkGroup().isOwnerOrMember(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		iFilters.put(WorkGroupCommentFields.POST, post.getId());

		return workGroupPersistentManager.listObjects(new WorkGroupCommentQuery(), iFilters);
	}

	@Override
	public WorkGroupComment getCommentByUUID(String iWorkGroupUUID, String iPostUUID, String iCommentUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroupComment comment = workGroupPersistentManager.readObjectByUUID(WorkGroupComment.class, iCommentUUID);
		if(comment == null || !comment.getPost().getWorkGroup().getUuid().equals(iWorkGroupUUID))
			throw new ExNotFoundException(iCommentUUID);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(!comment.getPost().getWorkGroup().isOwnerOrMember(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		return comment;
	}

	@Override
	public Post createPost(String iWorkGroupUUID, Post iPost) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		WorkGroup workGroup = this.getWorkGroupByUUID(iWorkGroupUUID);

		if(requestorMembership == null || !workGroup.isOwnerOrMember(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		if(iPost == null)
			throw new ExParamException(ErrorKeys.PARAM_INVALID);

		iPost.setWorkGroup(workGroup);
		iPost.setCreator(requestorMembership);
		iPost.setCreated(TimeProvider.now());
		iPost.setUpdated(iPost.getCreated());
		iPost.getWorkGroup().setUpdated(iPost.getCreated());

		Post post = workGroupPersistentManager.create(iPost);

		fileManager.updateFileMetadata(post);

		notificationManager.process(new WorkGroupNotificationEvent(NotificationType.WORKGROUP_POST_CREATED, post));
		ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.POST, post, getMembersWithOwner(post.getWorkGroup()));

		return post;
	}

	@Override
	public void deletePost(String iWorkGroupUUID, String iPostUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		Post post = this.getPostByUUID(iWorkGroupUUID, iPostUUID);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(requestorMembership == null || !post.isCreatorOrWorkGroupOwner(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		workGroupPersistentManager.delete(post);
	}

	@Override
	public Post updatePost(String iWorkGroupUUID, Post iPost) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		if(iPost == null || !iPost.getWorkGroup().getUuid().equals(iWorkGroupUUID))
			throw new ExNotFoundException(iWorkGroupUUID);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(requestorMembership == null || !iPost.isCreator(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		iPost.setUpdated(TimeProvider.now());

		workGroupPersistentManager.update(iPost);
		fileManager.updateFileMetadata(iPost);
		return this.getPostByUUID(iWorkGroupUUID, iPost.getUuid());
	}

	@Override
	public List<Post> listPosts(String iWorkGroupUUID, QueryFilters iFilters) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		WorkGroup workGroup = this.getWorkGroupByUUID(iWorkGroupUUID);
		Membership requestorMembership = (Membership)ContextHelper.getMembership();

		if(requestorMembership == null || !workGroup.isOwnerOrMember(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		iFilters.put(PostFields.WORKGROUP, workGroup.getId());

		return workGroupPersistentManager.listObjects(new PostQuery(), iFilters);
	}

	@Override
	public Post getPostByUUID(String iWorkGroupUUID, String iPostUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		Post post = workGroupPersistentManager.readObjectByUUID(Post.class, iPostUUID);
		if(post == null || !post.getWorkGroup().getUuid().equals(iWorkGroupUUID))
			throw new ExNotFoundException(iWorkGroupUUID);

		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(!post.getWorkGroup().isOwnerOrMember(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		return post;
	}

	private void checkWorkgroupModificationAuthorization(WorkGroup iWorkGroup){
		Membership requestorMembership = (Membership)ContextHelper.getMembership();
		if(requestorMembership == null || !iWorkGroup.isOwner(requestorMembership))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
	}

	@Override
	public List<SearchResultSummary> listSearchResultSummary() {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		List<SearchResultSummary> searchResultSummaries = new ArrayList<SearchResultSummary>();
		QueryFilters filters = QueryFilters.create(QueryParameters.FIELD, WorkGroupQFilters.TAGS);
		searchResultSummaries.add(workGroupPersistentManager.getSearchResultSummary(new WorkGroupSearchSummaryQuery(), filters));
		return searchResultSummaries;
	}

	@Override
	public List<Map<String, Object>> listFiles(String iWorkGroupUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		QueryFilters filters = new QueryFilters();
		filters.put(FileMetadata.WORKGROUP_UUID, iWorkGroupUUID);

		IFileManager fileManager = new FileManager();
		return fileManager.getFilesMetadata(filters);
	}

	@Override
	public List<Map<String, Object>> listFiles(String iWorkGroupUUID, String iPostUUID) {
		//checking profile permission
		hasAccessPermission(Security.ProfilePermissions.WORKGROUPS);

		QueryFilters filters = new QueryFilters();
		filters.put(FileMetadata.WORKGROUP_UUID, iWorkGroupUUID);
		filters.put(FileMetadata.POST_UUID, iPostUUID);

		IFileManager fileManager = new FileManager();
		return fileManager.getFilesMetadata(filters);
	}

	private List<Negotiator> getMembersWithOwner(WorkGroup workGroup){
		List<Negotiator> members = new ArrayList<>();
		members.add(workGroup.getOwner());
		members.addAll(workGroup.getMembers());
		return members;
	}
}

package com.exrade.models.workgroup;

import com.exrade.models.common.Comment;
import com.exrade.models.userprofile.Membership;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkGroupComment extends Comment {

	private Post post;
	
	private Date updated;

	private List<String> files = new ArrayList<>();
	
	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}
	
	public Boolean isCreator(Membership iMembership) {
		if(getCreator().equals(iMembership))
			return true;
	
		return false;
	}
}

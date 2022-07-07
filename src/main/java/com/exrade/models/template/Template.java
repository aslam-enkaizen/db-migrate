package com.exrade.models.template;

import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.BaseEntityUUID;

import java.util.Date;

/***
 * Class to represent a template e.g., Negotiation terms and conditions template, Agreement pdf template etc.
 * 
 * @author Md Mahfuzul Islam
 *
 */
public class Template extends BaseEntityUUID {

	private Date created;
	
	private Date updated;
	
	private String name;

	private String content;
	
	private String header;
	
	private String footer;
	
	private TemplateType templateType;
	
	private Membership ownerMembership; // author of the template

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Membership getOwnerMembership() {
		return ownerMembership;
	}

	public void setOwnerMembership(Membership ownerMembership) {
		this.ownerMembership = ownerMembership;
	}

	public TemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}
}

package com.exrade.models.userprofile.security;


public class InformationModelTemplateRole extends ExRole {

	public final static String MANAGER = "templates.team.manager";
	public final static String EDITOR = "templates.team.editor";
	public final static String COMMENTER = "templates.team.commenter";
	public final static String VIEWER = "templates.team.viewer";

	public InformationModelTemplateRole(){}

	public InformationModelTemplateRole(String iName) {
		super(iName);
	}

}

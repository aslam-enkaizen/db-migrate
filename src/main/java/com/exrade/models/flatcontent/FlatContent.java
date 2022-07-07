package com.exrade.models.flatcontent;

import com.exrade.models.i18n.Localizable;
import com.exrade.platform.persistence.IPersistenceUUID;
import com.exrade.util.ObjectsUtil;

import javax.persistence.Id;
import javax.persistence.Version;
import java.util.ResourceBundle;

public class FlatContent implements IPersistenceUUID,Localizable {

	@Id
	private String id;

	@Version
	private Integer version;

	private String uuid = ObjectsUtil.generateUniqueID();

	private String url;

	private String name;

	private String content;

	public FlatContent(){}

	public static FlatContent createFlatContent(String iUrl,String iName,String iContent){
		FlatContent flatContent = new FlatContent(); 
		flatContent.url = iUrl;
		flatContent.name = iName;
		flatContent.content = iContent;
		return flatContent;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Integer getVersion() {
		return version;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void localize(ResourceBundle resourceBundle) {
		this.setName(resourceBundle.getString(LocalizedFields.NAME));
		this.setContent(resourceBundle.getString(LocalizedFields.CONTENT));
	}

	public static final class LocalizedFields{
		public static final String NAME = "name"; 
		public static final String CONTENT = "content"; 
	}

}

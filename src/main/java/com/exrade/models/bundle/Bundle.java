package com.exrade.models.bundle;

import com.exrade.models.i18n.ExLang;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.informationmodel.Tag;
import com.exrade.models.processmodel.ModelPrivacyLevel;
import com.exrade.models.processmodel.ProcessModel;
import com.exrade.platform.persistence.IPersistence;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ObjectsUtil;

import javax.persistence.Id;
import javax.persistence.Version;
import java.util.*;

/**
 * A bundle is simple container for a process and Information model, with a unique
 * description and 'Terms and Conditions'
 * 
 * @author jasonfinnegan
 * 
 */
public class Bundle implements IPersistence {

	@Id
	private String id;

	@Version
	private Integer version;
	
	private Date publicationDate;

	private String name;
	
	private String uuid = ObjectsUtil.generateUniqueID();

	private ProcessModel processModel;

	private InformationModelTemplate informationModelTemplate;

	//private String description;
	
	//private String policies;
	
	private int userPlanLevel;  //user plan must be greater than or equal to this value, to use this bundle
	
	private Integer modelVersion = 0; // version of the bundle
	
	private ModelPrivacyLevel privacyLevel = ModelPrivacyLevel.PUBLIC;

	private String language = ExLang.ENGLISH.getCode();
		
	private Map<String,String> titleTranslations = new HashMap<String, String>();

	private Map<String,String> descriptionTranslations = new HashMap<String, String>();
	
	private Map<String,String> policiesTranslations = new HashMap<String, String>();
	
	public Bundle() {
	}

	public static Bundle createBundle(String iName, ProcessModel processModel,
			InformationModelTemplate iInformationModelTemplate,String iDescription,
			String iPolicies, int iPrivacyLevel, int bundleVersion) {
		Bundle bundle = new Bundle();
		bundle.setName(iName);
		bundle.setTitle(iName);
		
		bundle.processModel  =  processModel;
		bundle.informationModelTemplate = iInformationModelTemplate;
		bundle.setDescription(iDescription);
		bundle.setPolicies(iPolicies);
		bundle.setUserPlanLevel(iPrivacyLevel);
		bundle.setPrivacyLevel(ModelPrivacyLevel.fromLevel(iPrivacyLevel));
		bundle.setModelVersion(bundleVersion);
		bundle.setPublicationDate(TimeProvider.now());
		return bundle;
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Integer getVersion() {
		return version;
	}

	public List<Tag> getTags() {
		List<Tag> bundletags=new ArrayList<Tag>();
		bundletags.addAll(getProcessModel().getTags());
	//	bundletags.addAll(new ProcessModelManager().readByName(getProcessModel()).getTags());
		bundletags.addAll(getInformationModelTemplate().getTags());
		return bundletags;
	}

	public String getDescription() {
		//return description;
		return getDescriptionTranslations().get(getLanguage());
	}

	public void setDescription(String description) {
		//this.description = description;
		getDescriptionTranslations().put(getLanguage(), description);
	}

	public String getTitle() {
		return getTitleTranslations().get(getLanguage());
	}

	public void setTitle(String title) {
		getTitleTranslations().put(getLanguage(), title);
	}

	public InformationModelTemplate getInformationModelTemplate() {
		return informationModelTemplate;
	}

	public void setInformationModelTemplate(InformationModelTemplate iInformationModelTemplate) {
		informationModelTemplate = iInformationModelTemplate;
	}


	public ProcessModel getProcessModel() {
		return processModel;
	}

	public void setProcessModel(ProcessModel processModel) {
		this.processModel = processModel;
	}
	
	public String getInformationModelTemplateName(){

		String informationModelTemplateName = null;
		
		if (getInformationModelTemplate() != null){
			informationModelTemplateName = getInformationModelTemplate().getName();
		}
		
		return informationModelTemplateName;
	}


	public String getUuid() {
		return uuid;
	}

	public String getPolicies() {
		//return policies;
		return getPoliciesTranslations().get(getLanguage());
	}

	public void setPolicies(String policies) {
		//this.policies = policies;
		getPoliciesTranslations().put(getLanguage(), policies);
	}

	public int getUserPlanLevel() {
		return userPlanLevel;
	}

	public void setUserPlanLevel(int userPlanLevel) {
		this.userPlanLevel = userPlanLevel;
	}

	public Integer getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(Integer modelVersion) {
		this.modelVersion = modelVersion;
	}

	public Map<String,String> getDescriptionTranslations() {
		return descriptionTranslations;
	}

	public void setDescriptionTranslations(Map<String,String> descriptionTranslations) {
		this.descriptionTranslations = descriptionTranslations;
	}

	public Map<String,String> getPoliciesTranslations() {
		return policiesTranslations;
	}

	public void setPoliciesTranslations(Map<String,String> policiesTranslations) {
		this.policiesTranslations = policiesTranslations;
	}

	public Map<String,String> getTitleTranslations() {
		return titleTranslations;
	}

	public void setTitleTranslations(Map<String,String> titleTranslations) {
		this.titleTranslations = titleTranslations;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ModelPrivacyLevel getPrivacyLevel() {
		return privacyLevel;
	}

	public void setPrivacyLevel(ModelPrivacyLevel privacyLevel) {
		this.privacyLevel = privacyLevel;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

}

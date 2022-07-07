package com.exrade.runtime.informationmodel;

import com.exrade.models.activity.ObjectType;
import com.exrade.models.activity.Verb;
import com.exrade.models.common.Meta.Operation;
import com.exrade.models.common.Meta.Widget;
import com.exrade.models.informationmodel.*;
import com.exrade.models.negotiation.PublishStatus;
import com.exrade.models.processmodel.IProcessModel;
import com.exrade.models.processmodel.ProcessAttribute;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.ModelVersionQueryUtil;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.common.SyncOperationControllerFactory;
import com.exrade.runtime.filemanagement.FileManager;
import com.exrade.runtime.informationmodel.persistence.InformationTemplatePersistentManager;
import com.exrade.runtime.informationmodel.persistence.InformationTemplatePersistentManager.InformationTemplateQueryFilters;
import com.exrade.runtime.informationmodel.persistence.query.InformationTemplateSearchSummaryQuery;
import com.exrade.runtime.processmodel.IProcessModelManager;
import com.exrade.runtime.processmodel.ProcessModelManager;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * IInformationModelManager implementation integrating database persistence. This class
 * manages only GLOBAL_TEMPLATE
 *
 * @author john
 *
 */

public class InformationModelManager implements IInformationModelManager {

	private final InformationTemplatePersistentManager informationTemplatePM;

	public InformationModelManager() {
		this(new InformationTemplatePersistentManager());
	}

	public InformationModelManager(InformationTemplatePersistentManager informationTemplatePersistentManager) {
		this.informationTemplatePM = informationTemplatePersistentManager;
	}


	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#create(com.exrade.models.informationmodel.InformationModelTemplate)
	 */
	@Override
	public InformationModelTemplate create(InformationModelTemplate infoModelTemplate) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		// TODO: validate information model syntax (variable mandatory fields, formula, custom fields etc.)
		if(!Strings.isNullOrEmpty(infoModelTemplate.getTemplate())) {
			infoModelTemplate.setItems(InformationModelUtil.getFieldsFromTemplate(infoModelTemplate.getTemplate(), infoModelTemplate.getLanguage()));
		}

		infoModelTemplate.setCreated(TimeProvider.now());
		if(Strings.isNullOrEmpty(infoModelTemplate.getTitle()))
			infoModelTemplate.setTitle(infoModelTemplate.getName());
		
		if(infoModelTemplate.getPublishStatus() == PublishStatus.ACTIVE){
			InformationModelUtil.cleanTemplate(infoModelTemplate, false, false, true);
		}
		
		infoModelTemplate = informationTemplatePM.create(infoModelTemplate);
		return infoModelTemplate;
	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#readByName(java.lang.String)
	 */
	@Override
	public InformationModelTemplate readByName(String infoModelName) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		if(!Strings.isNullOrEmpty(infoModelName)){
			QueryFilters filters = new QueryFilters();
			filters.put(InformationTemplateQueryFilters.NAME, infoModelName);
			ModelVersionQueryUtil.addModelVersionFilter(filters);

			InformationModelTemplate template = informationTemplatePM.read(filters);
			return template;
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#update(com.exrade.models.informationmodel.InformationModelTemplate)
	 */
	@Override
	public InformationModelTemplate update(String infoModelUUID, InformationModelTemplate informationModelTemplate) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		// TODO: validate information model syntax (variable mandatory fields, formula, custom fields etc.)
		InformationModelTemplate existingTemplate = readByUUID(infoModelUUID);

		if(existingTemplate.getPublishStatus() == PublishStatus.ACTIVE && existingTemplate.isArchived() == informationModelTemplate.isArchived()) {// if active then only can be archived or unarchived
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
		}

		InformationModelSyncOperationController controller = (InformationModelSyncOperationController) SyncOperationControllerFactory
				.getInstance().createSyncOperationController(existingTemplate.getUuid(), ObjectType.INFORMATION_MODEL_TEMPLATE);
		existingTemplate = controller.updateInformationModelTemplate(infoModelUUID, informationModelTemplate);

		if(existingTemplate.getPublishStatus() == PublishStatus.ACTIVE) {
			if(existingTemplate.isArchived())
				ActivityLogger.log(ContextHelper.getMembership(), Verb.ARCHIVE, existingTemplate,
						Arrays.asList());
			else if(!existingTemplate.isArchived())
				ActivityLogger.log(ContextHelper.getMembership(), Verb.UNARCHIVE, existingTemplate,
						Arrays.asList());
		}

		return this.readByUUID(existingTemplate.getUuid());
	}


	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#delete(java.lang.String)
	 */
	@Override
	public void delete(String infoModelUUID) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		InformationModelTemplate informationModelTemplate = readByUUID(infoModelUUID);
		if(informationModelTemplate.getPublishStatus() == PublishStatus.ACTIVE)
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
		informationTemplatePM.delete(infoModelUUID);
	}


	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#listModelsByCategory(java.lang.String)
	 */
	@Override
	public List<InformationModelTemplate> listModelsByCategory(String categoryPath) {
		throw new UnsupportedOperationException("Not yet implemented");
	}


	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#listCategories(java.lang.String)
	 */
	@Override
	public List<InformationModelCategory> listCategories(String categoryPath) {
		throw new UnsupportedOperationException("Not yet implemented");
	}



	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#listModels(com.exrade.platform.persistence.query.QueryFilters)
	 */
	@Override
	public List<InformationModelTemplate> listModels(QueryFilters filters) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		//ModelVersionQueryUtil.addModelVersionFilter(filters);
		List<InformationModelTemplate> informationModelTemplates = informationTemplatePM.listModels(filters);
		return informationModelTemplates;
	}



	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#createInformationDocument(com.exrade.models.informationmodel.InformationModelTemplate)
	 */
	@Override
	public InformationModelDocument createInformationDocument(
			InformationModelTemplate iInformationModelTemplate) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);


		InformationModelDocument informationModelDocument = new InformationModelDocument(iInformationModelTemplate,ContextHelper.getLanguage());
		return informationModelDocument;
	}


	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#createInformationDocument(java.lang.String)
	 */
	@Override
	public InformationModelDocument createInformationDocument(
			String iInformationTemplateName) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		InformationModelTemplate informationModelTemplate = readByName(iInformationTemplateName);
		return createInformationDocument(informationModelTemplate);
	}

	/**
	 * Clone the InformationModelDocument given as input with file attachments duplication
	 * @param iInformationModelDocument
	 */
	@Override
	public InformationModelDocument copyInformationDocument(InformationModelDocument iInformationModelDocument) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		InformationModelDocument informationModelDocumentCopy = new InformationModelDocument();

		informationModelDocumentCopy.setInformationTemplateUUID(iInformationModelDocument.getInformationTemplateUUID());
		informationModelDocumentCopy.setCategory(iInformationModelDocument.getCategory());
		informationModelDocumentCopy.setLanguage(iInformationModelDocument.getLanguage());
		informationModelDocumentCopy.setTitle(iInformationModelDocument.getTitle());
		informationModelDocumentCopy.getTitleTranslations().putAll(iInformationModelDocument.getTitleTranslations());
		informationModelDocumentCopy.getSupportedLanguages().addAll(iInformationModelDocument.getSupportedLanguages());
		informationModelDocumentCopy.setItems(deepCloneItems(iInformationModelDocument.getItems()));
		informationModelDocumentCopy.setTemplate(cleanTemplate(iInformationModelDocument.getTemplate()));
		informationModelDocumentCopy.setModelData(iInformationModelDocument.getModelData());

		InformationModelDocument.addOperations(informationModelDocumentCopy.getItems(),Sets.newHashSet(Operation.SET,Operation.SET_REQUIRED,Operation.SET_NEGOTIABLE,Operation.SET_REQUIRED_BY_PROCESS));
		return informationModelDocumentCopy;

	}

	private String cleanTemplate(String template){
		if(!Strings.isNullOrEmpty(template)){
			Document doc = Jsoup.parse(template);
			Elements signatureContainers = doc.select(".signature-container");
			for(Element signatureContainer : signatureContainers){
				if(signatureContainer.hasAttr("data-signer") && "PARTICIPANT".equals(signatureContainer.dataset().get("signer"))){
					Element signer = signatureContainer.select(".signer").first();
					signer.removeAttr("data-signerid");
					signer.html("Someone from the other company");
				}
			}

			return doc.outerHtml();
		}
		return template;
	}

	private List<Attribute> deepCloneItems(List<Attribute> items){
		List<Attribute> attributes = Attribute.newInstance(items);
		for (Attribute attribute : attributes) {
			duplicateAttachments(attribute);
		}
		return attributes;
	}

	/**
	 * Deep clone of process attributes with file attachments duplication, only configurable values are copied from the currentAttributes list
	 * @param items
	 * @return List<ProcessAttribute>
	 */
	public List<ProcessAttribute> deepCloneProcessAttributes(List<ProcessAttribute> defaultAttributes,List<ProcessAttribute> currentAttributes){

		for (int i = 0; i < defaultAttributes.size(); i++) {
			ProcessAttribute defaultProcessAttribute = defaultAttributes.get(i);
			ProcessAttribute negotiationProcessAttribute = AbstractAttribute.getAttributeByName(currentAttributes,defaultProcessAttribute.getName());
			duplicateProcessAttributes(defaultProcessAttribute,negotiationProcessAttribute);
		}

		return defaultAttributes;
	}


	private static ProcessAttribute duplicateProcessAttributes(ProcessAttribute iDefaultProcessAttribute,ProcessAttribute iCurrentProcessAttribute){

		if (!iDefaultProcessAttribute.getProperties().isEmpty()) {
			for (int i = 0; i < iDefaultProcessAttribute.getProperties().size(); i++) {
				ProcessAttribute defaultProcessAttribute = iDefaultProcessAttribute.getProperties().get(i);
				ProcessAttribute negotiationProcessAttribute = iCurrentProcessAttribute.getProperties().get(i);
				duplicateProcessAttributes(defaultProcessAttribute,negotiationProcessAttribute);
			}
		}
		else if (iDefaultProcessAttribute.isConfigurable()){
			iDefaultProcessAttribute.getMeta().addOperation(Operation.SET);
			if (Widget.ATTACHMENT.equals(iDefaultProcessAttribute.getMeta().getWidget()) || Widget.IMAGE.equals(iDefaultProcessAttribute.getMeta().getWidget()) ){
				FileManager fileManager = new FileManager();
				String filenameCopy = fileManager.copyFile(iCurrentProcessAttribute.getValue());
				iDefaultProcessAttribute.setValue(filenameCopy);
			}
			else {
				iDefaultProcessAttribute.setValue(iCurrentProcessAttribute.getValue());
			}
		}
		return iDefaultProcessAttribute;
	}

	private static AbstractAttribute duplicateAttachments(AbstractAttribute iAttribute){

		if (!iAttribute.getProperties().isEmpty()) {
			for (AbstractAttribute attribute : iAttribute.getProperties()) {
				duplicateAttachments(attribute);
			}
		}
		else if (Widget.ATTACHMENT.equals(iAttribute.getMeta().getWidget()) || Widget.IMAGE.equals(iAttribute.getMeta().getWidget()) ){
			FileManager fileManager = new FileManager();
			String filenameCopy = fileManager.copyFile(iAttribute.getValue());
			iAttribute.setValue(filenameCopy);
		}
		return iAttribute;
	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.informationmodel.InfoModelAPI#readByUUID(java.lang.String)
	 */
	@Override
	public InformationModelTemplate readByUUID(String informationTemplateUUID) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		if(!Strings.isNullOrEmpty(informationTemplateUUID)){
			InformationModelTemplate template = informationTemplatePM.readObjectByUUID(InformationModelTemplate.class, informationTemplateUUID);
			if(template != null) {
				Security.checkInformationModelTemplateAccess(template);
				return template;
			}
		}
		return null;
	}

	@Override
	public InformationModelDocument createInformationDocument(String iInformationTemplateUUID, String iProcessModelName) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		InformationModelDocument informationModelDocument = null;
		if(Strings.isNullOrEmpty(iProcessModelName)){
			informationModelDocument = new InformationModelDocument(readByUUID(iInformationTemplateUUID),ContextHelper.getLanguage());
		}
		else{
			IProcessModelManager processModelManager = new ProcessModelManager();
			IProcessModel processModel = processModelManager.readByName(iProcessModelName);
			if(processModel != null)
				informationModelDocument = new InformationModelDocument(readByUUID(iInformationTemplateUUID), processModel.getRequiredAttributes(),ContextHelper.getLanguage());
		}

		return informationModelDocument;
	}

	private void updateTags(InformationModelTemplate informationModelTemplate, List<Tag> tags) {
		Iterator<Tag> iter = informationModelTemplate.getTags().iterator();

		while(iter.hasNext()){
			Tag tag = iter.next();
			if(!tags.contains(tag)){
				informationTemplatePM.delete(tag);
				iter.remove();
			}
		}

		for(Tag tag : tags) {
			if(!informationModelTemplate.getTags().contains(tag)) {
				informationModelTemplate.getTags().add(tag);
			}
		}
	}

	@Override
	public List<SearchResultSummary> listSearchResultSummary(QueryFilters iFilters) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES);

		List<SearchResultSummary> searchResultSummaries = new ArrayList<SearchResultSummary>();
		searchResultSummaries.add(informationTemplatePM.getSearchResultSummary(new InformationTemplateSearchSummaryQuery(), iFilters));
		return searchResultSummaries;
	}
}

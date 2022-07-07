package com.exrade.runtime.informationmodel;

import com.exrade.core.ExLogger;
import com.exrade.models.informationmodel.Attribute;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.informationmodel.Tag;
import com.exrade.models.negotiation.PublishStatus;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.runtime.common.ISyncOperationController;
import com.exrade.runtime.informationmodel.persistence.InformationTemplatePersistentManager;
import com.exrade.runtime.timer.TimeProvider;
import com.google.common.base.Strings;

import java.util.Iterator;
import java.util.List;

public class InformationModelSyncOperationController implements ISyncOperationController {

	public InformationModelTemplate updateInformationModelTemplate(String infoModelUUID, InformationModelTemplate informationModelTemplate) {
		InformationTemplatePersistentManager informationTemplatePM = new InformationTemplatePersistentManager();

		synchronized (this) {
			ExLogger.get().info("Updating InformationModelTemplate: " + infoModelUUID);

			InformationModelTemplate existingTemplate = informationTemplatePM.readObjectByUUID(InformationModelTemplate.class, infoModelUUID);

			if(existingTemplate.getPublishStatus() != PublishStatus.ACTIVE) {
				existingTemplate.setLanguage(informationModelTemplate.getLanguage());
				existingTemplate.setTitle(informationModelTemplate.getTitle());
				existingTemplate.setDescription(informationModelTemplate.getDescription());
				existingTemplate.setCategory(informationModelTemplate.getCategory());
				existingTemplate.setCustomFields(informationModelTemplate.getCustomFields());


				if(!Strings.isNullOrEmpty(informationModelTemplate.getTemplate())) {
					Attribute.updateIssues(InformationModelUtil.getFieldsFromTemplate(informationModelTemplate.getTemplate(), informationModelTemplate.getLanguage()),
							existingTemplate.getItems());
				}
				else {
					Attribute.updateIssues(informationModelTemplate.getItems(), existingTemplate.getItems());
				}
				existingTemplate.setTemplate(informationModelTemplate.getTemplate());

				List<Tag> tags = informationModelTemplate.getTags();
				Iterator<Tag> iter = existingTemplate.getTags().iterator();

				while(iter.hasNext()){
					Tag tag = iter.next();
					if(!tags.contains(tag)){
						iter.remove();
					}
				}

				for(Tag tag : tags) {
					if(!existingTemplate.getTags().contains(tag) && !Strings.isNullOrEmpty(tag.getValue())) {
						existingTemplate.getTags().add(PersistentManager.newDbInstance(Tag.class, tag.getValue()));
					}
				}

				if(existingTemplate.getPublishStatus() != PublishStatus.ACTIVE && informationModelTemplate.getPublishStatus() == PublishStatus.ACTIVE) {
					existingTemplate.setModelVersion(existingTemplate.getModelVersion() + 1);
					existingTemplate.setPublicationDate(informationModelTemplate.getPublicationDate());
					InformationModelUtil.cleanTemplate(existingTemplate, false, false, true);
				}
				existingTemplate.setPublishStatus(informationModelTemplate.getPublishStatus());
			}

			existingTemplate.setArchived(informationModelTemplate.isArchived());
			existingTemplate.setUpdated(TimeProvider.now());
			existingTemplate = informationTemplatePM.update(existingTemplate);

			ExLogger.get().info("Updated InformationModelTemplate: " + infoModelUUID);

			return existingTemplate;
		}
	}
}

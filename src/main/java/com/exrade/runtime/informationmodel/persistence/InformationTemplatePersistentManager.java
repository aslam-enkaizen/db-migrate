package com.exrade.runtime.informationmodel.persistence;

import com.exrade.models.informationmodel.InformationModelCategory;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExPersistentException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.informationmodel.persistence.query.InformationTemplateQuery;
import com.google.common.base.Strings;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IInformationModelManager implementation integrating database persistence. This class
 * manages only GLOBAL_TEMPLATE
 *
 * @author john
 *
 */

public class InformationTemplatePersistentManager extends PersistentManager {

	public InformationModelTemplate read(QueryFilters filters) {
		return readObject(new InformationTemplateQuery(),filters);
	}

	public void delete(String iInformationModelTemplateUUID) {
		QueryFilters filters = QueryFilters.create(QueryParameters.UUID,iInformationModelTemplateUUID);
		InformationModelTemplate informationModelTemplate = read(filters);
		delete(informationModelTemplate);
	}

	public List<InformationModelTemplate> listModels(QueryFilters filters) {
		List<InformationModelTemplate> informationModelTemplates = listObjects(new InformationTemplateQuery(),filters);
		return informationModelTemplates;
	}

	/**
	 * Return proxied instance of a InformationTemplate
	 *
	 * @param informationTemplateID
	 * @return InformationModelTemplate
	 */
	/*public InformationModelTemplate read(String informationTemplateID) {
		InformationModelTemplate informationTemplate = null;
		OObjectDatabaseTx db = connectionManager.getObjectConnection();
		try {
			ORecordId orid = new ORecordId(informationTemplateID);
			informationTemplate = db.load(orid);

		} catch (Exception ex) {
			throw new ExPersistentException(ExExceptionMessageKeys.INFOMODEL_TEMPLATE_CANNOT_LOAD, ex);
		} finally {
			db.close();
		}
		return informationTemplate;
	}*/

	/**
	 * Return proxied instance of a InformationModelTemplate using
	 * InformationTemplate UUID
	 *
	 * @param informationTemplateUUID
	 * @return InformationModelTemplate
	 */
	/*public InformationModelTemplate readByUUID(String informationTemplateUUID) {
		InformationModelTemplate informationTemplate = null;
		OObjectDatabaseTx db = connectionManager.getObjectConnection();
		String nquery = "select * from "
				+ InformationModelTemplate.class.getSimpleName()
				+ " where uuid like '" + informationTemplateUUID + "'";
		try {
			List<InformationModelTemplate> result = db
					.query(new OSQLSynchQuery<InformationModelTemplate>(nquery));
			if (result.size() == 1) {
				informationTemplate = result.get(0);
			} else if (result.size() > 1) {
				throw new ExPersistentException(ExExceptionMessageKeys.UUID_DUPLICATE);
			}
		} catch (Exception ex) {
			throw new ExPersistentException(ExExceptionMessageKeys.INFOMODEL_TEMPLATE_CANNOT_LOAD,	ex);
		} finally {
			db.close();
		}
		return informationTemplate;
	}

	public InformationModelTemplate readByName(String infoModelName) {
		InformationModelTemplate informationTemplate = null;
		OObjectDatabaseTx db = connectionManager.getObjectConnection();
		try {
			List<InformationModelTemplate> models = db
					.query(new OSQLSynchQuery<InformationModelTemplate>(
							"select from "
									+ InformationModelTemplate.class
											.getSimpleName()
									+ " where name = '" + infoModelName + "'"));

			if (models != null && models.size() > 0) {
				informationTemplate = models.get(0);
			}
		} catch (Exception ex) {
			throw new ExPersistentException(ExExceptionMessageKeys.INFOMODEL_TEMPLATE_CANNOT_LOAD, ex);
		} finally {
			db.close();
		}
		return informationTemplate;
	}

	public void delete(String infoModelName) {
		InformationModelTemplate infoModel = readByName(infoModelName);
		delete(infoModel);
	}

	public List<InformationModelTemplate> listModels(QueryFilters filters) {

		OObjectDatabaseTx db = connectionManager.getObjectConnection();
		List<InformationModelTemplate> informationModelTemplates = Collections.emptyList();


		String query = "select from " + InformationModelTemplate.class.getSimpleName()+ " where 1 = 1 ";

		if (filters.containsKey(InformationTemplateQueryFilters.TAGS) && filters.get(InformationTemplateQueryFilters.TAGS) != null){
			query += InformationTemplateQueryFilters.TAGS+" contains (value in ['" + filters.get(InformationTemplateQueryFilters.TAGS) + "']";
		}

		try {
			informationModelTemplates = db
					.query(new OSQLSynchQuery<InformationModelTemplate>(query));

		} catch (Exception ex) {
			throw new ExPersistentException(ExExceptionMessageKeys.INFOMODEL_TEMPLATE_CANNOT_LOAD, ex);
		} finally {
			db.close();
		}
		return informationModelTemplates;
	}*/

	// TODO Handle with an external Taxonomy
	public List<InformationModelTemplate> listModelsByCategory(
			String categoryPath) {
		List<InformationModelTemplate> models = new ArrayList<InformationModelTemplate>();
		InformationModelCategory category = InformationModelCategory
				.getCategory(categoryPath);
		if (category != null) {
			for (InformationModelTemplate model : category
					.getInformationModelTemplates()) {
				models.add(model);
			}
		}
		return models;
	}

	// TODO Handle with an external Taxonomy
	public List<InformationModelCategory> listCategories(String categoryPath) {
		if (categoryPath != null && !categoryPath.isEmpty()) {
			InformationModelCategory category = InformationModelCategory
					.getCategory(categoryPath);
			return category.getSubCategories();
		} else {
			OObjectDatabaseTx db = connectionManager.getObjectConnection();
			return db.query(new OSQLSynchQuery<InformationModelCategory>(
					"select from "
							+ InformationModelCategory.class.getSimpleName()
							+ " where parent is null"));
		}
	}

	public SearchResultSummary getSearchResultSummary(OrientSqlBuilder iQueryBuilder, QueryFilters iQueryFilters){

		SearchResultSummary searchResultSummary = null;
		try {
			List<ODocument> result = listObjects(iQueryBuilder, iQueryFilters);
			Map<String, Long> resultMap = new HashMap<String, Long>();

			if (result != null && result.size() > 0){
				for(ODocument document : result){
					String value = (String)document.field("value");
					if(!Strings.isNullOrEmpty(value))
						resultMap.put(value, (long)document.field("count"));
				}
			}

			searchResultSummary = new SearchResultSummary();
			searchResultSummary.setName((String)iQueryFilters.get(QueryParameters.FIELD));
			searchResultSummary.setValues(resultMap);
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.DB_READ_GENERIC, ex);
		}

		return searchResultSummary;
	}

	public static class InformationTemplateQueryFilters{
		// comma separated tag list
		public final static String TAGS = "tags";
		public final static String CATEGORY = "category";
		public final static String NAME = "name";
		public final static String PRIVACY_LEVEL = "privacyLevel";
		public final static String PUBLISH_STATUS = "publishStatus";
		public final static String AUTHOR = "author";
		public final static String PROFILE = "profile";
		//public final static String MODEL_VERSION = "modelVersion";
		//public final static String MAX = "max";

	}

}

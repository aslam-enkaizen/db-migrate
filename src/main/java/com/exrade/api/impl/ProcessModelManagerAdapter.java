package com.exrade.api.impl;

import com.exrade.api.ProcessModelAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.informationmodel.InformationModelDocument;
import com.exrade.models.processmodel.ProcessAttribute;
import com.exrade.models.processmodel.ProcessModel;
import com.exrade.models.processmodel.ProcessModelSummary;
import com.exrade.models.processmodel.protocol.events.TimeEvent;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.processmodel.IProcessModelManager;
import com.exrade.runtime.processmodel.ProcessModelManager;
import com.exrade.runtime.processmodel.persistence.ProcessModelPersistentManager.ProcessModelQFilters;
import com.exrade.runtime.rest.RestParameters.ProcessModelFields;
import com.exrade.runtime.rest.RestParameters.ProcessModelFilters;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.exrade.util.ObjectsUtil;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcessModelManagerAdapter implements ProcessModelAPI {

	private IProcessModelManager manager = new ProcessModelManager();
	
	@Override
	public List<ProcessModelSummary> getProcessList(ExRequestEnvelope request,Map<String, String> filters) {
		ContextHelper.initContext(request);
		QueryFilters qFilters = getProcessFilters(filters);

		List<ProcessModel> processmodels = manager.getPlainProcessList(qFilters);

		List<ProcessModelSummary> filteredprocesses = new ArrayList<>();

		String languageCode = request.getLanguage();
		for (ProcessModel process : processmodels) {
			if(!Strings.isNullOrEmpty(languageCode) && process!=null){				
				process.setLanguage(languageCode);
			}
			filteredprocesses.add(ObjectsUtil.getProxy(ProcessModelSummary.class, process));
		}
		return filteredprocesses;
	}

	private QueryFilters getProcessFilters(Map<String, String> filterParams) {
		QueryFilters filters = QueryFilters.create(filterParams);
		
		String tags = filterParams.get(ProcessModelFields.TAGS);
		if(!Strings.isNullOrEmpty(tags)){
			filters.put(ProcessModelFields.TAGS,ExCollections.commaSeparatedToList(tags.toLowerCase()));
		}
		filters.putIfNotNull(ProcessModelFilters.KEYWORDS,filterParams.get(ProcessModelFilters.KEYWORDS));
		filters.putIfNotNull(ProcessModelQFilters.NAME,filterParams.get(ProcessModelFilters.PROCESSMODEL_NAME));
		filters.putIfNotNull(QueryParameters.SORT,filterParams.get(QueryParameters.SORT));
		filters.putIfNotNull(ProcessModelFields.PRIVACY_LEVEL,filterParams.get(ProcessModelFields.PRIVACY_LEVEL));
		return filters;
	}
	
	@Override
	public List<TimeEvent> getOwnerTimeEvents(ExRequestEnvelope request,
			String processModelUUID) {
		ContextHelper.initContext(request);
		return manager.getOwnerTimeEvents(processModelUUID);
	}

	@Override
	public List<TimeEvent> getParticipantTimeEvents(ExRequestEnvelope request,
			String processModelUUID) {
		ContextHelper.initContext(request);
		return manager.getParticipantTimeEvents(processModelUUID);
	}

	@Override
	public String create(ExRequestEnvelope request, ProcessModel processModel) {
		ContextHelper.initContext(request);
		return manager.create(processModel);
	}

	@Override
	public ProcessModel readByName(ExRequestEnvelope request,
			String processModelName) {
		ContextHelper.initContext(request);
		ProcessModel processModel = manager.readByName(processModelName);
		processModel.setLanguage(request.getLanguage());
		return processModel;
	}

	@Override
	public ProcessModel readByUUID(ExRequestEnvelope request,
			String processModelUUID) {
		ContextHelper.initContext(request);
		ProcessModel processModel = manager.readByUUID(processModelUUID);
		if (processModel != null) {
			processModel.setLanguage(request.getLanguage());			
		}
		return processModel;
	}

	@Override
	public List<ProcessAttribute> getConfigurableAttributes(
			ExRequestEnvelope request, String processModelUUID) {
		ContextHelper.initContext(request);
		return manager.getConfigurableAttributes(processModelUUID);
	}

	@Override
	public List<ProcessAttribute> getAttributes(ExRequestEnvelope request,
			String processModelUUID) {
		ContextHelper.initContext(request);
		return manager.getAttributes(processModelUUID);
	}

	@Override
	public InformationModelDocument getBaseInformationModelDocument(
			ExRequestEnvelope request, String processModelUUID, String iTitle,
			String iLanguage) {
		ContextHelper.initContext(request);
		return manager.getBaseInformationModelDocument(processModelUUID, iTitle, iLanguage);
	}
	


}

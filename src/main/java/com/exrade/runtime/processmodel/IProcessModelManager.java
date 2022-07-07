package com.exrade.runtime.processmodel;

import com.exrade.models.informationmodel.InformationModelDocument;
import com.exrade.models.processmodel.ProcessAttribute;
import com.exrade.models.processmodel.ProcessModel;
import com.exrade.models.processmodel.ProcessModelSummary;
import com.exrade.models.processmodel.protocol.events.TimeEvent;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IProcessModelManager {

	List<ProcessModelSummary> getProcessList(QueryFilters filters);

	List<ProcessModel> getPlainProcessList(QueryFilters filters);

	List<TimeEvent> getOwnerTimeEvents(String processModelUUID);

	List<TimeEvent> getParticipantTimeEvents(String processModelUUID);

	String create(ProcessModel processModel);

	ProcessModel readByName(String processModelName);

	ProcessModel readByUUID(String processModelUUID);

	List<ProcessAttribute> getConfigurableAttributes(String processModelUUID);

	List<ProcessAttribute> getAttributes(String processModelUUID);
	
	InformationModelDocument getBaseInformationModelDocument(
			String processModelUUID, String iTitle, String iLanguage);

}
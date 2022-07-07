package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.informationmodel.InformationModelDocument;
import com.exrade.models.processmodel.ProcessAttribute;
import com.exrade.models.processmodel.ProcessModel;
import com.exrade.models.processmodel.ProcessModelSummary;
import com.exrade.models.processmodel.protocol.events.TimeEvent;

import java.util.List;
import java.util.Map;

public interface ProcessModelAPI {

	List<ProcessModelSummary> getProcessList(ExRequestEnvelope request, Map<String, String> map);

	List<TimeEvent> getOwnerTimeEvents(ExRequestEnvelope request, String processUUID);

	List<TimeEvent> getParticipantTimeEvents(ExRequestEnvelope request, String processUUID);

	String create(ExRequestEnvelope request, ProcessModel processModel);

	ProcessModel readByName(ExRequestEnvelope request, String processModelName);

	ProcessModel readByUUID(ExRequestEnvelope request, String processModelUUID);

	List<ProcessAttribute> getConfigurableAttributes(ExRequestEnvelope request, String processUUID);

	List<ProcessAttribute> getAttributes(ExRequestEnvelope request, String processUUID);
	
	InformationModelDocument getBaseInformationModelDocument(ExRequestEnvelope request,String processModelUUID,String iTitle,String iLanguage);

}
package com.exrade.runtime.processmodel;

import com.exrade.core.ExLogger;
import com.exrade.models.common.Meta.Operation;
import com.exrade.models.informationmodel.InformationModelDocument;
import com.exrade.models.processmodel.ProcessAttribute;
import com.exrade.models.processmodel.ProcessModel;
import com.exrade.models.processmodel.ProcessModelSummary;
import com.exrade.models.processmodel.protocol.events.TimeEvent;
import com.exrade.platform.persistence.query.ModelVersionQueryUtil;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.filemanagement.DBFileStorageController;
import com.exrade.runtime.filemanagement.IFileStorageController;
import com.exrade.runtime.processmodel.persistence.ProcessModelPersistentManager;
import com.exrade.runtime.processmodel.persistence.ProcessModelPersistentManager.ProcessModelQFilters;
import com.exrade.util.ExCollections;
import com.exrade.util.ObjectsUtil;
import com.google.common.base.Strings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is responsible for Managing Negotiation Process Models
 * 
 * @author john
 * 
 */
public class ProcessModelManager implements IProcessModelManager {

	private ProcessModelPersistentManager processModelPM;
	
	public ProcessModelManager(){
		this(new ProcessModelPersistentManager());
	}
	
	public ProcessModelManager(ProcessModelPersistentManager processModelPM){
		this.processModelPM = processModelPM;
	}
	
	
	/* (non-Javadoc)
	 * @see com.exrade.runtime.processmodel.IProcessModelManager#getProcessList()
	 */
	@Override
	public List<ProcessModelSummary> getProcessList(QueryFilters filters) {
		List<ProcessModelSummary> summarys = new ArrayList<>();
		
		for(ProcessModel processModel : getPlainProcessList(filters)){
			summarys.add(ObjectsUtil.getProxy(ProcessModelSummary.class, processModel));
		}
		return summarys;
	}

	@Override
	public List<ProcessModel> getPlainProcessList(QueryFilters filters) {
		ModelVersionQueryUtil.addModelVersionFilter(filters);
		return processModelPM.listProcessModels(filters);
	}
	
	/* (non-Javadoc)
	 * @see com.exrade.runtime.processmodel.IProcessModelManager#getOwnerTimeEvents(java.lang.String)
	 */
	@Override
	public List<TimeEvent> getOwnerTimeEvents(String processModelUUID) {
		ProcessModel processModel = readByUUID(processModelUUID);
		List<TimeEvent> timeEvents = new ArrayList<TimeEvent>();
		if (processModel != null) {
			timeEvents = processModel.getOwnerProtocolBehaviour().getTimeEvents();
		}
		return timeEvents;
	}

	
	/* (non-Javadoc)
	 * @see com.exrade.runtime.processmodel.IProcessModelManager#getParticipantTimeEvents(java.lang.String)
	 */
	@Override
	public List<TimeEvent> getParticipantTimeEvents(String processModelUUID) {
		ProcessModel processModel = readByUUID(processModelUUID);
		List<TimeEvent> timeEvents = new ArrayList<TimeEvent>();
		if (processModel != null) {
			timeEvents = processModel.getParticipantsProtocolBehaviour().getTimeEvents();
		}
		return timeEvents;
	}

	/*private ProcessModel getProcessModel(String processModelName) {
		IProcessModelFactory processModelFactory = ExradeProcessModel.fromName(processModelName).getFactory();
		ProcessModel processModel = new ProcessModel(processModelFactory);
		processModel.build();

		return processModel;
	}*/

	/* (non-Javadoc)
	 * @see com.exrade.runtime.processmodel.IProcessModelManager#create(com.exrade.models.processmodel.ProcessModel)
	 */
	@Override
	public String create(ProcessModel processModel) {
		//save image
		String imageFileUUID=saveprocessModelImage(processModel);
		processModel.setImageFileUUID(imageFileUUID);
		processModelPM.create(processModel);
		return processModel.getUuid();
	}
	
	
	/**
	 * Saves process image to File Store, called during DB initialization
	 * @param processModel
	 * @return FileUUID
	 */
	private String saveprocessModelImage(ProcessModel processModel){
		try{
			IFileStorageController fileController=new DBFileStorageController();
//			URL url = ResourceUtil.class.getResource("processModelImages/"+processModel.getName()+".jpg");			
//			File imageFile=new File(URLDecoder.decode(url.getPath(), "UTF-8"));
			
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource resource = resolver.getResource("classpath:processModelImages/"+processModel.getName()+".png");
			InputStream imageStream = null;
			if ( resource != null && resource.exists()) {
				imageStream = resource.getInputStream();
			}
			else {
				ExLogger.get().warn("No image found for: {}",processModel.getName());
				return null;
			}
			return fileController.storeFile(imageStream, processModel.getName()+".jpg",null);
		}
		catch (Exception e) {
			ExLogger.get().error("Error in retrieving image for: {}",processModel.getName());
			return null;
		}
	}

	
	/* (non-Javadoc)
	 * @see com.exrade.runtime.processmodel.IProcessModelManager#readByName(java.lang.String)
	 */
	@Override
	public ProcessModel readByName(String processModelName) {
		if(!Strings.isNullOrEmpty(processModelName)){
			QueryFilters filters = new QueryFilters();
			filters.put(ProcessModelQFilters.NAME, processModelName);
			ModelVersionQueryUtil.addModelVersionFilter(filters);
			return processModelPM.read(filters);
		}
		return null;
	}

	
	/* (non-Javadoc)
	 * @see com.exrade.runtime.processmodel.IProcessModelManager#readByUUID(java.lang.String)
	 */
	@Override
	public ProcessModel readByUUID(String processModelUUID) {
		if(processModelUUID != null){
			QueryFilters filters = new QueryFilters();
			filters.put(QueryParameters.UUID, processModelUUID);
			return processModelPM.read(filters);
		}
		return null;
	}

	
	/* (non-Javadoc)
	 * @see com.exrade.runtime.processmodel.IProcessModelManager#getConfigurableAttributes(java.lang.String)
	 */
	@Override
	public List<ProcessAttribute> getConfigurableAttributes(
			String processModelUUID) {
		List<ProcessAttribute> attributes = new ArrayList<ProcessAttribute>();
		for(ProcessAttribute attribute : getAttributes(processModelUUID))
			if(attribute.isConfigurable())
				attributes.add(attribute);
		setOperation(attributes, Operation.SET);
		return attributes;
	}

	
	/* (non-Javadoc)
	 * @see com.exrade.runtime.processmodel.IProcessModelManager#getAttributes(java.lang.String)
	 */
	@Override
	public List<ProcessAttribute> getAttributes(String processModelUUID) {
		List<ProcessAttribute> attributes = new ArrayList<ProcessAttribute>();
		ProcessModel pm = readByUUID(processModelUUID);
		
		if(pm != null && pm.getProcessAttributes() != null)
			attributes = ProcessAttribute.newInstance(pm.getProcessAttributes());
		return attributes;
	}
	
	/*private static void addOperation(List<ProcessAttribute> iItems,String iOperation){
		for (ProcessAttribute attribute : iItems) {
			attribute.getMeta().addOperation(iOperation);
			setOperation(attribute.getProperties(),iOperation);
		}
	}*/
	
	private static void setOperation(List<ProcessAttribute> iProperties, String iOperation) {
		for (ProcessAttribute attribute : iProperties) {
				attribute.getMeta().addOperation(iOperation);
			if (ExCollections.isNotEmpty(attribute.getProperties())) {
				setOperation(attribute.getProperties(), iOperation);
			}
		}
	}

	@Override
	public InformationModelDocument getBaseInformationModelDocument(String processModelUUID,String iTitle,String iLanguage) {
		ProcessModel process = readByUUID(processModelUUID);
		InformationModelDocument informationDocument = null;
		if (process != null) {
			informationDocument = new InformationModelDocument(process.getRequiredAttributes(), iLanguage, iTitle);			
		}
		return informationDocument;
	}

}

package com.exrade.runtime.kyc;

import com.exrade.core.ExLogger;
import com.exrade.models.integration.IntegrationServiceType;
import com.exrade.models.integration.IntegrationSetting;
import com.exrade.models.kyc.Kyc;
import com.exrade.models.messaging.Offer;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.filemanagement.FileStorageProvider;
import com.exrade.runtime.filemanagement.IFileStorageController;
import com.exrade.runtime.integration.IntegrationSettingManager;
import com.exrade.runtime.kyc.persistence.KycQuery;
import com.exrade.runtime.kyc.schedular.CallbackHandlerJobScheduler;
import com.exrade.runtime.rest.RestParameters.KycFields;
import com.exrade.util.ExCollections;
import com.exrade.util.JSONUtil;
import com.exrade.util.ResourceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KycManager {
	private static final Logger LOGGER = ExLogger.get();
	private static final Double MAX_UPLOAD_SIZE = 4000000.0; // 4mb
	private PersistentManager persistenceManager = new PersistentManager();
	private IntegrationSettingManager integrationSettingManager = new IntegrationSettingManager();
	private static final KycManager INSTANCE = new KycManager();
	@SuppressWarnings("unchecked")
	public static final Map<String, Object> KYC_SERVICES = (Map<String, Object>) ResourceUtil.getJsonResource("kyc/kyc.json", Map.class);
	private IFileStorageController fsc = FileStorageProvider.getFileStorageController();
	private JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
//	private IService port;

	private KycManager() {
		//KYC_SERVICES = ResourceUtil.getJsonResource("kyc/kyc.json", Map.class);
//		factory.setServiceClass(IService.class);
		factory.setAddress(ExConfiguration.getStringProperty("kyc.serviceUrl"));
		//factory.setAddress("https://apiv3-uat.w2globaldata.com/Service.svc");
		// Attache your RequestInterceptor here
	    factory.getOutInterceptors().add(new RequestInterceptor());
	   // Attache your ResponseInterceptor here
	    factory.getInInterceptors().add(new ResponseInterceptor());

//	    port = (IService) factory.create();
//	    Client client = ClientProxy.getClient(port);

		TLSClientParameters tlsClientParameters = new TLSClientParameters();
		tlsClientParameters.setDisableCNCheck(true);

		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setConnectionTimeout(120000);
		httpClientPolicy.setReceiveTimeout(120000);

//		HTTPConduit http = (HTTPConduit) client.getConduit();
//		http.setTlsClientParameters(tlsClientParameters);
//		http.setClient(httpClientPolicy);
	}

	public static KycManager getInstance() {
		return INSTANCE;
	}

//	public DocumentUploadResponse uploadDocument(String fileUUID, Offer offer) {
//		DocumentUploadResponse response = null;
//		IntegrationSetting profileKycSettings = integrationSettingManager.getIntegrationSetting(offer.getReceiver().getProfile().getUuid(), IntegrationServiceType.W2_KYC_AML);// ResourceUtil.getJsonResource("kyc/bundle_neosurf.json", Map.class);
//		if(!profileKycSettings.isActive())
//			throw new ExException("KYC integration is not active!");
//
//		byte[] fileContent = fsc.retrieveFileAsByte(fileUUID);
//		if(fileContent.length > MAX_UPLOAD_SIZE)
//			throw new ExException("File is too big. Maximum allowed file size is 4 megabytes.");
//
//		Base64 base64 = new Base64();
//		String encodedString = new String(base64.encode(fileContent));
//
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(TimeProvider.now());
//		cal.add(Calendar.DATE, 30);
//		GregorianCalendar gc = new GregorianCalendar();
//		gc.setTime(cal.getTime());
//		XMLGregorianCalendar expiaryDate = null;
//		try {
//			expiaryDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc); //"2018-02-25T00:00:00"
//		} catch (DatatypeConfigurationException e1) {
//			LOGGER.warn("Uploading KYC document: error setting expiaryDate", e1);
//		}
//
//		DocumentUploadRequest docRequest = new DocumentUploadRequest();
//		docRequest.setDocumentData(encodedString);
//		docRequest.setDocumentReference(fileUUID); // fileUUID or variableId
//		docRequest.setDocumentExpiry(expiaryDate);
//		docRequest.setDocumentType(DocumentTypeEnum.NONE);
//		docRequest.setServiceAuthorisation(getServiceAuthorisation(profileKycSettings.getSettings()));
//		docRequest.getServiceAuthorisation().setClientReference(offer.getUuid());
//		docRequest.setQueryOptions(buildCommonQueryOptions(ExConfiguration.getPropertyAsBoolean("kyc.sandoboxModeForDocument")));
//
////		IService port = (IService) factory.create();
////		Client client = ClientProxy.getClient(port);
////
////		TLSClientParameters tlsClientParameters = new TLSClientParameters();
////		tlsClientParameters.setDisableCNCheck(true);
////		HTTPConduit http = (HTTPConduit) client.getConduit();
////		http.setTlsClientParameters(tlsClientParameters);
//
//		try {
//			LOGGER.info("Starting document upload fileUUID: {}, offerUUID: {}", fileUUID, offer.getUuid());
//			response = port.uploadDocument(docRequest);
//			LOGGER.info("Finished document upload fileUUID: {}, offerUUID: {}", fileUUID, offer.getUuid());
//			//TODO: update the offer
//		} catch (IServiceUploadDocumentServiceFaultFaultFaultMessage e) {
//			LOGGER.error("Uploading KYC document failed", e);
//			throw new ExException("Error uploading KYC document");
//		}
//		return response;
//	}

	@SuppressWarnings("unchecked")
	public void checkKyc(Offer offer) {
		if(offer == null || Strings.isNullOrEmpty(offer.getTemplate()))
			return;
		// for each bundle call the api and update the template with result
		// if file then upload
		//Map profileKycSettings = ResourceUtil.getJsonResource("kyc/bundle_neosurf.json", Map.class);
		IntegrationSetting profileKycSettings = integrationSettingManager.getIntegrationSetting(offer.getReceiver().getProfile().getUuid(), IntegrationServiceType.W2_KYC_AML);
		if(!profileKycSettings.isActive())
			throw new ExException("KYC integration is not active!");
		//profileKycSettings.get("settings")
		Document doc = Jsoup.parse(offer.getTemplate()); //.kyc-variable > .kycdata -> .kycdataitem [data-kycdata]
		Elements kycVariableContainers = doc.select(".kyc-variable");

		if(ExCollections.isEmpty(kycVariableContainers))
			return;

//		for(Element kycVariableContainer : kycVariableContainers){
//			List<String> kycBundles = Arrays.asList(kycVariableContainer.attr("data-bundles").split(",")); // get services for bundles
//
//			for(String bundleName : kycBundles) {
//				//Set<String> services = getServices((Map)profileKycSettings.get("bundleMappings"), kycBundles);
//				Map<String, Object> bundleMappings = (Map<String, Object>) profileKycSettings.getSettings().get("bundleMappings");
//				List<String> services = (List<String>)bundleMappings.get(bundleName);
//				Map<String, Object> serviceSettings = (Map<String, Object>) KYC_SERVICES.get("services");
//
//				Elements kycDataItems = kycVariableContainer.select(".kycdataitem");
//				for(Element kycDataItem : kycDataItems){
//					Map<String, Object> kycData = JSONUtil.deserializeMap(kycDataItem.attr("data-kycdata"), String.class, Object.class);
//
//					for(String service : services) {
//						// validate kycData against service settings
//						// build queryData from kycData
//						// billd request object (document or object) && call service
//						// update offer with response
//
//						Map<String, Object> serviceSetting = (Map<String, Object>)serviceSettings.get(service);
//						List<String> applicableCountries = (List<String>) serviceSetting.get("countries");
//						if(ExCollections.isNotEmpty(applicableCountries) && !applicableCountries.contains(kycData.get("Country")))
//							continue;
//
//						Kyc kyc = new Kyc();
//
//						BundleData bundle = new BundleData();
//						bundle.setBundleName(bundleName);
//
//						QueryData qData = buildQueryData(service, kycData);
//						ServiceRequest request = new ServiceRequest();
//						request.setBundleData(bundle);
//						request.setQueryData(qData);
//						request.setServiceAuthorisation(getServiceAuthorisation(profileKycSettings.getSettings()));
//						request.getServiceAuthorisation().setClientReference(offer.getUuid());
//						request.setQueryOptions(buildCommonQueryOptions(ExConfiguration.getPropertyAsBoolean("kyc.sandoboxMode")));
//
//						if(requiresDocumentUpload(service)) {
//							String fileUUID = ((Map<String, Object>)kycData.get("Document")).get("file_uuid").toString();
//							DocumentUploadResponse documentUploadResponse = uploadDocument(fileUUID, offer);
//							kyc.setDocumentUID(documentUploadResponse.getDocumentUID());
//							kyc.setDocumentVerificationResultPending(true);
//							kyc.setRawDocumentUploadRequest(RequestInterceptor.requestXml);
//							kyc.setRawDocumentUploadResponse(ResponseInterceptor.responseXml);
//
//							request.setQueryOptions(buildCommonQueryOptions(ExConfiguration.getPropertyAsBoolean("kyc.sandoboxModeForDocument")));
//							KeyValueOfstringstring kvOptionVerificationType = new KeyValueOfstringstring();
//							kvOptionVerificationType.setKey("VerificationType");
//							kvOptionVerificationType.setValue("both");
//							request.getQueryOptions().getKeyValueOfstringstring().add(kvOptionVerificationType);
//
//							UploadedFile file = new UploadedFile();
//							file.setDocumentReference(documentUploadResponse.getDocumentReference());
//							file.setDocumentUID(documentUploadResponse.getDocumentUID());
//							file.setService(ServiceEnum.W_2_DATA_MDV_026);
//							ArrayOfUploadedFile files = new ArrayOfUploadedFile();
//							files.getUploadedFile().add(file);
//							request.setUploadedFiles(files);
//						}
//
//						try {
//							LOGGER.info("Calling service for bundle: {}, offerUUID: {}", bundleName, offer.getUuid());
//							ServiceResponse response = port.kycCheck(request);
//							LOGGER.info("Finished calling service for bundle: {}, offerUUID: {}", bundleName, offer.getUuid());
//
////							kycDataItem.select("tbody").append("<tr id=\"" + response.getProcessRequestResult().getTransactionInformation().getServiceCallReference() +  "\" class=\"kycdataitem-callreference\">"
////												+ "<td class=\"kycdataitem-bundle\">" +  bundleName + "</td>"
////												+ "<td class=\"kycdataitem-result\">" +  response.getProcessRequestResult().getTransactionInformation().getInterpretResult() + "</td>"
////											+ "</tr>");
//
//							kyc.setBundleName(bundleName);
//							kyc.setServiceName(service);
//							kyc.setOffer(offer);
//							kyc.setServiceCallReference(response.getProcessRequestResult().getTransactionInformation().getServiceCallReference());
//							kyc.setInterpretedResult(response.getProcessRequestResult().getTransactionInformation().getInterpretResult().name());
//							kyc.setRawRequest(RequestInterceptor.requestXml);
//							kyc.setRawResponse(ResponseInterceptor.responseXml);
//							persistenceManager.create(kyc);
//							// response.getProcessRequestResult().getTransactionInformation().getInterpretResult() -> Pass | Fail | Inconclusive | NoInterpretPerformed | NotApplicable
//							// response.getProcessRequestResult().getTransactionInformation().getServiceCallReference() ->
//							// response.getProcessRequestResult().getTransactionInformation().getServiceTransactions().getServiceTransactionInformation().get(0).getServiceInterpretResult()
//							//	-> Pass, Fail, Inconclusive, NoResults, OneResult, MultipleResults, NotApplicable, NotPerformed
//							// response.getProcessRequestResult().getTransactionInformation().getServiceTransactions().getServiceTransactionInformation().get(0).getServiceTransactionResult()
//							//	-> Success, SuccessNoResults, SuccessIncompleteResults, MultipleChoices, ClientErrorInsufficientInformation, NotExecutedDueToPreviousHalt, ServerErrorGeneralError
//							// response.getProcessRequestResult().getTransactionInformation().getServiceTransactions().getServiceTransactionInformation().get(0).getServiceTransactionResultMessage()
//							// response.getProcessRequestResult().getTransactionInformation().getServiceCallReference() // store serviceCallReference
//							// TODO: update offer
//						} catch (IServiceKYCCheckServiceFaultFaultFaultMessage e) {
//							LOGGER.error("Calling KYC service failed for the service: " + service, e);
//							throw new ExException("Error checking KYC");
//						}
//					}
//				}
//			}
//		}

		offer.setTemplate(doc.outerHtml());
		LOGGER.debug(offer.getTemplate());
	}

	public void handleCallback(JsonNode callbackData) {

		String offerUUID = callbackData.path("ClientReference").asText();
		String callReference = callbackData.path("CallReference").asText();

		LOGGER.info("Hnadling callback for the ClientReference: {}, CallReference: {}", offerUUID, callReference);

		// get kyc object && update the status
		QueryFilters filters = QueryFilters.create(KycFields.SERVICE_CALL_REFERENCE, callReference);
		List<Kyc> kycResults = listKycs(filters);

		if(ExCollections.isNotEmpty(kycResults)) {
			Kyc kyc = kycResults.get(0);

			//IMessageManager messageManager = new MessageManager();
			//Offer offer = (Offer)messageManager.readByUUID(offerUUID);

			//if(offer == null || Strings.isNullOrEmpty(offer.getTemplate()))
			//	return;

			//LOGGER.debug(offer.getTemplate());

			//Document doc = Jsoup.parse(offer.getTemplate());
			//String callReferenceSelector = "#" + callReference;
			//Element callReferenceElement = doc.select(callReferenceSelector).first();
			//if(callReferenceElement != null) {
				//Element callReferenceResultElement = callReferenceElement.select(".kycdataitem-result").first();
				//callReferenceResultElement.html(callbackData.path("ServiceResults").path("W2DataMdv026").path("ResultCategory").asText());
				//offer.setTemplate(doc.outerHtml());

				kyc.setDocumentVerificationResultPending(false);
				kyc.setInterpretedResult(callbackData.path("ServiceResults").path("W2DataMdv026").path("ResultCategory").asText());
				kyc.setRawDocumentVerificationResponse(JSONUtil.toJson(callbackData));
				persistenceManager.update(kyc);
				//persistenceManager.update(offer);

//				Elements callReferenceElements = doc.select(".kycdataitem-callreference");
//				for(Element el : callReferenceElements) {
//					LOGGER.debug(".kycdataitem-callreference element id: {}, html: {}", el.id(), el.outerHtml());
//					if(el.id().equals(callReference)) {
//						callReferenceElement = el;
//						break;
//					}
//				}
			//}
			//else {
				//LOGGER.warn("Could not find CallReference Selector with selector: {}", callReferenceSelector);
			//	new CallbackHandlerJobScheduler().schedule(offerUUID, callReference, JSONUtil.toJson(callbackData));
			//}
		}
		else {
			new CallbackHandlerJobScheduler().schedule(offerUUID, callReference, JSONUtil.toJson(callbackData));
		}


	}

	public List<Kyc> listKycs(QueryFilters iFilters) {
		return persistenceManager.listObjects(new KycQuery(), iFilters);
	}

//	@SuppressWarnings("unchecked")
//	private QueryData buildQueryData(String service, Map<String, Object> kycData) {
//		QueryData qData = new QueryData();
//
//		Map<String, Object> serviceSettings = (Map<String, Object>) KYC_SERVICES.get("services");
//		Map<String, Object> propertiesSpecification = (Map<String, Object>)KYC_SERVICES.get("properties");
//
//		Map<String, Object> serviceSetting = (Map<String, Object>)serviceSettings.get(service);
//		List<String> requiredProperties = (List<String>) serviceSetting.get("requiredProperties");
//		List<String> optionalProperties = (List<String>) serviceSetting.get("optionalProperties");
//
//		qData = updateQueryData(qData, kycData, requiredProperties, propertiesSpecification, true);
//		qData = updateQueryData(qData, kycData, optionalProperties, propertiesSpecification, false);
//
//		return qData;
//	}

//	@SuppressWarnings("unchecked")
//	private QueryData updateQueryData(QueryData qData, Map<String, Object> kycData, List<String> properties, Map<String, Object> propertiesSpecification, boolean isRequired) {
//		for(String property : properties) {
//			Object valueObject = null;
//
//			if(kycData.containsKey(property))
//				valueObject = kycData.get(property);
//			else {
//				List<String> composedFields = (List<String>) ((Map<String, Object>)propertiesSpecification.get(property)).get("composition");
//				if(!ExCollections.isEmpty(composedFields)) {
//					List<String> values = new ArrayList<>();
//					for(String composedField : composedFields) {
//						values.add((String)kycData.get(composedField));
//					}
//					valueObject = StringUtils.join(values, " ");
//				}
//			}
//
//			if(isRequired && valueObject == null)
//				throw new ExParamException(ErrorKeys.PARAM_INVALID, property);
//
//			if(valueObject != null) {
//				String fieldName = property;
//				if(StringUtils.isAllUpperCase(fieldName))
//					fieldName = fieldName.toLowerCase();
//				else
//					fieldName = StringUtils.uncapitalize(fieldName);
//
//				if(valueObject instanceof String) {
//					try {
//						valueObject = URLDecoder.decode(valueObject.toString(), "UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						throw new ExParamException(ErrorKeys.PARAM_INVALID, fieldName);
//					}
//				}
//
//				ObjectsUtil.setField(qData, fieldName, valueObject);
//			}
//		}
//
//		return qData;
//	}

	@SuppressWarnings("unchecked")
	private Set<String> getServices(Map bundleMappings, List<String> bundles) {
		Set<String> services = new HashSet<>();

		for(String bundle : bundles) {
			services.addAll((List<String>)bundleMappings.get(bundle));
		}

		return services;
	}

//	private ServiceAuthorisation getServiceAuthorisation(Map<String, Object> settings) {
//		ServiceAuthorisation auth = new ServiceAuthorisation();
//		auth.setAPIKey(settings.get("apiKey").toString()); //"5830002a-9a46-4147-816f-6672314ec714");
//
//		return auth;
//	}
//
//	private ArrayOfKeyValueOfstringstring buildCommonQueryOptions(boolean isSandboxMode) {
//		KeyValueOfstringstring kvOption = new KeyValueOfstringstring();
//		kvOption.setKey("Sandbox");
//		kvOption.setValue(isSandboxMode ? "true" : "false");
//
//		ArrayOfKeyValueOfstringstring qOptions = new ArrayOfKeyValueOfstringstring();
//		qOptions.getKeyValueOfstringstring().add(kvOption);
//
//		return qOptions;
//	}

	@SuppressWarnings("unchecked")
	private boolean requiresDocumentUpload(String serviceName) {
		Map<String, Object> servicesSettings = (Map<String, Object>) KYC_SERVICES.get("services");

		Map<String, Object> serviceSettings = (Map<String, Object>) servicesSettings.get(serviceName);
		if((Boolean)serviceSettings.get("requiresDocumentUpload")) {
			return true;
		}

		return false;
	}

//	private Double calcBase64SizeInBytes(String base64String) {
//	    Double result = -1.0;
//	    if(StringUtils.isNotEmpty(base64String)) {
//	        Integer padding = 0;
//	        if(base64String.endsWith("==")) {
//	            padding = 2;
//	        }
//	        else {
//	            if (base64String.endsWith("=")) padding = 1;
//	        }
//	        result = (Math.ceil(base64String.length() / 4) * 3 ) - padding;
//	    }
//	    return result;
//	}
}

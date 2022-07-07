package com.exrade.runtime.asset.integration;

import com.exrade.core.ExLogger;
import com.exrade.models.asset.Asset;
import com.exrade.models.integration.IntegrationServiceType;
import com.exrade.models.integration.IntegrationSetting;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.asset.AssetManager;
import com.exrade.runtime.blockchain.NotarizationSmartContract;
import com.exrade.runtime.filemanagement.FileMetadata;
import com.exrade.runtime.filemanagement.FileStorageProvider;
import com.exrade.runtime.filemanagement.IFileStorageController;
import com.exrade.runtime.integration.IntegrationSettingManager;
import com.exrade.runtime.rest.RestParameters.AssetFields;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ExCollections;
import com.exrade.util.JSONUtil;
import com.exrade.util.RESTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.MessageFormat;
import java.util.*;

public class ServitlyIntegrationManager {
	private static final String BASE_URL = "https://api.semioty.com";

	private AssetManager assetManager = new AssetManager();
	private NotarizationSmartContract smartContractApiClient = new NotarizationSmartContract();
	private IFileStorageController fileStorageController = FileStorageProvider.getFileStorageController();
	private Negotiator profileOwner;

	private String apiKey;
	private String email;
	private String password;
	private String tenant;
	private Map<String, String> requestHeaders = new HashMap<String, String>();

	public static abstract class Constants {
		private static final String NAME = "name";
		private static final String CONTENT = "content";
		private static final String SERIAL_NUMBER = "serialNumber";
		private static final String GPS_POSITION = "gpsPosition";
		private static final String ID = "id";
		private static final String THING_ID = "thingId";
		private static final String THING_DEFINITION_ID = "thingDefinitionId";
		private static final String METRICS = "metrics";
		private static final String METRIC_NAME = "metricName";
		private static final String START_DATE = "startDate";
		private static final String END_DATE = "endDate";
		// private static final String PAGE_SIZE = "pageSize";
		// private static final String PAGE_TOKEN = "pageToken";
		private static final String DATA_LAST_SYNCED = "dataLastSynced";
		private static final String ASSET_DATA = "assetData";
		private static final String ACTIVE = "ACTIVE";
		private static final String PHYSICAL = "PHYSICAL";
	}

	public ServitlyIntegrationManager(String iProfileUUID) {
		IntegrationSettingManager integrationSettingManager = new IntegrationSettingManager();
		IntegrationSetting integrationSetting = integrationSettingManager.getIntegrationSetting(iProfileUUID,
				IntegrationServiceType.SERVITLY_SERVITIZATION);

		if (integrationSetting == null)
			throw new ExNotFoundException(
					MessageFormat.format("IntegrationSetting not found for - Profile: {0}, ServiceType: {1}",
							iProfileUUID, IntegrationServiceType.SERVITLY_SERVITIZATION.name()));

		if (integrationSetting.isActive()) {
			profileOwner = new MembershipManager().getOwnerMembership(iProfileUUID);

			apiKey = integrationSetting.getSettings().get("apiKey").toString();
			email = integrationSetting.getSettings().get("email").toString();
			password = integrationSetting.getSettings().get("password").toString();
			tenant = integrationSetting.getSettings().get("tenant").toString();

			requestHeaders.put("X-Semioty-Tenant", tenant);

			authenticate();
		}
	}

	public void authenticate() {
		ExLogger.get().debug("Authenticating with Servitly");

		String url = MessageFormat.format("{0}/identity/users/login?apiKey={1}", BASE_URL, apiKey);

		Map<String, String> bodyParameters = new HashMap<>();
		bodyParameters.put("email", email);
		bodyParameters.put("password", password);

		//todo commented
//		final WSRequestHolder requestHolder = WS.url(url);
//
//		for (Entry<String, String> parameter : requestHeaders.entrySet()) {
//			requestHolder.setHeader(parameter.getKey(), parameter.getValue());
//		}
//
//		final Promise<WSResponse> responsePromise = requestHolder.post(JSONUtil.toJsonNode(bodyParameters));
//
//		final WSResponse response = responsePromise.get(PlayAuthenticate.TIMEOUT);
//		requestHeaders.put("Authorization", MessageFormat.format("Bearer {0}", response.getHeader("Authorization")));
//		String cookieName = MessageFormat.format("SMTY_USR_{0}", tenant);
//		requestHeaders.put("Cookie",
//				MessageFormat.format("{0}={1}", cookieName, response.getCookie(cookieName).getValue()));

		ExLogger.get().debug("Authenticated with Servitly");
	}

	public void syncAssets() {
		ExLogger.get().debug("Syncing assets with Servitly");

		JsonNode thingsResponseNode = RESTUtil.doRestGET(MessageFormat.format("{0}/v2/inventory/things", BASE_URL),
				requestHeaders, null);
		JsonNode thingsNode = thingsResponseNode.get(Constants.CONTENT);
		for (JsonNode thingNode : thingsNode) {
			String referenceId = thingNode.get(Constants.ID).asText();

			QueryFilters queryFilters = QueryFilters.create(AssetFields.EXTERNAL_ID, referenceId);
			queryFilters.put(AssetFields.DATA_SOURCE, IntegrationServiceType.SERVITLY_SERVITIZATION.name());

			if (ExCollections.isEmpty(assetManager.listAssets(queryFilters))) {
				Asset asset = new Asset();
				asset.setName(thingNode.get(Constants.NAME).asText());
				asset.setSerialNumber(thingNode.get(Constants.SERIAL_NUMBER).asText());

				asset.setGpsPosition(thingNode.path(Constants.GPS_POSITION).asText());

				asset.setOwner(profileOwner);

				asset.setStatus(Constants.ACTIVE);
				asset.setType(Constants.PHYSICAL);

				asset.setCategory(getCategory(asset.getName()));

				asset.setExternalId(referenceId);
				asset.setDataSource(IntegrationServiceType.SERVITLY_SERVITIZATION.name());
				asset.getTags().add("servitly");
				asset.getTags().add("sisspre");
				asset.getTags().add("olive");
				if (!Strings.isNullOrEmpty(asset.getCategory()))
					asset.getTags().add(asset.getCategory());

				asset.getCustomFields().put(Constants.THING_DEFINITION_ID,
						thingNode.path(Constants.THING_DEFINITION_ID).asText());
				asset.getCustomFields().put("rawData", JSONUtil.toJson(thingNode));

				asset = assetManager.createAsset(asset);
			} else {
				ExLogger.get().debug("Skipping creation of asset as already exists. ReferenceId: {}", referenceId);
			}

		}

		ExLogger.get().debug("Finished syncing assets with Servitly");

		syncAssetMetrics();
		syncAssetData();
		// TODO: handle pagination
		// content, last, totalPages, totalElements, size, number, sort, first,
		// numberOfElements
	}

	public void syncAssetMetrics() {
		ExLogger.get().debug("Syncing assets metrics with Servitly");

		QueryFilters queryFilters = QueryFilters.create(AssetFields.OWNER_PROFILE, profileOwner.getProfile().getUuid());
		queryFilters.put(AssetFields.DATA_SOURCE, IntegrationServiceType.SERVITLY_SERVITIZATION.name());

		List<Asset> assets = assetManager.listAssets(queryFilters);
		for (Asset asset : assets) {
			ExLogger.get().debug("Syncing asset data with Servitly. Asset: {}", asset.getUuid());
			try {
				String url = String.format("%s/inventory/thingDefinitions/%s/metrics", BASE_URL,
						asset.getCustomFields().get(Constants.THING_DEFINITION_ID));
				ExLogger.get().debug(url);
				JsonNode metricsResponse = RESTUtil.doRestGET(url, requestHeaders, null);

				List<String> metrics = new ArrayList<String>();
				for (JsonNode metricNode : metricsResponse) {
					metrics.add(metricNode.path(Constants.NAME).asText());
				}

				asset.getCustomFields().put(Constants.METRICS, String.join(",", metrics));
				assetManager.updateAsset(asset);
			} catch (Exception ex) {
				ExLogger.get().warn("Error retrieving metric: ", ex);
			}

		}

		ExLogger.get().debug("Finished syncing assets metrics with Servitly");
	}

	public void syncAssetData() {
		ExLogger.get().debug("Syncing assets data with Servitly");

		long endDate = TimeProvider.now().getTime();

		QueryFilters queryFilters = QueryFilters.create(AssetFields.OWNER_PROFILE, profileOwner.getProfile().getUuid());
		queryFilters.put(AssetFields.DATA_SOURCE, IntegrationServiceType.SERVITLY_SERVITIZATION.name());

		List<Asset> assets = assetManager.listAssets(queryFilters);

		for (Asset asset : assets) {
			if (asset.getCustomFields().get(Constants.METRICS) != null) {
				long startDate = getStartDate(asset);

				for (String metric : asset.getCustomFields().get(Constants.METRICS).toString().split(",")) {
					String fileUUID = syncAssetDataForMatric(asset, metric, startDate, endDate);
					if (fileUUID != null)
						asset.getFiles().add(fileUUID);
				}

				asset.getCustomFields().put(Constants.DATA_LAST_SYNCED, String.format("%d", endDate));
				assetManager.updateAsset(asset);
			}
		}

		ExLogger.get().debug("Finished syncing assets data with Servitly");
	}

	private String syncAssetDataForMatric(Asset asset, String metric, long startDate, long endDate) {
		ExLogger.get().debug("Syncing asset data with Servitly. Asset: {}, Metric: {}, StartDate: {}, EndDate: {}",
				asset.getUuid(), metric, startDate, endDate);

		String url = String.format("%s/data/values", BASE_URL);
		String fileUUID = null;
		try {

			Map<String, String> requestParams = new HashMap<String, String>();
			requestParams.put(Constants.THING_ID, String.format("%s", asset.getExternalId()));
			requestParams.put(Constants.METRIC_NAME, metric);
			requestParams.put(Constants.START_DATE, String.format("%d", startDate));
			requestParams.put(Constants.END_DATE, String.format("%d", endDate));

			JsonNode dataValuesResponse = RESTUtil.doRestGET(url, requestHeaders, requestParams);
			JsonNode dataValuesNode = dataValuesResponse.path("data");

			if (dataValuesNode.isArray() && dataValuesNode.size() > 0) {

				Map<String, Object> metaData = new HashMap<String, Object>();
				metaData.put(FileMetadata.ORIGINAL_NAME, String.format("%s_%s_%s_%s.json", asset.getUuid(),
						metric.replace(" ", ""), startDate, endDate));
				metaData.put(FileMetadata.ASSET_UUID, asset.getUuid());
				metaData.put(FileMetadata.AUTHOR, profileOwner.getIdentifier());
				metaData.put(FileMetadata.DESCRIPTION, Constants.ASSET_DATA);

				fileUUID = fileStorageController.storeFile(JSONUtil.toBytes(dataValuesNode), "json", metaData);
				ExLogger.get().debug("Asset: {}, Metric: {}, File: {}", asset.getUuid(), metric, fileUUID);

				String blockchainTx = notarize(fileUUID, fileStorageController.getFileMetadata(fileUUID), asset);
				metaData = new HashMap<String, Object>();
				metaData.put(FileMetadata.BLOCKCHAIN_TX, blockchainTx);
				fileStorageController.updateMetadata(fileUUID, metaData);
				// TODO: handle pagination
			}
		} catch (Exception ex) {
			ExLogger.get().warn("Error retrieving data: ", ex);
		}

		return fileUUID;
	}

	private String notarize(String fileUUID, Map<String, Object> metaData, Asset asset) {
		String transaction = smartContractApiClient.notarize(fileUUID, metaData, asset.getUuid(), asset.getOwner().getIdentifier());

		ExLogger.get().debug("Notarized - Asset: {}, File: {}, Transaction: {}", asset.getUuid(), fileUUID,
				transaction);

		return transaction;
	}

	private long getStartDate(Asset asset) {
		long startDate = new Date(0).getTime();

		if (asset.getCustomFields().get(Constants.DATA_LAST_SYNCED) != null) {
			startDate = Long.parseLong(asset.getCustomFields().get(Constants.DATA_LAST_SYNCED).toString()) + 1;
		}

		return startDate;
	}

	private String getCategory(String name) {
		String category = "";
		try {
			String[] nameParts = name.split(" ");

			for (int i = 0; i < 2 && i < nameParts.length; i++) {
				if (!NumberUtils.isNumber(nameParts[i]))
					category = String.format("%s %s", category, nameParts[i]);
			}
		} catch (Exception ex) {

		}

		return category.trim();
	}
}

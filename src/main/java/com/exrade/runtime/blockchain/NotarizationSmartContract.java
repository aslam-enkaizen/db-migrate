package com.exrade.runtime.blockchain;

import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.filemanagement.FileMetadata;
import com.exrade.runtime.userprofile.TraktiJwtManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.JSONUtil;
import com.exrade.util.RESTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotarizationSmartContract {
	private static final Logger logger = ExLogger.get();

	private static String BASE_URL = ExConfiguration.getStringProperty("site.apiUrl");

	public String notarize(String fileUUID, Map<String, Object> fileMetaData, String context, String membershipUUID) {
		Date fileCreationDate = (Date) fileMetaData.get(FileMetadata.CREATION_DATE);
		String hash = (String) fileMetaData.get(FileMetadata.HASH);

		Map<String, String> bodyParameters = new HashMap<>();
		bodyParameters.put("context", context);
		bodyParameters.put("user", membershipUUID);
		bodyParameters.put("timestamp", String.format("%d", fileCreationDate.getTime()));
		bodyParameters.put("hash", String.format("%s", hash));
		bodyParameters.put("file", fileUUID.split("\\.")[0]);

		JsonNode notarizationResponseNode = RESTUtil.doRestPOST(
				MessageFormat.format("{0}/notaries", BASE_URL), getAuthorizationHeader(),
				JSONUtil.toJsonNode(bodyParameters));

		String transaction = notarizationResponseNode.path("hash").asText(null);

		logger.debug("Notarized - Context: {}, File: {}, Transaction: {}", context, fileUUID,
				transaction);

		return transaction;
	}

	private Map<String, String> getAuthorizationHeader(){
		Map<String,String> headerParameters = new HashMap<>();
		headerParameters.put("Authorization", String.format("Bearer %s", TraktiJwtManager.getInstance().generateToken(ContextHelper.getMembershipUUID())));

		return headerParameters;
	}

}

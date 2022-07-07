package com.exrade.runtime.blockchain;

import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.userprofile.TraktiJwtManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.JSONUtil;
import com.exrade.util.RESTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class TraktiSmartContractApiClient {
	private static final Logger logger = ExLogger.get();
	public static final String ZERO_VALUED_UUID = "00000000-0000-0000-0000-000000000000";
	public static final Integer MAX_RETRY = 3;
	public static final Integer RETRY_DELAY_MILLISECONDS = 2000;

	private static String BASE_URL = ExConfiguration.getStringProperty("site.apiUrl");
	
	private boolean requiresAuth = true;
	private boolean waitForTransactionCompletion = false;
	
	public TraktiSmartContractApiClient(){
		
	}
	
	public TraktiSmartContractApiClient(boolean requiresAuth){
		this.requiresAuth = requiresAuth;
	}
	
	public TraktiSmartContractApiClient(boolean requiresAuth, boolean waitForTransactionCompletion){
		this.requiresAuth = requiresAuth;
		this.setWaitForTransactionCompletion(waitForTransactionCompletion);
	}
	
	public boolean isRequiresAuth() {
		return requiresAuth;
	}

	public void setRequiresAuth(boolean requiresAuth) {
		this.requiresAuth = requiresAuth;
	}

	public boolean isWaitForTransactionCompletion() {
		return waitForTransactionCompletion;
	}

	public void setWaitForTransactionCompletion(boolean waitForTransactionCompletion) {
		this.waitForTransactionCompletion = waitForTransactionCompletion;
	}

	public void initSmartContract(String contractUUID) {
        try {
            Map<String, String> bodyParameters = new HashMap<>();
            RESTUtil.doRestPOST(
                    MessageFormat.format("{0}/contracts/{1}/smart-contracts/init", BASE_URL, contractUUID), null,
                    JSONUtil.toJsonNode(bodyParameters));
        } catch (Exception ex) {
            ExLogger.get().warn("Failed to initialize smart contract from contract: " + contractUUID, ex);
        }
    }

	public String executeFunction(String contractMappingUUID, int functionMappingIndex, String functionSignature, Map<String, Object> functionInputParameters) {
		return executeFunction(ZERO_VALUED_UUID, contractMappingUUID, functionMappingIndex, functionSignature, functionInputParameters);
	}

	public String executeFunction(String contractUUID, String contractMappingUUID, int functionMappingIndex, String functionSignature,  Map<String, Object> functionInputParameters) {
		String transaction = null;

		JsonNode responseNode = invokeSmartContractFunction(contractUUID, contractMappingUUID, functionMappingIndex, functionSignature, functionInputParameters, false);
		if(responseNode != null && !responseNode.isEmpty()) {
			transaction = responseNode.path("hash").asText(null);
			
			if(!Strings.isNullOrEmpty(transaction) && isWaitForTransactionCompletion())
				getTransactionStatus(transaction, MAX_RETRY);
		}

		return transaction;
	}

	public JsonNode callFunction(String contractMappingUUID, int functionMappingIndex, String functionSignature, Map<String, Object> functionInputParameters) {
		return callFunction(ZERO_VALUED_UUID, contractMappingUUID, functionMappingIndex, functionSignature, functionInputParameters);
	}

	public JsonNode callFunction(String contractUUID, String contractMappingUUID, int functionMappingIndex, String functionSignature, Map<String, Object> functionInputParameters) {
		return invokeSmartContractFunction(contractUUID, contractMappingUUID, functionMappingIndex, functionSignature, functionInputParameters, false);
	}

	public JsonNode getTransaction(String transactionId){
		logger.info("Retrieving smart contract transaction. Tx: {}", transactionId);

		JsonNode responseNode = null;

		try {
			responseNode = RESTUtil.doRestGET(
				MessageFormat.format("{0}/bms/transactions/{1}", BASE_URL, transactionId), getAuthorizationHeader(), null);
		}
		catch(Exception ex) {
			logger.error("Smart contract get transaction failed", ex);
		}

		return responseNode;
	}

	public String getTransactionStatus(String transactionId){
		logger.info("Retrieving smart contract transaction status. Tx: {}", transactionId);
		JsonNode responseNode = getTransaction(transactionId);

		String status = null;
		if(responseNode != null)
			status = responseNode.path("status").asText(null);

		logger.info("Retrived smart contract transaction status. Tx: {}, status: {}", transactionId, status);
		return status;
	}

	public String getTransactionStatus(String transactionId, int retryCount){
		if(!Strings.isNullOrEmpty(transactionId)) {
			for(int i=0; i<retryCount; i++) {
				logger.info("Retrieving smart contract transaction status. Tx: {}, Max Retry: {}, Retry Count: {}", transactionId, retryCount, i);
				
				try {
					String transactionStatus = getTransactionStatus(transactionId);
					// SUCCESS | FAIL
					if("SUCCESS".equals(transactionStatus) || "FAIL".equals(transactionStatus)) {
						return transactionStatus;
					}
					// PENDING
					Thread.sleep(RETRY_DELAY_MILLISECONDS);
				}
				catch(Exception ex) {
					logger.error("Executing smart contract: Contract: {}, LinkTrak: {}, Index: {}", ex);
				}
			}
			
			logger.warn("Smart contract transaction pending. Tx: {}", transactionId);
		}


		return null;
	}
	
	public JsonNode getSmartContractMappings(String contractUUID){
		logger.info("Retrieving smart contract mappings. Contract: {}", contractUUID);

		JsonNode responseNode = null;

		try {
			responseNode = RESTUtil.doRestGET(
				MessageFormat.format("{0}/contracts/{1}/smart-contracts", BASE_URL, contractUUID), getAuthorizationHeader(), null);
		}
		catch(Exception ex) {
			logger.error("Smart contract get contract mappings failed", ex);
		}

		return responseNode;
	}
	
	public BigDecimal convertValue(Double value) {
		return Convert.toWei(new BigDecimal(value), Convert.Unit.ETHER);
	}

	private JsonNode invokeSmartContractFunction(String contractUUID, String contractMappingUUID, int functionMappingIndex, String functionSignature, Map<String, Object> functionInputParameters, boolean async) {
		logger.info("Invoking smart contract: Contract: {}, LinkTrak: {}, Index: {}, Signature: {}, Async: {}", contractUUID, contractMappingUUID, functionMappingIndex, functionSignature, async);

		Map<String, Object> bodyParameters = new HashMap<>();
		bodyParameters.put("index", functionMappingIndex);
		bodyParameters.put("signature", functionSignature);
		bodyParameters.put("params", functionInputParameters);
		bodyParameters.put("async", async);

		JsonNode responseNode = null;

		try {
			responseNode = RESTUtil.doRestPOST(
					MessageFormat.format("{0}/contracts/{1}/smart-contracts/{2}/execute", BASE_URL, contractUUID, contractMappingUUID), getAuthorizationHeader(),
					JSONUtil.toJsonNode(bodyParameters));
		}
		catch(Exception ex) {
			logger.error("Smart contract invokation failed", ex);
		}

		return responseNode;
	}

	private Map<String, String> getAuthorizationHeader(){
		if(this.isRequiresAuth()) {
			Map<String,String> headerParameters = new HashMap<>();
			headerParameters.put("Authorization", String.format("Bearer %s", TraktiJwtManager.getInstance().generateToken(ContextHelper.getMembershipUUID())));
			
			return headerParameters;
		}
		else {
			return null;
		}
	}

}

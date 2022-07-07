package com.exrade.runtime.blockchain;

import com.exrade.runtime.conf.ExConfiguration;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class OrderSmartContract {

	public static final String ZERO_VALUED_UUID = "00000000-0000-0000-0000-000000000000";
	private String contractUuid;
	private String contractMappingUuid = ExConfiguration.getStringProperty("smartContractMapping.orderLinkTrak");

	private TraktiSmartContractApiClient smartContractClient = new TraktiSmartContractApiClient(true, true);

	public OrderSmartContract() {
		this.contractUuid = ZERO_VALUED_UUID;
		// JsonNode responseJson =
		// smartContractClient.getSmartContractMappings(contractUUID);
		// contractMappingUuid = responseJson.get(0).path("uuid").asText();
	}

	/**
	 * "type": "function", "index": 12, "params": {"TRAK_orderId": "",
	 * "TRAK_referenceId": "", "VAR_title": "", "VAR_description": "",
	 * "VAR_grandTotal": "", "TRAK_sender": "", "TRAK_receiver": ""}, "signature":
	 * "createOrder(bytes32,bytes32,string,string,uint256,address,address)"
	 * 
	 * @param referenceId
	 * @param orderId
	 * @param title
	 * @param description
	 * @param sender
	 * @param receiver
	 * @param total
	 * @return
	 */
	public String createOrder(String referenceId, String orderId, String title, String description, String sender,
			String receiver, Double total) {
		/**/

		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_orderId", orderId);
		functionInputParameters.put("TRAK_referenceId", referenceId);
		functionInputParameters.put("VAR_title", title);
		functionInputParameters.put("VAR_description", description);
		functionInputParameters.put("VAR_grandTotal", smartContractClient.convertValue(total));
		functionInputParameters.put("TRAK_sender", sender);
		functionInputParameters.put("TRAK_receiver", receiver);

		smartContractClient.setRequiresAuth(false);
		smartContractClient.setWaitForTransactionCompletion(false);

		String createOrderResponseTx = smartContractClient.executeFunction(contractUuid, contractMappingUuid, 12,
				"createOrder(bytes32,bytes32,string,string,uint256,address,address)", functionInputParameters);

		smartContractClient.setRequiresAuth(true);
		smartContractClient.setWaitForTransactionCompletion(true);

		smartContractClient.getTransactionStatus(createOrderResponseTx, TraktiSmartContractApiClient.MAX_RETRY);

		return createOrderResponseTx;
	}

	/**
	 * "type": "function", "index": 9, "params": {"TRAK_orderId": "", "TRAK_itemId":
	 * "", "VAR_title": "", "VAR_lead": "", "VAR_quantity": "", "VAR_price": "",
	 * "VAR_total": ""}, "signature":
	 * "addItem(bytes32,bytes32,string,uint256,uint256,uint256,uint256)"
	 * 
	 * @param orderID
	 * @param itemID
	 * @param title
	 * @param quantity
	 * @param price
	 * @param total
	 * @param delivery
	 * @return
	 */
	public String addItem(String orderID, String itemID, String title, Integer quantity, Double price, Double total,
			Integer delivery) {

		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_orderId", orderID);
		functionInputParameters.put("TRAK_itemId", itemID);
		functionInputParameters.put("VAR_title", title);
		functionInputParameters.put("VAR_lead", delivery);
		functionInputParameters.put("VAR_quantity", quantity);
		functionInputParameters.put("VAR_price", smartContractClient.convertValue(price));
		functionInputParameters.put("VAR_total", smartContractClient.convertValue(total));

		String tx = smartContractClient.executeFunction(contractUuid, contractMappingUuid, 9,
				"addItem(bytes32,bytes32,string,uint256,uint256,uint256,uint256)", functionInputParameters);

		return tx;
	}

	/**
	 * "type": "function", "index": 8, "params": {"TRAK_orderId": ""}, "signature":
	 * "activateOrder(bytes32)"
	 * 
	 * @param orderID
	 * @return
	 */
	public String activateOrder(String orderID) {

		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_orderId", orderID);

		String tx = smartContractClient.executeFunction(contractUuid, contractMappingUuid, 8, "activateOrder(bytes32)",
				functionInputParameters);

		return tx;
	}

	/**
	 * "type": "function", "index": 10, "params": {"TRAK_orderId": ""}, "signature":
	 * "cancelOrder(bytes32)"
	 * 
	 * @param orderID
	 * @return
	 */
	public String cancelOrder(String orderID) {
		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_orderId", orderID);

		String tx = smartContractClient.executeFunction(contractUuid, contractMappingUuid, 10, "cancelOrder(bytes32)",
				functionInputParameters);

		return tx;
	}

	/**
	 * "type": "function", "index": 11, "params": {"TRAK_orderId": ""}, "signature":
	 * "closeOrder(bytes32)"
	 * 
	 * @param orderID
	 * @return
	 */
	public String closeOrder(String orderID) {
		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_orderId", orderID);

		String tx = smartContractClient.executeFunction(contractUuid, contractMappingUuid, 11, "closeOrder(bytes32)",
				functionInputParameters);

		return tx;
	}

	/**
	 * "type": "function", "index": 13, "params": {"TRAK_itemId": "",
	 * "VAR_delivery": ""}, "signature": "resolveItem(bytes32,uint256)"
	 * 
	 * @param itemID
	 * @param deliveryTime
	 * @return
	 */
	public String closeItem(String itemID, Long deliveryTime) {
		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_itemId", itemID);
		functionInputParameters.put("VAR_delivery", deliveryTime);

		String tx = smartContractClient.executeFunction(contractUuid, contractMappingUuid, 13,
				"resolveItem(bytes32,uint256)", functionInputParameters);

		return tx;
	}

	/**
	 * "type": "function", "index": 3, "params": {"TRAK_orderId": ""}, "signature":
	 * "getOrder(bytes32)"
	 * 
	 * @param orderId
	 * @return
	 */
	public JsonNode getOrder(String orderId) {
		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_orderId", orderId);

		JsonNode response = smartContractClient.callFunction(contractUuid, contractMappingUuid, 3, "getOrder(bytes32)",
				functionInputParameters);

		return response;
	}

	/**
	 * "type": "function", "index": 2, "params": {"TRAK_itemId": ""}, "signature":
	 * "getItem(bytes32)"
	 * 
	 * @param orderItemId
	 * @return
	 */
	public JsonNode getItem(String orderItemId) {
		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_itemId", orderItemId);

		JsonNode response = smartContractClient.callFunction(contractUuid, contractMappingUuid, 2, "getItem(bytes32)",
				functionInputParameters);

		return response;
	}

	/**
	 * "type": "function", "index": 7 "params": {"TRAK_orderId": ""}, "signature":
	 * "orderStatus(bytes32)"
	 * 
	 * @param orderItemId
	 * @return
	 */
	public JsonNode getOrderStatus(String orderId) {
		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_orderId", orderId);

		JsonNode response = smartContractClient.callFunction(contractUuid, contractMappingUuid, 7,
				"orderStatus(bytes32)", functionInputParameters);

		return response;
	}

	/**
	 * "type": "function", "index": 6, "params": {"TRAK_orderId": ""}, "signature":
	 * "orderExist(bytes32)"
	 * 
	 * @param orderId
	 * @return
	 */
	public JsonNode isOrderExist(String orderId) {
		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_orderId", orderId);

		JsonNode response = smartContractClient.callFunction(contractUuid, contractMappingUuid, 6,
				"orderExist(bytes32)", functionInputParameters);

		return response;
	}

	/**
	 * "type": "function", "index": 5, "params": {"TRAK_itemId": ""}, "signature":
	 * "itemStatus(bytes32)"
	 * 
	 * @param orderItemId
	 * @return
	 */
	public JsonNode getItemStatus(String orderItemId) {
		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_itemId", orderItemId);

		JsonNode response = smartContractClient.callFunction(contractUuid, contractMappingUuid, 5,
				"itemStatus(bytes32)", functionInputParameters);

		return response;
	}

	/**
	 * "type": "function", "index": 4, "params": {"TRAK_itemId": ""}, "signature":
	 * "itemExist(bytes32)"
	 * 
	 * @param orderItemId
	 * @return
	 */
	public JsonNode isItemExist(String orderItemId) {
		Map<String, Object> functionInputParameters = new HashMap<>();
		functionInputParameters.put("TRAK_itemId", orderItemId);

		JsonNode response = smartContractClient.callFunction(contractUuid, contractMappingUuid, 4, "itemExist(bytes32)",
				functionInputParameters);

		return response;
	}
	
	
}
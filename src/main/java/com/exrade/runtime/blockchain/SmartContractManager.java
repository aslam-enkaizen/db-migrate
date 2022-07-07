package com.exrade.runtime.blockchain;

public class SmartContractManager {
/*
	public static String deployTimestampingContract(String dealId, String dealTitle) throws IOException, CipherException, InterruptedException, ExecutionException {
		List<Type> inputParameters = new ArrayList<>();
		inputParameters.add(new Utf8String(dealId));
		inputParameters.add(new Utf8String(dealTitle));

		String encodedConstructor = FunctionEncoder.encodeConstructor(inputParameters);
		String tx = SmartContractAdapter.deployContract(ExConfiguration.getStringProperty("blockchain.provider"),
				"keystores/UTC--2017-07-13T15-03-28.016359822Z--6fc60185b777a3459eca58fa1250b0d440368fd7", 
				ExConfiguration.getStringProperty("blockchain.account.password"),
				"smartcontracts/trakti.bytecode", encodedConstructor);
		
		return tx;
	}

	public static void initContracts(String dealId, String template) throws ParseException, IOException, CipherException, InterruptedException, ExecutionException {
		String abi = FileUtils.readFileToString(ResourceUtil.getFile("smartcontracts/SimplePayment.abi"), "UTF-8");
		List<String> encodedFunctions = generateFunctions(dealId, template, abi);
		
		for(String encodedFunction : encodedFunctions) {
			SmartContractAdapter.initContract(ExConfiguration.getStringProperty("blockchain.provider"),
					"keystores/UTC--2017-07-13T15-03-28.016359822Z--6fc60185b777a3459eca58fa1250b0d440368fd7", 
					ExConfiguration.getStringProperty("blockchain.account.password"),
					ExConfiguration.getStringProperty("blockchain.simplePayment.address"),
					encodedFunction);
		}
	}
	
	public static List<String> generateFunctions(String dealId, String template, String abi) throws ParseException{
		List<String> encodedFunctions = new ArrayList<>();
		
		String encodedDealId = dealId.replace("-", ""); // Hashing.sha256().hashBytes(dealId.getBytes()).toString();
		//Type paymentId = buildType("bytes32", encodedDealId);
		ExLogger.get().debug("DealId: {}, PaymentId: {}", dealId, encodedDealId);
		
		JsonNode abiNode = JSONUtil.toJsonNode(abi);
		
		Document doc = Jsoup.parse(template);
		Elements smartContracts = doc.select(".smart-contract");
		
		for(Element smartContractContainer : smartContracts){
			List<Type> types = new ArrayList<>();
			Map<String, String> smartContractDataSet = smartContractContainer.dataset();
			String initMethodName = smartContractDataSet.get("init");
			JsonNode initMethodNode = getMatchedNode(abiNode, initMethodName);
			ExLogger.get().debug("InitMethodName: {}", initMethodNode);
			
			Map<String, String> contractParamValues = extractValues(doc, smartContractContainer);
			contractParamValues.put("paymentId", encodedDealId);
			
			Iterator<JsonNode> methodParameterNodes = initMethodNode.path("inputs").elements();
	        while (methodParameterNodes.hasNext()) {
	        		JsonNode methodParameterNode = methodParameterNodes.next();
	        		ExLogger.get().debug("MethodParameterNode: {}, {}", methodParameterNode.get("name").asText(), methodParameterNode.get("type").asText());
	        		types.add(buildType(methodParameterNode.get("type").asText(), contractParamValues.get(methodParameterNode.get("name").asText())));
	        }
			
			Function function = new Function(
					initMethodName, 
					types, 
	                Collections.<TypeReference<?>>emptyList());
			String encodedFunction = FunctionEncoder.encode(function);
			
			ExLogger.get().debug("EncodedFunction: {}", encodedFunction);
			
			encodedFunctions.add(encodedFunction);
		}
		return encodedFunctions;
	}
	
	public static Map<String, String> extractValues(Document doc, Element smartContractContainer) throws ParseException{
		Map<String, String> contractParamValues = new HashMap<>();
		String variablesData = smartContractContainer.dataset().get("variables");
		JsonNode variablesNode = JSONUtil.toJsonNode(variablesData);
		
		Iterator<Map.Entry<String, JsonNode>> iteratorMails = variablesNode.fields();
        while (iteratorMails.hasNext()) {
        		Map.Entry<String, JsonNode> entry = iteratorMails.next();
        		String contractParamName = entry.getKey();
        		ExLogger.get().debug("VariableName: {}", contractParamName);
        		
        		//JsonNode methodParameterNode = getMatchedNode(initMethodNode.path("inputs"), contractParamName);
        		//ExLogger.get().debug("MethodParameterNode: {}, {}", methodParameterNode.get("name").asText(), methodParameterNode.get("type").asText());
        		
        		String variableType = entry.getValue().get("type").asText();
        		String variableValue = entry.getValue().get("value").asText();
        		
        		if("REFERENCE".equals(variableType)) {
        			variableValue = getValueFromElement(doc.select("#"+variableValue).first());
        		}
        		
        		ExLogger.get().debug("VariableValue: {}", variableValue);
        		contractParamValues.put(contractParamName, variableValue);
        }
        return contractParamValues;
	}
	
	public static Type buildType(String variableType, String variableValue) {
		if("bool".equals(variableType)) {
			return new org.web3j.abi.datatypes.Bool(Boolean.parseBoolean(variableValue));
		}
		if("bytes32".equals(variableType)) {
			return SmartContractAdapter.stringToBytes32(variableValue);
		}
		if("bytes".equals(variableType)) {
			return new org.web3j.abi.datatypes.DynamicBytes(variableValue.getBytes());
		}
		if(variableType.startsWith("uint")) {
			return new org.web3j.abi.datatypes.Uint(new BigInteger(variableValue));
		}
		if(variableType.startsWith("int")) {
			return new org.web3j.abi.datatypes.Int(new BigInteger(variableValue));
		}
		if("address".equals(variableType)) {
			return new org.web3j.abi.datatypes.Address(variableValue);
		}
		if("string".equals(variableType)) {
			return new org.web3j.abi.datatypes.Utf8String(variableValue);
		}
		return null;
	}
	
	private static JsonNode getMatchedNode(JsonNode abiNode, String name) {
		Iterator<JsonNode> iteratorNodes = abiNode.iterator();
        while (iteratorNodes.hasNext()) {
        		JsonNode node = iteratorNodes.next();
        		if(name.equals(node.get("name").asText()))
        			return node;
        }
        return null;
	}
	
	private static String getValueFromElement(Element variable) throws ParseException {
		String value = variable.attr("data-value");
		String dataType = variable.attr("data-type");
				
		if(dataType.equals(DataType.DATE.name())){
			SimpleDateFormat parserSDF=new SimpleDateFormat("MMM dd yyyy");
			value = "" + parserSDF.parse(value).getTime()/1000;
		}
		
		if(dataType.equals(DataType.DATETIME.name())){
			SimpleDateFormat parserSDF=new SimpleDateFormat("MMM dd yyyy HH:mm");
			value = "" + parserSDF.parse(value).getTime()/1000;
		}
		
		if(dataType.equals(DataType.BOOLEAN.name())){
			if("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)) {
				value = Boolean.TRUE.toString();
			}
			else if("false".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)) {
				value = Boolean.FALSE.toString();
			}
		}
			
		return value;
	}
	*/
}

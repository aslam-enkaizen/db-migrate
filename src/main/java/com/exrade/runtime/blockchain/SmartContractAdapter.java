package com.exrade.runtime.blockchain;

//import org.web3j.abi.FunctionEncoder;
//import org.web3j.abi.TypeReference;
//import org.web3j.abi.datatypes.Function;
//import org.web3j.abi.datatypes.Type;
//import org.web3j.abi.datatypes.generated.Bytes32;
//import org.web3j.crypto.CipherException;
//import org.web3j.crypto.Credentials;
//import org.web3j.crypto.RawTransaction;
//import org.web3j.crypto.TransactionEncoder;
//import org.web3j.crypto.WalletUtils;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.core.DefaultBlockParameterName;
//import org.web3j.protocol.core.methods.request.Transaction;
//import org.web3j.protocol.core.methods.response.EthEstimateGas;
//import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
//import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
//import org.web3j.protocol.core.methods.response.EthSendTransaction;
//import org.web3j.protocol.infura.InfuraHttpService;
//import org.web3j.utils.Numeric;


public class SmartContractAdapter {
/*
	public static String initContract() throws IOException, CipherException, InterruptedException, ExecutionException, ParseException {
		// contract name, address
		// init method name
		// abi to java data type builder
		// variable value extraction
		Web3j web3 = Web3j.build(new InfuraHttpService(ExConfiguration.getStringProperty("blockchain.provider")));
		Credentials credentials = WalletUtils.loadCredentials(
				ExConfiguration.getStringProperty("blockchain.account.password"),
		        	getFilePath("keystores/UTC--2017-07-13T15-03-28.016359822Z--6fc60185b777a3459eca58fa1250b0d440368fd7"));
		
		
//		   start_at = datetime.datetime.strptime("11/22/2017 10:40", settings.DEFAULT_DATETIME_FORMAT).strftime("%s")
//        	   end_at = datetime.datetime.strptime("11/22/2017 10:50", settings.DEFAULT_DATETIME_FORMAT).strftime("%s")
//        	   did = datetime.datetime.now().strftime("%s")
//		   transaction_id = self.contract.transact({
//                "from": "0x6fc60185b777a3459eca58fa1250b0d440368fd7",
//                "gas": 4700000
//            }).newPayment(Web3.toBytes(text=did), "0x49832053ce97cc1f06f89d1da59c059a1dae9715", 
//                                     "0x14f1f31d6bd17c02572937d85ff8f5450642ddd0", int(start_at), int(end_at), 60, 200000000000000, 
//                                     600000000000000, 60, 1, 100000000000000)
//		 
		SimpleDateFormat parserSDF=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date startTime = parserSDF.parse("2017/12/24 10:40:00");
		Date endTime = parserSDF.parse("2017/12/24 10:45:00");
		String did = "d7c32f1c147f452196304a4af7112a2b";// "TRK" + new Random().nextInt(10000);
		Function function = new Function(
                "newPayment", 
                Arrays.<Type>asList(stringToBytes32(did), 
                new org.web3j.abi.datatypes.Address("0x49832053ce97cc1f06f89d1da59c059a1dae9715"), 
                new org.web3j.abi.datatypes.Address("0x14f1f31d6bd17c02572937d85ff8f5450642ddd0"), 
                new org.web3j.abi.datatypes.generated.Uint256(startTime.getTime()/1000), 
                new org.web3j.abi.datatypes.generated.Uint256(endTime.getTime()/1000), 
                new org.web3j.abi.datatypes.generated.Uint256(60), 
                new org.web3j.abi.datatypes.generated.Uint256(new BigInteger("200000000000000")), 
                new org.web3j.abi.datatypes.generated.Uint256(new BigInteger("600000000000000")), 
                new org.web3j.abi.datatypes.generated.Uint256(60), 
                new org.web3j.abi.datatypes.generated.Uint256(1), 
                new org.web3j.abi.datatypes.generated.Uint256(new BigInteger("200000000000000"))), 
                Collections.<TypeReference<?>>emptyList());
		String encodedFunction = FunctionEncoder.encode(function);
//		
//		
//	    Transaction transaction = Transaction.createFunctionCallTransaction(
//	    		"0xd3cda913deb6f67967b99d67acdfa1712c293601", new BigInteger("0"), new BigInteger("0"), new BigInteger("0"), "0xd3cda913deb6f67967b99d67acdfa1712c293601", encodedFunction);
//	    
//	    org.web3j.protocol.core.methods.response.EthSendTransaction transactionResponse =
//	             web3.ethSendTransaction(transaction).sendAsync().get();
//
//	    String transactionHash = transactionResponse.getTransactionHash();
	    
//		RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
//				getNonce(web3, "0x6fc60185b777a3459eca58fa1250b0d440368fd7"), new BigInteger("21000000000"), new BigInteger("4700000"), "0xd3cda913deb6f67967b99d67acdfa1712c293601", new BigInteger("0"));
		RawTransaction rawTransaction  = RawTransaction.createTransaction(getNonce(web3, "0x6fc60185b777a3459eca58fa1250b0d440368fd7"),
																				new BigInteger("21000000000"), new BigInteger("4700000"),
																				"0xec0c1d8a039b27f4f6123d33813c3807ef53fe70", encodedFunction); 
//		RawTransaction rawTransaction  = RawTransaction.createContractTransaction(getNonce(web3, "0x6fc60185b777a3459eca58fa1250b0d440368fd7"),
//				new BigInteger("21000000000"), new BigInteger("4700000"),
//				new BigInteger("0"), encodedFunction); 
		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);
		EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
		String transactionHash = ethSendTransaction.getTransactionHash();
		ExLogger.get().info("Transaction: tx = " + transactionHash);
		return transactionHash;
	}
	
	public static String initContract(String serviceUrl, String keyFilePath, String password, String contractAddress, String encodedFunction) throws IOException, CipherException, InterruptedException, ExecutionException, ParseException {
		
		Web3j web3 = Web3j.build(new InfuraHttpService(serviceUrl));
		Credentials credentials = WalletUtils.loadCredentials(
				password,
		        	getFilePath(keyFilePath));
		
//		Function function = new Function(
//				functionName, 
//				types, 
//                Collections.<TypeReference<?>>emptyList());
//		String encodedFunction = FunctionEncoder.encode(function);
		
		RawTransaction rawTransaction  = RawTransaction.createTransaction(getNonce(web3, credentials.getAddress()),
				new BigInteger(ExConfiguration.getStringProperty("blockchain.gasPrice")),
				new BigInteger(ExConfiguration.getStringProperty("blockchain.gasLimit")),
				contractAddress, encodedFunction); 
		
		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);
		EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
		String transactionHash = ethSendTransaction.getTransactionHash();
		ExLogger.get().info("Transaction: tx = " + transactionHash);
		return transactionHash;
	}
	
	public static String deployContract(String serviceUrl, String keyFilePath, String password, String contractFilePath, String encodedConstructor) 
			throws IOException, CipherException, InterruptedException, ExecutionException {
		String contractAddress = null;
		
		Web3j web3 = Web3j.build(new InfuraHttpService(serviceUrl));
		Credentials credentials = WalletUtils.loadCredentials(
				password,
		        	getFilePath(keyFilePath));
		// using a raw transaction
		RawTransaction rawTransaction = RawTransaction.createContractTransaction(
				getNonce(web3, credentials.getAddress()),
				new BigInteger(ExConfiguration.getStringProperty("blockchain.gasPrice")),
				new BigInteger(ExConfiguration.getStringProperty("blockchain.gasLimit")),
				new BigInteger("0"),
				FileUtils.readFileToString(ResourceUtil.getFile(contractFilePath), "UTF-8") + encodedConstructor);
		
		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);
		EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send(); //.sendAsync().get();
		String transactionHash = ethSendTransaction.getTransactionHash();
		ExLogger.get().info("Deploying smart contract: tx = " + transactionHash);
		
		// get contract address
		EthGetTransactionReceipt transactionReceipt =
		             web3.ethGetTransactionReceipt(transactionHash).send();

		if (transactionReceipt.getTransactionReceipt().isPresent()) {
		    contractAddress = transactionReceipt.getResult().getContractAddress();
		    ExLogger.get().info("Deployed smart contract: address = " + contractAddress);
		} else {
			ExLogger.get().warn("Deploying smart contract failed!");
		}
		
		return contractAddress;
	}
	
	private static BigInteger getNonce(Web3j web3, String address) throws InterruptedException, ExecutionException {
		EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
				address, DefaultBlockParameterName.LATEST).sendAsync().get();
		
		BigInteger nonce = ethGetTransactionCount.getTransactionCount();
		return nonce;
	}
	
	private static String getFilePath(String fileName) throws UnsupportedEncodingException {
		URL url = ResourceUtil.class.getResource("/"+fileName);
		String filePath = new File(URLDecoder.decode(url.getPath(), "UTF-8")).getPath();
		return filePath;
	}
	
	public static Bytes32 stringToBytes32(String string) {
        byte[] byteValue = string.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }
	
	private BigInteger estimateGas(Web3j web3, String address, String encodedFunction) throws Exception {
        EthEstimateGas ethEstimateGas = web3.ethEstimateGas(
                Transaction.createEthCallTransaction(address, null, encodedFunction))
                .sendAsync().get();
        // this was coming back as 50,000,000 which is > the block gas limit of 4,712,388
        // see eth.getBlock("latest")
        return ethEstimateGas.getAmountUsed().divide(BigInteger.valueOf(100));
    }
	
//	private static String bytes32ToString(Bytes32 value) {
//		StringUtils.newStringUsAscii(value.getValue());
//
//	}
 
 */
}

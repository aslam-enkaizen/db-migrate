package com.exrade.runtime.payment;


import com.exrade.core.ExLogger;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.runtime.conf.ExConfiguration;
import com.paypal.svcs.services.AdaptiveAccountsService;
import com.paypal.svcs.services.AdaptivePaymentsService;
import com.paypal.svcs.types.aa.AccountIdentifierType;
import com.paypal.svcs.types.aa.GetVerifiedStatusRequest;
import com.paypal.svcs.types.aa.GetVerifiedStatusResponse;
import com.paypal.svcs.types.ap.*;
import com.paypal.svcs.types.common.ErrorData;
import com.paypal.svcs.types.common.RequestEnvelope;
import com.paypal.svcs.types.common.ResponseEnvelope;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PayPalUtil {
	//private static String apiUsername="jason-facilitator_api1.exrade.com";
	private static final Logger logger = ExLogger.get();
	
	public static final String USE_PAYMENT = "USE_PAYMENT";
	public static final String USER_NAME_PROPERTY = "paypal.acct1.UserName";
	public static final String COMPLETED = "COMPLETED";
	public static final String EXPIRED = "EXPIRED";
	public static final String CREATED = "CREATED";
	
	/**
	 * creates a payment from sender to receiver via eXrade, which takes a commision
	 * currently exrade takes 0.15(euro/usd) + 2.7% 
	 * 
	 * @param senderEmail (not used as this would limit user using paypalID different from trakti email
	 * @param receiverEmail
	 * @param amount   minimum = 5
	 * @param url the user is sent to after authorizing payment, e.g. http://www.trakti.com/en/negotiation/4848e-588c-489b/ 
	 * @return paykey
	 */
	public static String createPayment(String senderEmail, String receiverEmail, double amount, String currency, Negotiation negotiation){
		logger.info("Paypal payment: receiver=" + receiverEmail + " " + currency + amount);

		//Properties paypalProperties	= ExConfiguration.getInstance().getProperties();
		String returnUrl=ExConfiguration.getStringProperty("paypal.returnurl");
		String cancelUrl=ExConfiguration.getStringProperty("paypal.cancelurl");
		String apiUserName=ExConfiguration.getStringProperty(USER_NAME_PROPERTY);
		String exradePaypalEmail=ExConfiguration.getStringProperty("paypal.email");

		//todo remove constructor
		RequestEnvelope env = new RequestEnvelope(); //"en_US"

		//Receiver pays exrade fee
		double exradeFee = calculateExradeCharge(amount);
		double receiverAmount = amount - exradeFee;
		
		List<Receiver> receivers = new ArrayList<Receiver>();
		Receiver exradeReceiver = createReceiver(exradePaypalEmail, exradeFee, createExradeInvoiceID(negotiation.getUuid()));
		Receiver finalrec = createReceiver(receiverEmail, receiverAmount, createReceiverInvoiceID(negotiation.getUuid()));
		//To enable chained payment just set : primRec.setPrimary(true);
		receivers.add(exradeReceiver);
		receivers.add(finalrec);
		ReceiverList receiverlist = new ReceiverList(receivers);

		PayRequest payRequest = new PayRequest();
		payRequest.setReceiverList(receiverlist);
		payRequest.setRequestEnvelope(env);

		payRequest.setActionType("PAY");
		payRequest.setCurrencyCode(currency);
		payRequest.setCancelUrl(cancelUrl);
		payRequest.setReturnUrl(returnUrl);
		payRequest.setTrackingId(negotiation.getUuid());
		//payRequest.setFeesPayer(receiverEmail);
		payRequest.setMemo("Payment for the deal: " + negotiation.getTitle());

		try {
			AdaptivePaymentsService adaptivePaymentsService = getAdaptivePaymentsService();

			PayResponse payResponse = adaptivePaymentsService.pay(payRequest,apiUserName);
			logResponse(payResponse.getResponseEnvelope());

			if(payResponse.getError() != null && payResponse.getError().size() > 0){
				logger.warn("Paypal error response: ");
				for(ErrorData error : payResponse.getError()){
					logger.error(error.getErrorId() + error.getMessage());
				}
			}	
			else{
				setPaymentOptions(payResponse.getPayKey(), receiverEmail, receiverAmount, exradeFee, negotiation.getUuid(), negotiation.getTitle());
				return payResponse.getPayKey();
			}
		}
		catch (Exception e){
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	private static void setPaymentOptions(String payKey, String receiverEmail, double receiverAmount, double exradeFee, String negotiationUUID, String negTitle){
		//todo remove constructor
		RequestEnvelope requestEnvelope = new RequestEnvelope();//"en_US"
		SetPaymentOptionsRequest req = new SetPaymentOptionsRequest();
		req.setPayKey(payKey);
		
		DisplayOptions displayOptions = new DisplayOptions();
		displayOptions.setBusinessName("Trakti");
		displayOptions.setHeaderImageUrl("https://deals.trakti.com/static/img/logo.png");
		req.setDisplayOptions(displayOptions);
		
		List<ReceiverOptions> receiverOptionsList = new ArrayList<ReceiverOptions>();
		ReceiverIdentifier exradeReceiver = new ReceiverIdentifier();
		exradeReceiver.setEmail(ExConfiguration.getStringProperty("paypal.email"));
		receiverOptionsList.add(createReceiverOption(exradeReceiver, exradeFee, "Service fee for " + negTitle, createExradeInvoiceID(negotiationUUID)));
		
		ReceiverIdentifier finalrec = new ReceiverIdentifier();
		finalrec.setEmail(receiverEmail);
		receiverOptionsList.add(createReceiverOption(finalrec, receiverAmount, negTitle, createReceiverInvoiceID(negotiationUUID)));
		
		
		req.setReceiverOptions(receiverOptionsList);
		req.setRequestEnvelope(requestEnvelope);
		
		try {
			AdaptivePaymentsService adaptivePaymentsService = getAdaptivePaymentsService();

			SetPaymentOptionsResponse payResponse = adaptivePaymentsService.setPaymentOptions(req);
			logResponse(payResponse.getResponseEnvelope());
			
			if(payResponse.getError() != null && payResponse.getError().size() > 0){
				String errorMsg = "";
				for(ErrorData error : payResponse.getError()){
					errorMsg += error.getErrorId() + " : " + error.getMessage() + "\n";
				}
				logger.error("Paypal error response: " + errorMsg);
			}
		}
		catch (Exception e){
			logger.error(e.getMessage(), e);
		}
		
	}
	
	private static Receiver createReceiver(String email, double amount, String invoiceId){
		Receiver receiver = new Receiver();
		receiver.setAmount(round(amount, 2, BigDecimal.ROUND_HALF_UP));
		receiver.setEmail(email);
		receiver.setInvoiceId(invoiceId);
		return receiver;
	}
	
	private static ReceiverOptions createReceiverOption(ReceiverIdentifier rcv, double amount, String negTitle, String invoiceID){
		ReceiverOptions receiverOptions = new ReceiverOptions(rcv);
		//receiverOptions.setDescription("This is an example description of receiver option");
		
		InvoiceData invoiceData = new InvoiceData();
		List<InvoiceItem> invoiceItemList = new ArrayList<InvoiceItem>();
		InvoiceItem invoiceItem = new InvoiceItem();
		invoiceItem.setIdentifier(invoiceID);
		
		Double itemPrice = round(amount, 2, BigDecimal.ROUND_HALF_UP);
		Integer itemCount = 1;
		invoiceItem.setItemCount(itemCount);
		invoiceItem.setItemPrice(itemPrice);
		invoiceItem.setPrice(itemPrice*itemCount);
		invoiceItem.setName(negTitle);
		invoiceItemList.add(invoiceItem);
		invoiceData.setItem(invoiceItemList);
		receiverOptions.setInvoiceData(invoiceData);
		//receiverOptions.setReferrerCode("RcvRef" + new Random().nextInt());
		
		return receiverOptions;
	}
	
	private static String createExradeInvoiceID(String negotiationUUID){
		return createInvoiceID("XRD", negotiationUUID);
	}
	
	private static String createReceiverInvoiceID(String negotiationUUID){
		return createInvoiceID("RCV", negotiationUUID);
	}
	
	private static String createInvoiceID(String prefix, String negotiationUUID){
		return prefix + "-" + negotiationUUID;
	}
	
	/**
	 * calcualte the standard exrade charge for agiven amount
	 * @param amount
	 * @return
	 */
	public static double calculateExradeCharge(double amount) {
		//TODO support differnet rates for differnt users/account types
		double fixedFee = ExConfiguration.getDoubleProperty("paypal.exradefee.fixed");
		double variableFee = ExConfiguration.getDoubleProperty("paypal.exradefee.variable");
		return fixedFee + (amount * variableFee);
	}

	public static String getPaymentStatus(String paykey){
		return getPaymentDetails(paykey).getStatus(); 
	}
	
	/**
	 * Check if the payment has been completed
	 * @param paykey
	 * @return
	 */
	public static boolean checkPaymentCompleted(String paykey){
		return COMPLETED.equalsIgnoreCase(getPaymentDetails(paykey).getStatus()); 
	}
	
	/**
	 * Chec if the payment has expired , paykeys are only valid for 3 hours
	 * @param paykey
	 * @return
	 */
	public static boolean isKeyExpired(String paykey) {
		return EXPIRED.equalsIgnoreCase(getPaymentDetails(paykey).getStatus());
	}
	
	/**
	 * Create a new paykey based on an expired paykey
	 * @param value
	 * @return
	 */
	public static String refreshIfPaymentKeyExpired(Negotiation negotiation) {
		PaymentDetailsResponse paymentDetailsResponse = getPaymentDetails(negotiation.getPayment().getPaymentKey());
		//be careful to never duplicate a payment if not expired
		if(EXPIRED.equalsIgnoreCase(paymentDetailsResponse.getStatus())){  
			String newPaykey=createPayment(paymentDetailsResponse.getSenderEmail(), 
					paymentDetailsResponse.getPaymentInfoList().getPaymentInfo().get(1).getReceiver().getEmail(),  //the final reciever
					paymentDetailsResponse.getPaymentInfoList().getPaymentInfo().get(0).getReceiver().getAmount() + paymentDetailsResponse.getPaymentInfoList().getPaymentInfo().get(1).getReceiver().getAmount(), //the total amount paid to exrade
					paymentDetailsResponse.getCurrencyCode(),
					negotiation);
			
			return newPaykey;
			
		}
		else{
			//error paykey still valid
			return negotiation.getPayment().getPaymentKey();
		}
	}
	
	public static boolean isUserVerified(String email, String firstName, String lastName){
		RequestEnvelope env = new RequestEnvelope();
		env.setErrorLanguage("en_US");
		GetVerifiedStatusRequest request = new GetVerifiedStatusRequest(env, "NAME");
		request.setFirstName(firstName);
		request.setLastName(lastName);
		
		AccountIdentifierType accountIdentifier = new AccountIdentifierType();
		accountIdentifier.setEmailAddress(email);
		
		request.setAccountIdentifier(accountIdentifier);
		try {
			GetVerifiedStatusResponse response = getAdaptiveAccountsService().getVerifiedStatus(request);
			logResponse(response.getResponseEnvelope());
			if (response != null && response.getAccountStatus() != null) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return false;
	}
	
	private static PaymentDetailsResponse getPaymentDetails(String paykey){
		RequestEnvelope env = new RequestEnvelope();
		env.setErrorLanguage("en_US");
		AdaptivePaymentsService adaptivePaymentsService = getAdaptivePaymentsService();
		PaymentDetailsRequest paymentDetailsRequest=new PaymentDetailsRequest();
		paymentDetailsRequest.setPayKey(paykey);
		paymentDetailsRequest.setRequestEnvelope(env);
		
		try {
			PaymentDetailsResponse response = adaptivePaymentsService.paymentDetails(paymentDetailsRequest, ExConfiguration.getStringProperty(USER_NAME_PROPERTY));
			logResponse(response.getResponseEnvelope());
			
			if(response.getError() != null && response.getError().size() > 0){
				String errorMsg = "";
				for(ErrorData error : response.getError()){
					errorMsg += error.getErrorId() + " : " + error.getMessage() + "\n";
				}
				logger.error("Paypal error response: " + errorMsg);
			}
			return response;
						
		} catch (Exception e){
			logger.error(e.getMessage(), e);
		}
		
		
		return null;
	}
	
	private static AdaptiveAccountsService getAdaptiveAccountsService(){
		Map<String,String> paypalConf = ExConfiguration.getConfiguration("paypal");
		return new AdaptiveAccountsService (paypalConf);
	}
	
	private static AdaptivePaymentsService getAdaptivePaymentsService(){
		Map<String,String> paypalConf = ExConfiguration.getConfiguration("paypal");
		return new AdaptivePaymentsService(paypalConf);
	}
	
	public static double round(double unrounded, int precision, int roundingMode)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}
	
	private static void logResponse(ResponseEnvelope respEnv){
		logger.info(respEnv.getTimestamp() + " - " 
				+ respEnv.getCorrelationId() + " - "
				+ respEnv.getAck());
	}
}

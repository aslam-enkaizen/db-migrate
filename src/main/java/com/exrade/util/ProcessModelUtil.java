package com.exrade.util;

import com.exrade.models.common.Meta.Format;
import com.exrade.models.informationmodel.Attribute;
import com.exrade.models.payment.IPaymentMethod;
import com.exrade.models.payment.PaymentType;
import com.exrade.models.payment.PaypalPaymentMethod;
import com.exrade.models.processmodel.IProcessModel;
import com.exrade.models.processmodel.ProcessAttribute;
import com.exrade.models.processmodel.protocol.ProtocolBehaviour;
import com.exrade.models.processmodel.protocol.Transition;
import com.exrade.models.processmodel.protocol.events.TimeEvent;
import com.exrade.models.userprofile.Profile;
import com.exrade.runtime.engine.StateMachine;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

public class ProcessModelUtil {
	
	public static String PAYMENT_EMAIL = "PAYMENT_EMAIL";

	/***
	 * Returns payment receiver's email address.
	 * This method searches the email address in following order:
	 * 1. "PAYMENT_EMAIL" in process attribute
	 * 2.
	 * 	[BusinessProfile]
	 * 		2.1 paypalMerchantEmail attribute
	 * 		2.2 Profile "OWNER"'s email address
	 * 		2.3 User email address
	 * 	[PersonalProfile]
	 * 		2.1 User email address 
	 * @param stateMachine
	 * @return
	 */
	public static String getPaymentReceiverEmail(StateMachine stateMachine){
		ProcessAttribute paymentEmailAttribute = stateMachine.getNegotiation().getProcessAttribute(PAYMENT_EMAIL);
		
		String paymentReceiverEmail = paymentEmailAttribute != null ? paymentEmailAttribute.getValue() : null;
		if(Strings.isNullOrEmpty(paymentReceiverEmail)){
			Profile profile = (Profile)stateMachine.getUser().getProfile();
			//TODO: support other payment methods than paypal
			if(profile.getPaymentMethods() != null && profile.getPaymentMethods().size() > 0){
				IPaymentMethod paymentMethod = profile.getPaymentMethods().get(0);
				if(paymentMethod.getPaymentType() == PaymentType.PAYPAL){
					paymentReceiverEmail = ((PaypalPaymentMethod) paymentMethod).getEmail();
				}
			}
			//TODO: use profile payment methods
			/*
			if(stateMachine.getUser().getProfile().isBusinessProfile()){
				BusinessProfile businessProfile = (BusinessProfile) stateMachine.getUser().getProfile(); 
				paymentReceiverEmail = "";//businessProfile.getPaypalMerchantEmail();
				
				if (Strings.isNullOrEmpty(paymentReceiverEmail)) {
					IMembershipManager membershipManager = new MembershipManager();
					Membership profileOwnerMembership = membershipManager.getOwnerMembership(stateMachine.getUser().getProfile().getUuid());
					if(profileOwnerMembership != null)
						paymentReceiverEmail = profileOwnerMembership.getEmail();
					else
						paymentReceiverEmail = stateMachine.getUser().getUser().getEmail();
				}
			}
			else
				paymentReceiverEmail = stateMachine.getUser().getUser().getEmail();*/
			/*if(Strings.isNullOrEmpty(paymentReceiverEmail))
				paymentReceiverEmail = stateMachine.getUser().getUser().getEmail();*/
		}
		
		return paymentReceiverEmail;
	}
	
	public static boolean isPaymentAttribute(Attribute processAtr){
		if(processAtr != null && processAtr.getMeta() != null 
				&& processAtr.getMeta().getFormat() != null
				&& processAtr.getMeta().getFormat().equalsIgnoreCase(Format.PAYPAL_PAYKEY)){
			return true;
		}
		return false;
	}
	
	public static List<ProcessAttribute> mergeConfiguredAttributes(IProcessModel processModel, List<ProcessAttribute> configuredAttributes, String language){
		List<ProcessAttribute> mergedAttributes = new ArrayList<>();
		
		for(ProcessAttribute attribute : ProcessAttribute.newInstance(processModel.getProcessAttributes())){
			//TODO enforce setting configurable attributes.
			if(attribute.isConfigurable() && ExCollections.isNotEmpty(configuredAttributes) ){
				for(ProcessAttribute configuredAttribute : configuredAttributes){
					if(attribute.getName().equals(configuredAttribute.getName())){
						configuredAttribute.setLanguage(language);
						mergedAttributes.add(configuredAttribute);
						break;
					}
				}
			}
			else{
				attribute.setLanguage(language);
				mergedAttributes.add(attribute);
			}
		}
		
		return mergedAttributes;
	}
	
	public static String findTransition(TimeEvent timeEvent, ProtocolBehaviour protocolBehaviour) {
		for (Transition transition : protocolBehaviour.getTransition()) {
			if (transition.getTrigger() instanceof TimeEvent && transition.getTrigger().getName().equals(timeEvent.getName()))
				return transition.getName();
		}
		return null;
	}	
}

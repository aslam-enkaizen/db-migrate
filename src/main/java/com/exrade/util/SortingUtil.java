package com.exrade.util;

import com.exrade.models.informationmodel.wrappers.Money;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.models.messaging.NegotiationMessageCreationDateComparable;
import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.INegotiationSummary;
import com.orientechnologies.orient.object.db.OObjectLazyList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortingUtil {

	public static void sortByPublicationDate(List<INegotiationSummary> negotiationSummaries, boolean isDesc) {
		Comparator<INegotiationSummary> pubDateComparator = new Comparator<INegotiationSummary>() {
			public int compare(INegotiationSummary arg0, INegotiationSummary arg1) {
				return arg0.getPublicationTime().compareTo(arg1.getPublicationTime());
			}
		};
		/*if (isDesc)
			Collections.sort(negotiationSummaries, Collections.reverseOrder(pubDateComparator));
		else
			Collections.sort(negotiationSummaries, pubDateComparator);

		return negotiationSummaries;*/
		sort(negotiationSummaries, pubDateComparator, isDesc);
	}

	public static void sortByStartDate(List<INegotiationSummary> negotiationSummaries, boolean isDesc) {
		Comparator<INegotiationSummary> startDateComparator = new Comparator<INegotiationSummary>() {
			public int compare(INegotiationSummary arg0, INegotiationSummary arg1) {
				return arg0.getStartTime().compareTo(arg1.getStartTime());
			}
		};
		/*if (isDesc)
			Collections.sort(negotiationSummaries, Collections.reverseOrder(startDateComparator));
		else
			Collections.sort(negotiationSummaries, startDateComparator);

		return negotiationSummaries;*/
		sort(negotiationSummaries, startDateComparator, isDesc);
	}

	public static void sortByEndDate(List<INegotiationSummary> negotiationSummaries, boolean isDesc) {
		Comparator<INegotiationSummary> endDateComparator = new Comparator<INegotiationSummary>() {
			public int compare(INegotiationSummary arg0, INegotiationSummary arg1) {
				return arg0.getEndTime().compareTo(arg1.getEndTime());
			}
		};
		/*if (isDesc)
			Collections.sort(negotiationSummaries, Collections.reverseOrder(endDateComparator));
		else
			Collections.sort(negotiationSummaries, endDateComparator);

		return negotiationSummaries;*/
		sort(negotiationSummaries, endDateComparator, isDesc);
	}

	/*
	 * sort a list of offers in descending get(0)=highest get(size)=lowest
	 */
	public static void sortByPrice(List<Offer> offers, boolean isDesc) {
		Comparator<Offer> priceComparator = new Comparator<Offer>() {
			public int compare(Offer offer0, Offer offer1) {
				Double offer0Price = InformationModelUtil.getFinalAmount(offer0, InformationModelUtil.getFinalAmountVariableNameFromTemplate(offer0.getTemplate()));
				Double offer1Price = InformationModelUtil.getFinalAmount(offer1, InformationModelUtil.getFinalAmountVariableNameFromTemplate(offer1.getTemplate()));
				return Money.newInstance(offer0Price, null).compareTo(Money.newInstance(offer1Price, null));
			}
		};
		
		sort(offers, priceComparator, isDesc);
	}

	public static List<Offer> castOfferList(List<NegotiationMessage> negMsgs) {
		List<Offer> offerList = new ArrayList<Offer>();
		for (NegotiationMessage msg : negMsgs) {
			offerList.add((Offer) msg);
		}
		return offerList;
	}
	
	public static void sortByMessageCreationTime(List<? extends NegotiationMessage> messages, boolean isDesc) {
		if(!ExCollections.isEmpty(messages) && messages.size() > 1){
			Comparator<NegotiationMessage> comparator = new NegotiationMessageCreationDateComparable();
			if (messages instanceof OObjectLazyList){ //TODO: quickfix for the problem of Java8 Collections.sort for OObjectLazyList. change if orientdb fix the issue in new version.
				for(NegotiationMessage message : messages)
					message.getClass();
			}
			
			if (isDesc){
				Collections.sort(messages, Collections.reverseOrder(comparator));
			}
			else{
				Collections.sort(messages, comparator);
			}
		}
	}

	public static <T> void sort(List<T> data, Comparator<T> comparator, boolean isDesc){
		if(!ExCollections.isEmpty(data) && data.size() > 1){
			if (data instanceof OObjectLazyList){//TODO: quickfix for the problem of Java8 Collections.sort for OObjectLazyList. change if orientdb fix the issue in new version.
				for(T item : data)
					item.getClass();
			}
			if (isDesc){
				Collections.sort(data, Collections.reverseOrder(comparator));
			}
			else{
				Collections.sort(data, comparator);
			}
		}
	}
}

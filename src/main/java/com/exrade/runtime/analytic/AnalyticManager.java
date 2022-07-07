package com.exrade.runtime.analytic;

import com.exrade.models.analytic.*;
import com.exrade.models.common.DataType;
import com.exrade.models.invitations.InvitationStatus;
import com.exrade.models.messaging.MessageStatus;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.NegotiationType;
import com.exrade.models.negotiation.PublishStatus;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.analytic.persistence.AnalyticPersistenceManager;
import com.exrade.runtime.analytic.persistence.NegotiationQueryGroupBy;
import com.exrade.runtime.contact.persistence.ContactQuery;
import com.exrade.runtime.invitation.persistence.InvitationQuery;
import com.exrade.runtime.invitation.persistence.NegotiationInvitationQuery;
import com.exrade.runtime.message.persistence.MessageBoxQuery;
import com.exrade.runtime.message.persistence.MessageBoxQuery.MessageBoxQFilters;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.negotiation.persistence.NegotiationQuery.NegotiationQFilters;
import com.exrade.runtime.negotiation.persistence.NegotiationQuery.NegotiationStageFilter;
import com.exrade.runtime.negotiation.persistence.NegotiationQueryProfiled;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.*;
import com.exrade.runtime.signatures.ISignatureManager;
import com.exrade.runtime.signatures.SignatureManager;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnalyticManager {
	AnalyticPersistenceManager persistenceManager = new AnalyticPersistenceManager();

	public AnalyticResponse getResult(AnalyticRequest request) {
		if (request.getMetrics().contains(Metric.Negotiations)) {
			return negotiations(request);
		}
		if (request.getMetrics().contains(Metric.PurchaseOrders)) {
			return negotiations(request);
		}
		else if (request.getMetrics().contains(Metric.Contacts)) {
			return contacts(request);
		}
		else if (request.getMetrics().contains(Metric.NegotiationInvitations)) {
			return negotiationInvitations(request);
		}
		else if (request.getMetrics().contains(Metric.NegotiationAdmissionRequests)) {
			return negotiationAdmissionRequests(request);
		}
		else if (request.getMetrics().contains(Metric.NegotiationMessages)) {
			return negotiationMessages(request);
		}
		else if (request.getMetrics().contains(Metric.NegotiationSignatures)) {
			return negotiationSignatures(request);
		}

		return AnalyticResponse.create(request);
	}

	public AnalyticResponse negotiations(AnalyticRequest request) {
		AnalyticResponse response = AnalyticResponse.create(request);

		if (request.getMetrics().contains(Metric.Negotiations) || request.getMetrics().contains(Metric.PurchaseOrders)) {
			QueryFilters filters = createCommonNegotiationFilter(request);
			if (request.getDimensions().contains(Dimension.NegotiationRole)) {
				filters.put(NegotiationFilters.OWNED, ContextHelper.getMembership());
				long proposed = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				filters.remove(NegotiationFilters.OWNED);
				filters.put(NegotiationFilters.PARTICIPATED, ContextHelper.getMembership());
				long participated = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				response.getColumns().add(Column.create(Dimension.NegotiationRole.name(), ColumnType.Dimension, DataType.INTEGER));
				response.getColumns().add(Column.create(Metric.Negotiations.name(), ColumnType.Metric, DataType.INTEGER));
				response.getRows().add(Arrays.asList("Proposed", String.format("%d", proposed)));
				response.getRows().add(Arrays.asList("Participated", String.format("%d", proposed)));
				response.getTotals().put(Metric.Negotiations.name(), String.format("%d", proposed + participated));

			} else if (request.getDimensions().contains(Dimension.AdmissionStatus)) {
				filters.put(NegotiationFilters.NEGOTIATION_STAGE, Arrays.asList(NegotiationStageFilter.OPEN));
				long open = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				filters.put(NegotiationFilters.NEGOTIATION_STAGE, Arrays.asList(NegotiationStageFilter.CLOSED));
				long closed = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				response.getColumns().add(Column.create(Dimension.AdmissionStatus.name(), ColumnType.Dimension, DataType.INTEGER));
				response.getColumns().add(Column.create(Metric.Negotiations.name(), ColumnType.Metric, DataType.INTEGER));
				response.getRows().add(Arrays.asList("Open", String.format("%d", open)));
				response.getRows().add(Arrays.asList("Closed", String.format("%d", closed)));
				response.getTotals().put(Metric.Negotiations.name(), String.format("%d", open + closed));

			} else if (request.getDimensions().contains(Dimension.NegotiationStage)) {
				filters.put(NegotiationFilters.NEGOTIATION_STAGE, Arrays.asList(NegotiationStageFilter.OPEN));
				long open = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				filters.put(NegotiationFilters.NEGOTIATION_STAGE, Arrays.asList(NegotiationStageFilter.AGREED));
				long agreed = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				filters.put(NegotiationFilters.NEGOTIATION_STAGE, Arrays.asList(NegotiationStageFilter.NOT_AGREED));
				long notAgreed = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

//				filters.put(NegotiationFilters.NEGOTIATION_STAGE, Arrays.asList(NegotiationStageFilter.CANCELED));
//				long cancelled = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters))
//						.size();

				filters.remove(NegotiationFilters.NEGOTIATION_STAGE);
				filters.put(NegotiationFields.PUBLISH_STATUS, Arrays.asList(PublishStatus.DRAFT));
				long draft = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				response.getColumns().add(Column.create(Dimension.NegotiationStage.name(), ColumnType.Dimension, DataType.INTEGER));
				response.getColumns().add(Column.create(Metric.Negotiations.name(), ColumnType.Metric, DataType.INTEGER));
				response.getRows().add(Arrays.asList("Open", String.format("%d", open)));
				response.getRows().add(Arrays.asList("Agreed", String.format("%d", agreed)));
				response.getRows().add(Arrays.asList("NotAgreed", String.format("%d", notAgreed)));
				//response.getRows().add(Arrays.asList("Cancelled", String.format("%d", cancelled)));
				response.getRows().add(Arrays.asList("Draft", String.format("%d", draft)));
				response.getRows().add(Arrays.asList("Closed", String.format("%d", agreed + notAgreed)));
				response.getTotals().put(Metric.Negotiations.name(), String.format("%d", open + agreed + notAgreed + draft));

			} else if (request.getDimensions().contains(Dimension.NegotiationCategory)) {
				filters.put(NegotiationFilters.SYSTEM_TAGS, Arrays.asList("buy"));
				long buy = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				filters.put(NegotiationFilters.SYSTEM_TAGS, Arrays.asList("sell"));
				long sell = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				filters.put(NegotiationFilters.SYSTEM_TAGS, Arrays.asList("cooperate"));
				long cooperate = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

				response.getColumns().add(Column.create(Dimension.NegotiationCategory.name(), ColumnType.Dimension, DataType.INTEGER));
				response.getColumns().add(Column.create(Metric.Negotiations.name(), ColumnType.Metric, DataType.INTEGER));
				response.getRows().add(Arrays.asList("Buy", String.format("%d", buy)));
				response.getRows().add(Arrays.asList("Sell", String.format("%d", sell)));
				response.getRows().add(Arrays.asList("Cooperate", String.format("%d", cooperate)));
				response.getTotals().put(Metric.Negotiations.name(), String.format("%d", buy + sell + cooperate));

			} else if (request.getDimensions().contains(Dimension.Bundle)) {
				buildGroupByResponse(response, Metric.Negotiations, Dimension.Bundle, "bundle.name", filters);

			}
			else if (request.getDimensions().contains(Dimension.ProcessModel)) {
				buildGroupByResponse(response, Metric.Negotiations, Dimension.ProcessModel, "processModel.name", filters);

			}
			else if (request.getDimensions().contains(Dimension.NegotiationLanguage)) {
				buildGroupByResponse(response, Metric.Negotiations, Dimension.NegotiationLanguage, NegotiationFields.LANGUAGE, filters);

			} else if (request.getDimensions().contains(Dimension.Day)) {
				buildGroupByResponse(response, Metric.Negotiations, Dimension.Day, NegotiationFields.PUBLICATION_DATE, filters);

			} else {

			}
		}

		return response;
	}

	public AnalyticResponse agreedNegotiations(AnalyticRequest request) {
		QueryFilters filters = createCommonNegotiationFilter(request);
		if (request.getDimensions().contains(Dimension.NegotiationRole)) {

			filters.put(NegotiationFilters.OWNED, ContextHelper.getMembership());
			long owned = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

			filters.remove(NegotiationFilters.OWNED);
			filters.put(NegotiationFilters.PARTICIPATED, ContextHelper.getMembership().getId());
			long participated = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));
		}
		return null;
	}

	public AnalyticResponse activeNegotiations(AnalyticRequest request) {
		QueryFilters filters = createCommonNegotiationFilter(request);
		if (request.getDimensions().contains(Dimension.NegotiationRole)) {

			filters.put(NegotiationFilters.OWNED, ContextHelper.getMembership());
			long owned = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

			filters.remove(NegotiationFilters.OWNED);
			filters.put(NegotiationFilters.PARTICIPATED, ContextHelper.getMembership().getId());
			long participated = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));
		}
		return null;
	}

	public AnalyticResponse closedNegotiations(AnalyticRequest request) {
		QueryFilters filters = createCommonNegotiationFilter(request);
		if (request.getDimensions().contains(Dimension.NegotiationRole)) {

			filters.put(NegotiationFilters.OWNED, ContextHelper.getMembership());
			long owned = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));

			filters.remove(NegotiationFilters.OWNED);
			filters.put(NegotiationFilters.PARTICIPATED, ContextHelper.getMembership().getId());
			long participated = persistenceManager.getCount(new NegotiationQueryProfiled().createQuery(filters));
		} else if (request.getDimensions().contains(Dimension.NegotiationStage)) {

		}
		return null;
	}

	public AnalyticResponse workgroups(AnalyticRequest request) {
		AnalyticResponse response = AnalyticResponse.create(request);

		if (request.getMetrics().contains(Metric.WorkGroups)) {

		}

		return response;
	}

	public AnalyticResponse contacts(AnalyticRequest request) {
		AnalyticResponse response = AnalyticResponse.create(request);
		QueryFilters filters = QueryFilters.create(QueryParameters.PER_PAGE, Integer.MAX_VALUE);

		if (request.getMetrics().contains(Metric.Contacts)) {
			long count = persistenceManager.getCount(new ContactQuery().createQuery(filters));
			response.getColumns().add(Column.create(Metric.Contacts.name(), ColumnType.Metric, DataType.INTEGER));
			response.getRows().add(Arrays.asList(String.format("%d", count)));
			response.getTotals().put(Metric.Contacts.name(), String.format("%d", count));
		}

		return response;
	}

	public AnalyticResponse negotiationInvitations(AnalyticRequest request) {
		AnalyticResponse response = AnalyticResponse.create(request);
		QueryFilters filters = QueryFilters.create(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
		filters.put(NegotiationInvitationFilters.INVITATION_INBOX,InvitationQuery.INCOMING);
		filters.put(NegotiationInvitationFields.INVITATION_STATUS, InvitationStatus.PENDING.toString());
		if (request.getMetrics().contains(Metric.NegotiationInvitations)) {
			long count = persistenceManager.getCount(new NegotiationInvitationQuery().createQuery(filters));
			response.getColumns().add(Column.create(Metric.NegotiationInvitations.name(), ColumnType.Metric, DataType.INTEGER));
			response.getRows().add(Arrays.asList(String.format("%d", count)));
			response.getTotals().put(Metric.NegotiationInvitations.name(), String.format("%d", count));
		}

		return response;
	}

	public AnalyticResponse negotiationAdmissionRequests(AnalyticRequest request) {
		AnalyticResponse response = AnalyticResponse.create(request);

		if (request.getMetrics().contains(Metric.NegotiationAdmissionRequests)) {
			long count = 0;
			INegotiationManager negotiationManager = new NegotiationManager();
			if(!Strings.isNullOrEmpty(request.getObjectID())){
				count = negotiationManager.getAdmissionRequests(ContextHelper.getMembershipUUID(), request.getObjectID(), MessageStatus.PENDING.toString()).size();
			}
			else{
				QueryFilters filters = createCommonNegotiationFilter(request);
				filters.put(NegotiationFilters.NEGOTIATION_STAGE, Arrays.asList(NegotiationStageFilter.OPEN));
				List<Negotiation> negotiations = persistenceManager.listObjects(new NegotiationQueryProfiled().createQuery(filters));
				for(Negotiation negotiation : negotiations){
					count += negotiationManager.getAdmissionRequests(ContextHelper.getMembershipUUID(), negotiation.getUuid(), MessageStatus.PENDING.toString()).size();
				}
			}

			response.getColumns().add(Column.create(Metric.NegotiationAdmissionRequests.name(), ColumnType.Metric, DataType.INTEGER));
			response.getRows().add(Arrays.asList(String.format("%d", count)));
			response.getTotals().put(Metric.NegotiationAdmissionRequests.name(), String.format("%d", count));
		}

		return response;
	}

	public AnalyticResponse negotiationSignatures(AnalyticRequest request) {
		AnalyticResponse response = AnalyticResponse.create(request);

		if (request.getMetrics().contains(Metric.NegotiationSignatures)) {
			long count = 0;
			ISignatureManager signatureManager = new SignatureManager();
			if(!Strings.isNullOrEmpty(request.getObjectID()) && signatureManager.isSignaturePendingForRequestor(request.getObjectID())){
				count = 1;
			}
			else{
				QueryFilters filters = createCommonNegotiationFilter(request);
				filters.put(NegotiationFilters.NEGOTIATION_STAGE, Arrays.asList(NegotiationStageFilter.AGREED));
				List<Negotiation> negotiations = persistenceManager.listObjects(new NegotiationQueryProfiled().createQuery(filters));
				for(Negotiation negotiation : negotiations){
					if(signatureManager.isSignaturePendingForRequestor(negotiation.getUuid()))
						count += 1;
				}
			}

			response.getColumns().add(Column.create(Metric.NegotiationSignatures.name(), ColumnType.Metric, DataType.INTEGER));
			response.getRows().add(Arrays.asList(String.format("%d", count)));
			response.getTotals().put(Metric.NegotiationSignatures.name(), String.format("%d", count));
		}

		return response;
	}

	public AnalyticResponse negotiationMessages(AnalyticRequest request) {
		AnalyticResponse response = AnalyticResponse.create(request);
		QueryFilters filters = QueryFilters.create(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
		filters.put(MessageBoxQFilters.RECEIVED, true);
		filters.put(MessageBoxQFilters.NEGOTIATION_UUID, request.getObjectID());

		if (request.getMetrics().contains(Metric.NegotiationMessages)) {
			if (request.getDimensions().contains(Dimension.MessageType)) {

				filters.put(MessageFields.MESSAGE_TYPE, "Offer");
				long offers = persistenceManager.getCount(new MessageBoxQuery().createQuery(filters));
				filters.put(MessageFields.MESSAGE_TYPE, "CounterOffer");
				long counterOffers = persistenceManager.getCount(new MessageBoxQuery().createQuery(filters));

				response.getColumns().add(Column.create(Dimension.MessageType.name(), ColumnType.Dimension, DataType.INTEGER));
				response.getColumns().add(Column.create(Metric.NegotiationMessages.name(), ColumnType.Metric, DataType.INTEGER));
				response.getRows().add(Arrays.asList("Offer", String.format("%d", offers)));
				response.getRows().add(Arrays.asList("CounterOffer", String.format("%d", counterOffers)));
				response.getTotals().put(Metric.NegotiationMessages.name(), String.format("%d", offers + counterOffers));
			}
		}

		return response;
	}

	private QueryFilters createCommonNegotiationFilter(AnalyticRequest request) {
		QueryFilters filters = QueryFilters.create(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
		filters.put(NegotiationFilters.INVOLVED, ContextHelper.getMembershipUUID());
		filters.put(RestParameters.CREATION_DATE, request.getEndDate());
		filters.put(NegotiationQFilters.CREATED_AFTER_INCLUSIVE, request.getStartDate());
		if(request.getMetrics().contains(Metric.PurchaseOrders))
			filters.put(NegotiationFields.NEGOTIATION_TYPE, NegotiationType.ORDER);
		return filters;
	}

	private void buildGroupByResponse(AnalyticResponse response, Metric metric, Dimension dimension, String field, QueryFilters filters){
		if(dimension == Dimension.Day)
			filters.put(QueryParameters.FIELD, field + ".format('yyyy-MM-dd')");
		else
			filters.put(QueryParameters.FIELD, field);
		Map<Object, Long> result = persistenceManager.listGroupedCounts(new NegotiationQueryGroupBy(), filters);

		response.getColumns().add(Column.create(dimension.name(), ColumnType.Dimension, DataType.TEXT));
		response.getColumns().add(Column.create(metric.name(), ColumnType.Metric, DataType.INTEGER));

		long total = 0;
		for(Object key : result.keySet()){
			total += result.get(key);
			response.getRows().add(Arrays.asList(key.toString(), String.format("%d", result.get(key))));
		}
		response.getTotals().put(metric.name(), String.format("%d", total));
	}

	private void buildGroupByResponse(AnalyticResponse response, Metric metric, Dimension dimension, Map<String, Long> result){

		response.getColumns().add(Column.create(dimension.name(), ColumnType.Dimension, DataType.TEXT));
		response.getColumns().add(Column.create(metric.name(), ColumnType.Metric, DataType.INTEGER));

		long total = 0;
		for(String key : result.keySet()){
			total += result.get(key);
			response.getRows().add(Arrays.asList(key, String.format("%d", result.get(key))));
		}
		response.getTotals().put(metric.name(), String.format("%d", total));
	}
}

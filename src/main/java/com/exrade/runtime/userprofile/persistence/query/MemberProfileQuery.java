package com.exrade.runtime.userprofile.persistence.query;

import com.exrade.models.contact.Contact;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.contact.persistence.ContactPersistenceManager.ContactQFilters;
import com.exrade.runtime.rest.RestParameters.MembershipFields;
import com.exrade.runtime.rest.RestParameters.MembershipFilters;
import com.exrade.runtime.rest.RestParameters.NegotiationFields;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class MemberProfileQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {


		String select =  buildSelect(iFilters);

		String where =  buildWhere(iFilters);

		return "select from " + select +" where 1 = 1 "+where;

	}

	private String buildSelect(QueryFilters iFilters){
		String select =  "";
		select += Membership.class.getSimpleName();
		return select;
	}

	private String buildWhere(QueryFilters filters){
		String where =  "";

		if (filters.containsKey(QueryParameters.UUID)){
			where += andEq(QueryParameters.UUID, filters.get(QueryParameters.UUID));
		}

		if (filters.containsKey(MembershipFilters.PROFILE)){
			where += andEq(MembershipFilters.PROFILE, filters.get(MembershipFilters.PROFILE));
		}

		if (filters.containsKey(MembershipFilters.EMAIL)){
			where += andEq(MembershipFilters.EMAIL,filters.get(MembershipFilters.EMAIL));
		}

		if (filters.containsKey(MembershipFilters.PHONE)){
			where += andEq(MembershipFilters.PHONE,filters.get(MembershipFilters.PHONE));
		}

		if (filters.containsKey(MembershipFilters.USER_NAME)){
			where += andEq(MembershipFilters.USER_NAME,filters.get(MembershipFilters.USER_NAME));
		}

		if (filters.containsKey(MembershipFilters.USER)){
			where += andEq(MembershipFilters.USER, filters.get(MembershipFilters.USER));
		}
		if (filters.containsKey(MembershipFilters.NOT_IN_USE)){
			where += andNotEq(QueryParameters.UUID, getActor().getIdentifier());
		}
		if (filters.containsKey(MembershipFilters.ROLENAME)){
			//where += andEq(MembershipFilters.ROLENAME,filters.get(MembershipFilters.ROLENAME));
			List<String> roles = Lists.newArrayList(Splitter.on(",").trimResults()
				       .omitEmptyStrings().split((String) filters.get(MembershipFilters.ROLENAME)));
			where += andIn(MembershipFilters.ROLENAME, roles);
		}
		if (filters.isNotNull(QueryParameters.KEYWORDS)){
			List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
				       .omitEmptyStrings().split((String) filters.get(QueryParameters.KEYWORDS)));
			for (String keyword : keywords) {
				where += and(contains(QueryKeywords.ANY + ".toLowerCase()", keyword.toLowerCase()));
			}
		}
		if (filters.containsKey(MembershipFilters.NOT_IN_CONTACT)){
			eq(ContactQFilters.OWNER_PROFILE, getActor().getProfile().getId());
			String contactQuery = "select linkedMembership.uuid from " + Contact.class.getSimpleName() + " where 1 = 1 ";
			contactQuery += andEq(ContactQFilters.OWNER_PROFILE, PlainSql.get(getActor().getProfile().getId()));
			where += andNotIn(QueryParameters.UUID, PlainSql.get("("+contactQuery+")"));
		}
		if (filters.containsKey(MembershipFilters.STATUS)){
			where += andEq(MembershipFilters.STATUS,filters.get(MembershipFilters.STATUS));
		}

		if (filters.containsKey(MembershipFields.EXPIRATION_DATE)){
			where += andEq(MembershipFields.EXPIRATION_DATE + ".format('" + dateFormatPattern + "')",
					PlainSql.get("date('" + dateFormatter.format(filters.get(MembershipFields.EXPIRATION_DATE)) + "', '"	+ dateFormatPattern + "')") );
		}

		if (filters.containsKey(MemberProfileQFilters.NOT_LOGGEDIN_AFTER)){
			where += and(condition("user.lastLogin", filters.get(MemberProfileQFilters.NOT_LOGGEDIN_AFTER), Operator.LT));
		}

		if (filters.containsKey(MembershipFilters.AGREEMENT_SIGNER)){
			where += andEq(MembershipFilters.AGREEMENT_SIGNER, filters.get(MembershipFilters.AGREEMENT_SIGNER));
		}

		if (filters.containsKey(MembershipFilters.IS_ACTIVE)){
			if(filters.isTrue(MembershipFilters.IS_ACTIVE))
				where += and("(status='ACTIVE' and user.accountStatus='ACTIVE' and (expirationDate is null or expirationDate > sysdate() ))");
			else
				where += and("(status<>'ACTIVE' or user.accountStatus<>'ACTIVE' or (expirationDate is not null and expirationDate < sysdate() ))");
		}

		if (filters.containsKey(MemberProfileQFilters.PUBLISHED_NEGOTIATION_BEFORE) || filters.containsKey(MemberProfileQFilters.PUBLISHED_NEGOTIATION_AFTER)){
			String publishedBefore = "";
			String publishedAfter = "";

			if(filters.containsKey(MemberProfileQFilters.PUBLISHED_NEGOTIATION_BEFORE))
				publishedBefore = and(condition(NegotiationFields.PUBLICATION_DATE, filters.get(MemberProfileQFilters.PUBLISHED_NEGOTIATION_BEFORE),Operator.LT));
			if(filters.containsKey(MemberProfileQFilters.PUBLISHED_NEGOTIATION_AFTER))
				publishedBefore = and(condition(NegotiationFields.PUBLICATION_DATE, filters.get(MemberProfileQFilters.PUBLISHED_NEGOTIATION_AFTER),Operator.GT));

			where += and("uuid not in "
					+ "(select membership.uuid from "
					+ "(select flatten(negotiators) from negotiation where 1 = 1 " + publishedBefore + publishedAfter
					+ " and negotiators contains (exRoles contains (name = 'negotiation.owner' ))))");
		}

		if (filters.containsKey(MemberProfileQFilters.PROFILE_TRIAL_EXPIRATION_DATE)){
			where += andEq("profile.planSubscription.trialEndDate.format('" + dateFormatPattern + "')",
					PlainSql.get("date('" + dateFormatter.format(filters.get(MemberProfileQFilters.PROFILE_TRIAL_EXPIRATION_DATE)) + "', '"	+ dateFormatPattern + "')") );
		}

		if (filters.containsKey(MemberProfileQFilters.PROFILE_PAYMENT_NOT_SET)){
			where += andIsNull("profile.planSubscription.externalSubscriptionID");
		}


		// by default return non-guest memberships
		if (filters.isTrue(MembershipFilters.GUEST_ONLY)){
			where += andEq(MembershipFields.GUEST, true);
		}
		else if(!filters.isTrue(MembershipFilters.INCLUDE_GUEST)) {
			where += and("(NOT (guest=true))");
		}

//		if (filters.containsKey(MemberProfileQFilters.JOINED_NEGOTIATION_BEFORE) || filters.containsKey(MemberProfileQFilters.JOINED_NEGOTIATION_AFTER)){
//			String joinedBefore = "";
//			String joinedAfter = "";
//
//			if(filters.containsKey(MemberProfileQFilters.JOINED_NEGOTIATION_BEFORE))
//				joinedBefore = and(condition(NegotiationFields.PUBLICATION_DATE, filters.get(MemberProfileQFilters.JOINED_NEGOTIATION_BEFORE),Operator.LT));
//			if(filters.containsKey(MemberProfileQFilters.JOINED_NEGOTIATION_AFTER))
//				joinedBefore = and(condition(NegotiationFields.PUBLICATION_DATE, filters.get(MemberProfileQFilters.JOINED_NEGOTIATION_AFTER),Operator.GT));
//
//			where += and("uuid not in "
//					+ "(select membership.uuid from "
//					+ "(select flatten(negotiators) from negotiation where 1 = 1 " + joinedBefore + joinedAfter
//					+ " and negotiators traverse (exRoles[name] = 'negotiation.owner’ )))");
//		}
		return where;
	}


	public static final class MemberProfileQFilters {
		public static final String ADMIN = "admin";
		public static final String PUBLISHED_NEGOTIATION = "publishedNegotiation";
		public static final String NOT_PUBLISHED_NEGOTIATION = "notPublishedNegotiation";
		public static final String PUBLISHED_NEGOTIATION_BEFORE = "publishedNegotiationBefore";
		public static final String PUBLISHED_NEGOTIATION_AFTER = "publishedNegotiationAfter";
		public static final String JOINED_NEGOTIATION_BEFORE = "joinedNegotiationBefore";
		public static final String JOINED_NEGOTIATION_AFTER = "joinedNegotiationAfter";
		public static final String NOT_LOGGEDIN_AFTER = "notLoggedInAfter";
		public static final String PROFILE_TRIAL_EXPIRATION_DATE = "profileTrialExpirationDate";
		public static final String PROFILE_PAYMENT_NOT_SET = "profilePaymentNotSet";
	}

}

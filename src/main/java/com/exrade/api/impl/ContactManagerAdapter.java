package com.exrade.api.impl;

import com.exrade.api.ContactAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.contact.Contact;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.contact.ContactManager;
import com.exrade.runtime.contact.IContactManager;
import com.exrade.runtime.contact.persistence.ContactPersistenceManager.ContactQFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;

public class ContactManagerAdapter implements ContactAPI {
	
	private IContactManager manager = new ContactManager();

	@Override
	public Contact addContact(ExRequestEnvelope request, Contact iContact, String iLinkedMembershipIdentifier) {
		ContextHelper.initContext(request);
		
		if(!Strings.isNullOrEmpty(iLinkedMembershipIdentifier)){
			IMembershipManager membershipManager = new MembershipManager();
			Membership membership = membershipManager.findByUUID(iLinkedMembershipIdentifier, true);
			iContact.setLinkedMembership(membership);
		}
		return manager.addContact(iContact);
	}

	@Override
	public void removeContact(ExRequestEnvelope request, String iContactUUID) {
		ContextHelper.initContext(request);
		manager.removeContact(iContactUUID);
	}

	@Override
	public void updateContact(ExRequestEnvelope request, Contact iContact) {
		ContextHelper.initContext(request);
		manager.updateContact(iContact);
	}

	@Override
	public void updateTags(ExRequestEnvelope request, String iContactUUID, List<String> tags) {
		ContextHelper.initContext(request);
		manager.updateTags(iContactUUID, tags);
	}

	@Override
	public List<Contact> listContacts(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putIfNotEmpty(ContactQFilters.TAGS, iFilters.get(ContactQFilters.TAGS));
		filters.putIfNotEmpty(RestParameters.KEYWORDS, iFilters.get(RestParameters.KEYWORDS));
		filters.putIfNotEmpty(QueryParameters.SORT, iFilters.get(QueryParameters.SORT));
		filters.putIfNotEmpty(ContactQFilters.LINKED_MEMBERSHIP, iFilters.get(ContactQFilters.LINKED_MEMBERSHIP));
		filters.putIfNotEmpty(ContactQFilters.HAS_LINKED_MEMBERSHIP, iFilters.get(ContactQFilters.HAS_LINKED_MEMBERSHIP));
		filters.putIfNotEmpty(ContactQFilters.EXTERNAL_ID, iFilters.get(ContactQFilters.EXTERNAL_ID));
		
		if (filters.isNullOrEmpty(QueryParameters.SORT)){
			filters.put(QueryParameters.SORT,"name");
		}
		
		return manager.listContacts(filters);
	}

	@Override
	public Contact getContactByUUID(ExRequestEnvelope request, String iContactUUID) {
		ContextHelper.initContext(request);
		return manager.getContactByUUID(iContactUUID);
	}

	@Override
	public Contact getContactByEmail(ExRequestEnvelope request, String iEmail) {
		ContextHelper.initContext(request);
		return manager.getContactByEmail(iEmail);
	}

	@Override
	public List<SearchResultSummary> listSearchResultSummary(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		return manager.listSearchResultSummary();
	}

}

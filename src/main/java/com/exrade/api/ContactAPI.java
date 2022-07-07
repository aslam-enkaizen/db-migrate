package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.contact.Contact;
import com.exrade.platform.persistence.SearchResultSummary;

import java.util.List;
import java.util.Map;

public interface ContactAPI {

	Contact addContact(ExRequestEnvelope request, Contact iContact, String iLinkedMembershipIdentifier);
	
	void removeContact(ExRequestEnvelope request, String iContactUUID);
	
	void updateContact(ExRequestEnvelope request, Contact iContact);
	
	void updateTags(ExRequestEnvelope request, String iContactUUID, List<String> tags);
	
	List<Contact> listContacts(ExRequestEnvelope request, Map<String, String> iFilters);
	
	List<SearchResultSummary> listSearchResultSummary(ExRequestEnvelope request, Map<String, String> iFilters);
	
	Contact getContactByUUID(ExRequestEnvelope request, String iContactUUID);
	
	Contact getContactByEmail(ExRequestEnvelope request, String iEmail);
}

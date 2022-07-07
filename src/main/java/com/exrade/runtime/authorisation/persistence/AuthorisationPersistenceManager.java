package com.exrade.runtime.authorisation.persistence;

import com.exrade.platform.persistence.PersistentManager;

public class AuthorisationPersistenceManager extends PersistentManager {

	/*public Contact findByEmail(String iEmail) {
		QueryFilters filters = QueryFilters.create(ContactQFilters.EMAIL, iEmail);
		IQuery nquery = new ContactQuery().createQuery(filters);
		Contact contact = readObject(nquery);
		return contact;
	}
	
	public Contact findByUUID(String iUUID) {
		QueryFilters filters = QueryFilters.create(QueryParameters.UUID,iUUID);
		IQuery nquery = new ContactQuery().createQuery(filters);
		Contact contact = readObject(nquery);
		return contact;
	}*/
	
	public static class AuthorisationRequestQFilters{
	}
	
}

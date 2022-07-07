package com.exrade.runtime.template.persistence;

import com.exrade.platform.persistence.PersistentManager;

public class TemplatePersistenceManager extends PersistentManager {

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
	
	public static class TemplateQFilters{
		public final static String NAME = "name";
		public final static String TEMPLATE_TYPE = "templateType";
		public final static String OWNER_PROFILE = "ownerProfile";
		public final static String OWNER_MEMBERSHIP = "ownerMembership";
	}
	
}

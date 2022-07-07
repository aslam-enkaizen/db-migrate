package com.exrade.runtime.reminder.persistence;

import com.exrade.models.calendar.Event;
import com.exrade.models.userprofile.Actor;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.security.NegotiationRole;
import com.exrade.platform.persistence.ConnectionManager;
import com.exrade.platform.persistence.IConnectionManager;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.IQuery;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.rest.RestParameters;
import com.google.common.collect.Range;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.*;

/**
 * The bundle manager allows the storage and retrieval of bundles
 *
 * @author carlo.polisini
 *
 */
public class ReminderPersistentManager extends PersistentManager {

	public ReminderPersistentManager() {
		this(ConnectionManager.getInstance());
	}

	public ReminderPersistentManager(IConnectionManager persistenceManager) {
	}

	public List<Event> listReminders(QueryFilters filters,Negotiator iNegotiator) {
		IQuery query = new ReminderQuery().createQuery(filters);
		List<ODocument> result = listObjects(query);

		Date lowerDate = (Date) filters.get(ReminderQFilters.FROM);
		Date upperDate = (Date) filters.get(ReminderQFilters.TO);
		Range<Date> rangeEvents = getDateRange(lowerDate, upperDate);

		List<Event> reminders = parseReminders(iNegotiator, rangeEvents, result);

		// TODO parsing and sorting could be semplified if startDate,endDate and publicationDate
		// will become time events objects
		Collections.sort(reminders);

		return reminders;
	}

	private List<Event> parseReminders(Negotiator iNegotiator, Range<Date> rangeEvents,
			List<ODocument> result) {
		List<Event> reminders = new ArrayList<>();

		for (ODocument oDocument : result) {

			String uuid = oDocument.field("uuid");
			String title = oDocument.field("title");
			boolean ownedNegotiation = false;

			if(oDocument.containsField("owner")){
				ORecordId owner = oDocument.field("owner",ORID.class);
				ownedNegotiation = owner.equals(new ORecordId(iNegotiator.getId()));
			}
			else if(oDocument.containsField("negotiators")){
				//List<ORecordId> negotiators = oDocument.field("negotiators");
				List<Object> negotiators = oDocument.field("negotiators");

				if (!negotiators.isEmpty()) {
					//check instance of the data
					if (negotiators.get(0) instanceof ODocument)
					for (Object actorDocument : negotiators) {
						Actor actor = (Actor) connectionManager.getObjectConnection().getUserObjectByRecord((ODocument)actorDocument, "*:-1");
						if (iNegotiator.getIdentifier().equals(actor.getMembership().getIdentifier()) && Security.hasRole(actor.getRoles(), NegotiationRole.OWNER)) {
							ownedNegotiation = true;
							break;
						}
					}
					else if (negotiators.get(0) instanceof ORecordId)
					for (Object actorDocument : negotiators) {
						Actor actor = (Actor) connectionManager.getObjectConnection().getUserObjectByRecord((ORecordId)actorDocument, "*:-1");
						if (iNegotiator.getIdentifier().equals(actor.getMembership().getIdentifier()) && Security.hasRole(actor.getRoles(), NegotiationRole.OWNER)) {
							ownedNegotiation = true;
							break;
						}
					}
				}
			}

			Date publicationDate = oDocument.field("publicationDate");
			if (rangeEvents.contains(publicationDate)){
				Event reminder = new Event(publicationDate, title + "- Negotiation publication",null, uuid,ownedNegotiation);
				reminders.add(reminder );
			}
			Date startDate = oDocument.field("startDate");
			if (rangeEvents.contains(startDate)){
				Event reminder = new Event(startDate, title + " - Negotiation start",null, uuid,ownedNegotiation);
				reminders.add(reminder );
			}
			List<ODocument> modelTimeEvents = null;
			if (ownedNegotiation){
				modelTimeEvents = oDocument.field(RestParameters.NegotiationSummaryFields.OWNER_TIME_EVENTS);
			}
			else {
				modelTimeEvents = oDocument.field(RestParameters.NegotiationSummaryFields.PARTICIPANT_TIME_EVENTS);
			}

			if (modelTimeEvents != null){
				List<Event> modelReminders = buildRemindersFromTimeEvents(modelTimeEvents,rangeEvents, uuid,title,ownedNegotiation);
				reminders.addAll(modelReminders);
			}

			Date endDate = oDocument.field("endDate");
			if (rangeEvents.contains(endDate)){
				Event reminder = new Event(endDate, title + " - Negotiation end",null, uuid,ownedNegotiation);
				reminders.add(reminder );
			}
		}
		return reminders;
	}

	private List<Event> buildRemindersFromTimeEvents(List<ODocument> iTimeEvents,Range<Date> rangeEvents,String iNegotiationUUID, String iNegotiationTitle,boolean ownedNegotiation){
		List<Event> reminders = new ArrayList<>();
		for (ODocument oDocument : iTimeEvents) {
			if (rangeEvents.contains((Date)oDocument.field("time"))){
				reminders.add(new Event((Date)oDocument.field("time"), iNegotiationTitle + " - "+(String)oDocument.field("name"),
						(String)oDocument.field("description"),iNegotiationUUID,ownedNegotiation));
			}
		}
		return reminders;
	}

	private Range<Date> getDateRange(Date lowerDate, Date upperDate) {
		Range<Date> rangeEvents = null;
		if (lowerDate !=null && upperDate == null){
			rangeEvents = Range.atLeast(lowerDate);
		}
		else if (lowerDate ==null && upperDate != null){
			rangeEvents = Range.lessThan(upperDate);
		}
		else if (lowerDate == null && upperDate == null){
			rangeEvents = Range.all();
		}
		else {
			rangeEvents = Range.closed(lowerDate, upperDate);
		}
		return rangeEvents;
	}


	public static class ReminderQFilters{
		public static final String FROM = "from";
		public static final String TO = "to";
	}

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.set(2012,11,1);
		System.out.println(cal.getTimeInMillis());
		cal.set(2012,11,16);
		System.out.println(cal.getTimeInMillis());
	}

}


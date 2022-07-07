package com.exrade.util;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.activity.Activity;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.security.CustomUserDetails;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class ContextHelper {

    /**
     * Get the current negotiation profile trying to get it in this order:
     * <ul>
     * 	<li>from Http.Context.current()</li>
     *  <li>from Cache</li>
     *  <li>if PROFILE_UUID is not null in the HTTP Header it is retrivied from DB and stored in cache</li>
     * </ul>
     *
     * @return current authorized user profile
     */
    public static Negotiator getMembership() {
        return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNegotiator();
    }

    public static String getMembershipUUID() {
        String membershipUUID = null;
        if (ContextHelper.getMembership() != null) {
            membershipUUID = ContextHelper.getMembership().getIdentifier();
        }
        return membershipUUID;
    }

    public static String getLanguage() {
//		return Http.Context.current().lang().language();
        return null;
    }

    public static String getIpAddress() {
//		return Http.Context.current().request().remoteAddress();
        return null;
    }

    public static void put(String key, Object object) {
//		Http.Context.current().args.put(key, object);
    }

    public static Object get(String key) {
//		return Http.Context.current().args.get(key);
        return null;
    }

    public static void remove(String key) {
//		Http.Context.current().args.remove(key);
    }

    public static void setUserProfile(Negotiator membership) {
        put(ContextHelper.MEMBERSHIP, membership);
    }

    public static void initContext(ExRequestEnvelope request) {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addNotificationEvent(NotificationEvent<?> event) {
//		List<NotificationEvent> events = new ArrayList<NotificationEvent>();
//		Context ctx = Http.Context.current();
//		if (ctx.args.get(ContextHelper.NOTIFICATION_EVENTS) != null){
//			events = (List<NotificationEvent>) ctx.args.get(ContextHelper.NOTIFICATION_EVENTS);
//		}
//
//		events.add(event);
//		ctx.args.put(ContextHelper.NOTIFICATION_EVENTS, events);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<NotificationEvent> retrieveAndClearNotificationEvents() {
        List<NotificationEvent> events = new ArrayList<NotificationEvent>();

//		Context ctx = Http.Context.current();
//		if (ctx.args.get(ContextHelper.NOTIFICATION_EVENTS) != null){
//			events = (List<NotificationEvent>) ctx.args.get(ContextHelper.NOTIFICATION_EVENTS);
//		}
//		ctx.args.remove(ContextHelper.NOTIFICATION_EVENTS);
        return events;
    }

    @SuppressWarnings("unchecked")
    public static void addActivity(Activity activity) {
//		List<Activity> activities = new ArrayList<Activity>();
//		Context ctx = Http.Context.current();
//		if (ctx.args.get(ContextHelper.ACTIVITIES) != null){
//			activities = (List<Activity>) ctx.args.get(ContextHelper.ACTIVITIES);
//		}
//
//		activities.add(activity);
//		ctx.args.put(ContextHelper.ACTIVITIES, activities);
    }

    @SuppressWarnings("unchecked")
    public static List<Activity> retrieveAndClearActivities() {
        List<Activity> activities = new ArrayList<Activity>();

//		Context ctx = Http.Context.current();
//		if (ctx.args.get(ContextHelper.ACTIVITIES) != null){
//			activities = (List<Activity>) ctx.args.get(ContextHelper.ACTIVITIES);
//		}
//		ctx.args.remove(ContextHelper.ACTIVITIES);
        return activities;
    }

    public final static String MEMBERSHIP = "membership";

    public final static String MEMBERSHIP_UUID = "membership.uuid";

    public final static String NOTIFICATION_EVENTS = "notificationEvents";

    public final static String ACTIVITIES = "activities";

    public static ExRequestEnvelope getRequestEnvelope() {
        //todo add temporary
        return null;
    }

    public static ResponseEntity<JsonNode> ok(JsonNode jsonNode) {
        return ResponseEntity.ok(jsonNode);
    }

    public static ResponseEntity<JsonNode> created(JsonNode jsonNode) {
        return new ResponseEntity<>(jsonNode, HttpStatus.CREATED);
    }

}

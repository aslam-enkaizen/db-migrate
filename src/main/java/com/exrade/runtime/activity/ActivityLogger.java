package com.exrade.runtime.activity;

import com.exrade.core.ExLogger;
import com.exrade.models.activity.ASObject;
import com.exrade.models.activity.Activity;
import com.exrade.models.activity.ObjectType;
import com.exrade.models.activity.Verb;
import com.exrade.models.authorisation.AuthorisationObjectType;
import com.exrade.models.authorisation.AuthorisationRequest;
import com.exrade.models.contract.Contract;
import com.exrade.models.informationmodel.Clause;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.NegotiationComment;
import com.exrade.models.payment.Payment;
import com.exrade.models.processmodel.protocol.Transition;
import com.exrade.models.review.Review;
import com.exrade.models.review.ReviewRequest;
import com.exrade.models.trak.Trak;
import com.exrade.models.trak.TrakApproval;
import com.exrade.models.trak.TrakResponse;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.User;
import com.exrade.models.workgroup.Post;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.models.workgroup.WorkGroupComment;
import com.exrade.runtime.notification.NotificationFeedManager;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.*;
import com.exrade.util.ContextHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityLogger {

    public static void log(ASObject actor, Verb verb, ASObject object, ASObject target, List<Negotiator> toList) {
        log(actor, verb, object, target, toList, null);
    }

    public static void log(ASObject actor, Verb verb, ASObject object, ASObject target, List<Negotiator> toList, Map<String, String> extraContext) {
        try {
            Activity activity = new ActivityManager().store(Activity.create(actor, verb, object, target));
            if (extraContext != null && !extraContext.isEmpty())
                activity.setExtraContext(extraContext);
            new NotificationFeedManager().create(activity, toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, NegotiationMessage object, Negotiation target, List<Negotiator> toList) {
        try {
            Map<String, String> extraContext = new HashMap<String, String>();
            Negotiator authenticatedActor = ContextHelper.getMembership() != null ? ContextHelper.getMembership() : actor;
            if (!actor.getIdentifier().equals(authenticatedActor.getIdentifier())) {
                extraContext.put("authenticatedActor", authenticatedActor.getIdentifier());
                log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(target), toList, extraContext);
            } else {
                log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(target), toList);
            }
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Transition object, Negotiation target, List<Negotiator> toList) {
        try {
            Map<String, String> extraContext = new HashMap<String, String>();
            extraContext.put("source", object.getSource().getName());
            extraContext.put("target", object.getTarget().getName());
            Negotiator actualActor = ContextHelper.getMembership() != null ? ContextHelper.getMembership() : actor;
            log(ASObject.create((Membership) actualActor), verb, ASObject.create(object), ASObject.create(target), toList, extraContext);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Negotiation object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), null, toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Contract object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), null, toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Negotiator object, Contract target, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create((Membership) object), ASObject.create(target), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, InformationModelTemplate object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), null, toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Negotiator object, InformationModelTemplate target, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create((Membership) object), ASObject.create(target), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, WorkGroup object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), null, toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Negotiator object, WorkGroup target, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create((Membership) object), ASObject.create(target), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Negotiation object, WorkGroup target, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(target), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Post object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getWorkGroup()), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, WorkGroupComment object, List<Negotiator> toList) {
        try {
            Map<String, String> extraContext = new HashMap<String, String>();
            extraContext.put(PostFields.WORKGROUP, object.getPost().getWorkGroup().getUuid());
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getPost()), toList, extraContext);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, NegotiationComment object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getNegotiation()), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, MemberInvitation object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getInvitedProfile()), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, NegotiationInvitation object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getInvitedNegotiation()), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(ObjectType actorObjectType, Verb verb, Negotiator object, List<Negotiator> toList, Map<String, String> extraContext) {
        try {
            log(ASObject.create(actorObjectType.name(), actorObjectType, actorObjectType.name(), "", "")
                    , verb
                    , ASObject.create((Membership) object), null, toList, extraContext);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, AuthorisationRequest object, List<Negotiator> toList) {
        try {
            ASObject target = null;
            Map<String, String> extraContext = new HashMap<String, String>();
            if (object.getObjectType() == AuthorisationObjectType.NEGOTIATION)
                target = ASObject.create(object.getObjectID(), ObjectType.NEGOTIATION, ObjectType.NEGOTIATION.name(), "", null);
            else if (object.getObjectType() == AuthorisationObjectType.NEGOTIATION_TEMPLATE)
                target = ASObject.create(object.getObjectID(), ObjectType.NEGOTIATION_TEMPLATE, ObjectType.NEGOTIATION_TEMPLATE.name(), "", null);
            else if (object.getObjectType() == AuthorisationObjectType.INFORMATION_MODEL_TEMPLATE)
                target = ASObject.create(object.getObjectID(), ObjectType.INFORMATION_MODEL_TEMPLATE, ObjectType.INFORMATION_MODEL_TEMPLATE.name(), "", null);
            else if (object.getObjectType() == AuthorisationObjectType.NEGOTIATION_MESSAGE) {
                target = ASObject.create(object.getObjectID(), ObjectType.NEGOTIATION_MESSAGE, ObjectType.NEGOTIATION_MESSAGE.name(), "", null);
                extraContext.put(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID, object.getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID).toString());
                extraContext.put(RestParameters.OBJECT_ID, object.getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID).toString());
                extraContext.put(AuthorisationFields.EXTRA_CONTEXT_ACTION, object.getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_ACTION).toString());
            }

            log(ASObject.create((Membership) actor), verb, ASObject.create(object), target, toList, extraContext);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, ReviewRequest object, List<Negotiator> toList) {
        try {
            Map<String, String> extraContext = new HashMap<String, String>();
            extraContext.put(ReviewRequestFields.OFFER_UUIDS, String.join(",", object.getOfferUUIDs()));
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getNegotiation()), toList, extraContext);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Review object, List<Negotiator> toList) {
        try {
            Map<String, String> extraContext = new HashMap<String, String>();
            extraContext.put(ReviewFields.OFFER_UUID, object.getOfferUUID());
            extraContext.put(ReviewFields.REVIEW_REQUEST_UUID, object.getReviewRequestUUID());
            extraContext.put(ReviewFields.NEGOTIATION_UUID, object.getNegotiationUUID());
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getNegotiation()), toList, extraContext);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, User object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), null, toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Trak object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getContract()), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create trak activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, TrakResponse object, List<Negotiator> toList) {
        try {
            Map<String, String> extraContext = new HashMap<String, String>();
            extraContext.put(TrakFields.CONTRACT_UUID, object.getTrak().getContractUUID());
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getTrak()), toList, extraContext);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create trak response activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, TrakApproval object, List<Negotiator> toList) {
        try {
            Map<String, String> extraContext = new HashMap<String, String>();
            extraContext.put(TrakFields.CONTRACT_UUID, object.getTrakResponse().getTrak().getContractUUID());
            extraContext.put(TrakResponseFields.TRAK_UUID, object.getTrakResponse().getTrakUUID());
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getTrakResponse().getTrak()), toList, extraContext);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create trak response approval activity.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Clause object, List<Negotiator> toList) {
        try {
            log(ASObject.create((Membership) actor), verb, ASObject.create(object), ASObject.create(object.getProfile()), toList);
        } catch (Exception e) {
            ExLogger.get().warn("Failed to update clause publication status.", e);
        }
    }

    public static void log(Negotiator actor, Verb verb, Payment payment, List<Negotiator> toList, Negotiation negotiation, Map<String, String> extraContext) {
        try {
            extraContext.put(NegotiationFilters.PAYMENT_UUID, String.join(",", payment.getUuid()));
            log(
                    ASObject.create((Membership) actor),
                    verb,
                    ASObject.create(payment),
                    ASObject.create(negotiation),
                    toList,
                    extraContext
            );
        } catch (Exception e) {
            ExLogger.get().warn("Failed to create payment log.", e);
        }
    }
}

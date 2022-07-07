package com.exrade.models.activity;

import com.exrade.models.authorisation.AuthorisationRequest;
import com.exrade.models.contract.Contract;
import com.exrade.models.informationmodel.Clause;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.invitations.AbstractInvitation;
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
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;
import com.exrade.models.workgroup.Post;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.models.workgroup.WorkGroupComment;
import com.exrade.platform.persistence.BaseEntityUUID;

/***
 * Activity Stream Object to store user/system's activity 
 * @author Md. Mahfuzul Islam
 *
 */
public class ASObject extends BaseEntityUUID {

    private ObjectType type;

    private String objectID;

    private String displayName;

    private String url;

    private String image;

    public static ASObject create(String objectID, ObjectType type, String displayName, String url, String image) {
        ASObject object = new ASObject();
        object.setObjectID(objectID);
        object.setType(type);
        object.setDisplayName(displayName);
        object.setUrl(url);
        object.setImage(image);
        return object;
    }

    public static ASObject create(Membership membership) {
        return ASObject.create(membership.getUuid(), ObjectType.MEMBERSHIP, membership.getFullName(), "", membership.getUserAvatar());
    }

    public static ASObject create(Negotiation negotiation) {
        String image = negotiation.getImages().size() > 0 ? negotiation.getImages().get(0).getFileUUID() : null;
        return ASObject.create(negotiation.getUuid(), ObjectType.NEGOTIATION, negotiation.getTitle(), "", image);
    }

    public static ASObject create(InformationModelTemplate informationModelTemplate) {
        return ASObject.create(informationModelTemplate.getUuid(), ObjectType.INFORMATION_MODEL_TEMPLATE, informationModelTemplate.getTitle(), "", null);
    }

    public static ASObject create(NegotiationMessage negotiationMessage) {
        return ASObject.create(negotiationMessage.getUuid(), ObjectType.NEGOTIATION_MESSAGE, negotiationMessage.getMessageType(), "", null);
    }

    public static ASObject create(Transition transition) {
        return ASObject.create(transition.getName(), ObjectType.TRANSITION, transition.getLabel(), "", null);
    }

    public static ASObject create(NegotiationComment comment) {
        return ASObject.create(comment.getUuid(), ObjectType.COMMENT, comment.getMessage(), "", null);
    }

    public static ASObject create(Profile profile) {
        String displayName = profile.getName();
        String image = profile.getLogo();
        return ASObject.create(profile.getUuid(), ObjectType.PROFILE, displayName, "", image);
    }

    public static ASObject create(WorkGroup workGroup) {
        return ASObject.create(workGroup.getUuid(), ObjectType.WORKGROUP, workGroup.getName(), "", workGroup.getLogo());
    }

    public static ASObject create(Post post) {
        return ASObject.create(post.getUuid(), ObjectType.POST, post.getTitle(), "", null);
    }

    public static ASObject create(WorkGroupComment comment) {
        return ASObject.create(comment.getUuid(), ObjectType.COMMENT, comment.getMessage(), "", null);
    }

    public static ASObject create(AbstractInvitation invitation) {
        return ASObject.create(invitation.getUuid(), ObjectType.INVITATION, null, "", null);
    }

    public static ASObject create(AuthorisationRequest authorisationRequest) {
        return ASObject.create(authorisationRequest.getUuid(), ObjectType.AUTHORISATION_REQUEST, null, "", null);
    }

    public static ASObject create(ReviewRequest reviewRequest) {
        return ASObject.create(reviewRequest.getUuid(), ObjectType.REVIEW_REQUEST, null, "", null);
    }

    public static ASObject create(Review review) {
        return ASObject.create(review.getUuid(), ObjectType.REVIEW, null, "", null);
    }

    public static ASObject create(Contract contract) {
        return ASObject.create(contract.getUuid(), ObjectType.CONTRACT, contract.getTitle(), "", null);
    }

    public static ASObject create(User user) {
        return ASObject.create(user.getUuid(), ObjectType.USER, user.getUuid(), "", null);
    }

    public static ASObject create(Trak trak) {
        return ASObject.create(trak.getUuid(), ObjectType.TRAK, trak.getTitle(), "", null);
    }

    public static ASObject create(TrakResponse trakResponse) {
        return ASObject.create(trakResponse.getUuid(), ObjectType.TRAK_RESPONSE,
                trakResponse.getNote(), "", null);
    }

    public static ASObject create(TrakApproval trakApproval) {
        return ASObject.create(trakApproval.getUuid(), ObjectType.TRAK_APPROVAL,
                trakApproval.getNote(), "", null);
    }

    public static ASObject create(Clause clause) {
        return ASObject.create(clause.getUuid(), ObjectType.CLAUSE, clause.getTitle(), "", null);
    }

    public static ASObject create(Payment payment) {
        return ASObject.create(payment.getUuid(), ObjectType.PAYMENT, payment.getPaymentStatus().toString(), payment.getPaymentUrl(), null);
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

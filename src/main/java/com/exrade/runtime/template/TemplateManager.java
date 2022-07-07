package com.exrade.runtime.template;

import com.exrade.models.template.Template;
import com.exrade.models.template.TemplateType;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.rest.RestParameters.NegotiationFields;
import com.exrade.runtime.rest.RestParameters.ReviewRequestFilters;
import com.exrade.runtime.review.IReviewManager;
import com.exrade.runtime.review.ReviewManager;
import com.exrade.runtime.template.persistence.TemplatePersistenceManager;
import com.exrade.runtime.template.persistence.TemplatePersistenceManager.TemplateQFilters;
import com.exrade.runtime.template.persistence.TemplateQuery;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;

import java.util.List;

public class TemplateManager implements ITemplateManager {

    private TemplatePersistenceManager templatePersistentManager;

    public TemplateManager() {
        this(new TemplatePersistenceManager());
    }

    public TemplateManager(TemplatePersistenceManager iTemplatePersistentManager) {
        this.templatePersistentManager = iTemplatePersistentManager;
    }

    @Override
    public Template createTemplate(Template iTemplate) {
        cleanTemplate(iTemplate);
        iTemplate.setOwnerMembership((Membership) ContextHelper.getMembership());
        iTemplate.setCreated(TimeProvider.now());
        checkTemplateOwnership(iTemplate);

        if (iTemplate.getTemplateType() == TemplateType.NegotiationAgreement)
            Security.checkAddHeadedPaperTemplatePermission();
        return templatePersistentManager.create(iTemplate);
    }

    @Override
    public Template updateTemplate(Template iTemplate) {
        cleanTemplate(iTemplate);
        checkTemplateOwnership(iTemplate);
        iTemplate.setUpdated(TimeProvider.now());
        return templatePersistentManager.update(iTemplate);
    }

    @Override
    public void deleteTemplate(String uuid) {
        Template template = this.getTemplateByUUID(uuid);
        checkTemplateOwnership(template);

        if (template.getTemplateType() == TemplateType.NegotiationAgreement) {
            INegotiationManager negotiationManager = new NegotiationManager();
            QueryFilters filters = QueryFilters.create(NegotiationFields.HEADED_PAPER_TEMPLATE_UUID, uuid);
            if (ExCollections.isNotEmpty(negotiationManager.listNegotiations(filters)))
                throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

        } else if (template.getTemplateType() == TemplateType.Review) {
            IReviewManager reviewManager = new ReviewManager();
            QueryFilters filters = QueryFilters.create(ReviewRequestFilters.REVIEW_TEMPLATE_UUID, uuid);
            if (ExCollections.isNotEmpty(reviewManager.listReviewRequests(filters)))
                throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

        }
        templatePersistentManager.delete(template);
    }

    @Override
    public Template getTemplateByUUID(String iTemplateUUID) {
        return templatePersistentManager.readObjectByUUID(Template.class, iTemplateUUID);
    }

    @Override
    public List<Template> listTemplates(QueryFilters iFilters) {

        if (!iFilters.isNullOrEmpty(TemplateQFilters.OWNER_PROFILE) &&
                !iFilters.get(TemplateQFilters.OWNER_PROFILE).equals(ContextHelper.getMembership().getProfile().getUuid())) {
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
        }

        if (!iFilters.isNullOrEmpty(TemplateQFilters.OWNER_MEMBERSHIP)
                && !isTemplateOwnerOrProfileAdministrator(iFilters.get(TemplateQFilters.OWNER_MEMBERSHIP).toString())) {
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
        }

        if (iFilters.isNullOrEmpty(TemplateQFilters.OWNER_PROFILE) && iFilters.isNullOrEmpty(TemplateQFilters.OWNER_MEMBERSHIP)) {
            iFilters.put(TemplateQFilters.OWNER_MEMBERSHIP, ContextHelper.getMembershipUUID());
        }

        return templatePersistentManager.listObjects(new TemplateQuery(), iFilters);
    }

    private boolean isTemplateOwnerOrProfileAdministrator(String ownerMembershipUUID) {
        if (!ownerMembershipUUID.equals(ContextHelper.getMembershipUUID())
                || !Security.isProfileAdministrator(ContextHelper.getMembership().getProfile().getUuid()))
            return false;
        return true;
    }

    private void checkTemplateOwnership(Template iTemplate) {
        if (!Security.isProfileAdministrator(iTemplate.getOwnerMembership().getProfileUUID()))
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
    }

    private void cleanTemplate(Template iTemplate) {
        //cleaning template
        if (iTemplate.getContent() != null)
            iTemplate.setContent(InformationModelUtil
                    .cleanTemplate(iTemplate.getContent(), true, true, false));
    }

}

package com.exrade.runtime.integration;

import com.exrade.core.ExLogger;
import com.exrade.models.integration.IntegrationServiceType;
import com.exrade.models.integration.IntegrationSetting;
import com.exrade.models.payment.IPaymentMethod;
import com.exrade.models.payment.PaymentType;
import com.exrade.models.payment.PaypalPaymentMethod;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.integration.persistence.IntegrationSettingQuery;
import com.exrade.runtime.rest.RestParameters.IntegrationSettingFields;
import com.exrade.runtime.rest.RestParameters.IntegrationSettingFilters;
import com.exrade.runtime.userprofile.IProfileManager;
import com.exrade.runtime.userprofile.ProfileManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.PaypalUtil;
import org.slf4j.Logger;

import java.util.List;

public class IntegrationSettingManager {
    private static final Logger LOGGER = ExLogger.get();
    private PersistentManager persistenceManager = new PersistentManager();
    private static String CLIENT_ID = "clientId";
    private static String MERCHANT_ID = "merchantId";

    // TODO: manage the case when integration setting is disable but there is active negotiation with the setting
    public IntegrationSetting createIntegrationSetting(IntegrationSetting iIntegrationSetting) {
        checkIntegrationPermission(iIntegrationSetting.getIntegrationServiceType());

        if (!Security.isProfileAdministrator(iIntegrationSetting.getProfile().getUuid()))
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

        IntegrationSetting integrationSetting = new IntegrationSetting();
        integrationSetting.setCreateddBy(ContextHelper.getMembership());
        integrationSetting.setSettings(iIntegrationSetting.getSettings());
        integrationSetting.setActive(iIntegrationSetting.isActive());
        integrationSetting.setIntegrationServiceType(iIntegrationSetting.getIntegrationServiceType());
        integrationSetting.setProfile(iIntegrationSetting.getProfile());

        integrationSetting = persistenceManager.create(integrationSetting);
        LOGGER.info("Created integration setting {}", integrationSetting);
        
        handlePostIntegrationSettingModification(integrationSetting);
        
        return getIntegrationSetting(integrationSetting.getUuid());
    }

    public IntegrationSetting updatIntegrationSetting(String iUUID, IntegrationSetting iIntegrationSetting) {
        checkIntegrationPermission(iIntegrationSetting.getIntegrationServiceType());

        IntegrationSetting integrationSetting = getIntegrationSetting(iUUID);
        if (!Security.isProfileAdministrator(integrationSetting.getProfile().getUuid()))
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
        integrationSetting.setSettings(iIntegrationSetting.getSettings());
        integrationSetting.setActive(iIntegrationSetting.isActive());
        integrationSetting.setUpdatedBy(ContextHelper.getMembership());
        persistenceManager.update(integrationSetting);
        LOGGER.info("Updated integration settings {}", integrationSetting);
        
        handlePostIntegrationSettingModification(integrationSetting);
        
        return getIntegrationSetting(integrationSetting.getUuid());
    }

    public IntegrationSetting getIntegrationSetting(String iUUID) {
        //todo removed kyc get permission check
        return persistenceManager.readObjectByUUID(IntegrationSetting.class, iUUID);
    }

    public IntegrationSetting getIntegrationSetting(String iProfileUUID, IntegrationServiceType iIntegrationServiceType) {
        //todo removed kyc get permission check
//        checkIntegrationPermission(iIntegrationServiceType);

        QueryFilters filters = QueryFilters.create(IntegrationSettingFilters.PROFILE_UUID, iProfileUUID);
        filters.put(IntegrationSettingFields.INTEGRATION_SERVICE_TYPE, iIntegrationServiceType);

        return persistenceManager.readObject(new IntegrationSettingQuery(), filters);
    }

    public List<IntegrationSetting> getIntegrationSettings(QueryFilters filters) {
        checkIntegrationPermission(IntegrationServiceType.SERVITLY_SERVITIZATION); //passing to check integration permission
        List<IntegrationSetting> integrationSettings = persistenceManager.listObjects(new IntegrationSettingQuery(), filters);

        return integrationSettings;
    }

    public List<IntegrationSetting> getIntegrationSettings(IntegrationServiceType iIntegrationServiceType) {
        checkIntegrationPermission(iIntegrationServiceType);
        QueryFilters filters = QueryFilters.create(IntegrationSettingFields.INTEGRATION_SERVICE_TYPE, iIntegrationServiceType);
        List<IntegrationSetting> integrationSettings = persistenceManager.listObjects(new IntegrationSettingQuery(), filters);

        return integrationSettings;
    }

    public String getPaypalMerchantURl(String lang) {
        return PaypalUtil.getPaypalMerchantURl(lang);
    }

    public void deleteIntegrationSetting(String iUUID) {
        IntegrationSetting integrationSetting = persistenceManager.readObjectByUUID(IntegrationSetting.class, iUUID);
        checkIntegrationPermission(integrationSetting.getIntegrationServiceType()); //passing to check integration permission
        if (!Security.isProfileAdministrator(integrationSetting.getProfile().getUuid()))
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
        persistenceManager.delete(integrationSetting);
        LOGGER.info("Updated integration settings {}", iUUID);
    }
    
    private void handlePostIntegrationSettingModification(IntegrationSetting iIntegrationSetting) {
    	// TODO: handle update & delete
		switch (iIntegrationSetting.getIntegrationServiceType().toString()) {
		case "PAYPAL":
			addPaymentMethod(iIntegrationSetting, PaymentType.PAYPAL, CLIENT_ID);
			break;
		case "PAYPAL_MERCHANT":
			addPaymentMethod(iIntegrationSetting, PaymentType.PAYPAL, MERCHANT_ID);
			break;

		default:
			break;
		}
    }

	private void addPaymentMethod(IntegrationSetting iIntegrationSetting, PaymentType paymentType, String id) {
		IProfileManager manager = new ProfileManager();
		IPaymentMethod paymentMethod = null;
		List<IPaymentMethod> paymentMethods = manager.getPaymentMethods(iIntegrationSetting.getProfile().getUuid());

		for (IPaymentMethod method : paymentMethods) {
			if (method.getPaymentType() == paymentType) {
				paymentMethod = method;
				break;
			}
		}

		if (paymentMethod != null) {
			((PaypalPaymentMethod) paymentMethod).setEmail(iIntegrationSetting.getSettings().get(id).toString());
			manager.updatePaymentMethod(iIntegrationSetting.getProfile().getUuid(), paymentMethod);
		} else {
			paymentMethod = new PaypalPaymentMethod();
			((PaypalPaymentMethod) paymentMethod).setEmail(iIntegrationSetting.getSettings().get(id).toString());

			manager.addPaymentMethod(iIntegrationSetting.getProfile().getUuid(), paymentMethod);
		}
	}

    private void checkIntegrationPermission(IntegrationServiceType iIntegrationServiceType) {
        //checking profile permission
        if (iIntegrationServiceType.equals(IntegrationServiceType.W2_KYC_AML))
            Security.hasAccessPermission(Security.ProfilePermissions.INTEGRATIONS_KYC);
        else if (iIntegrationServiceType.equals(IntegrationServiceType.TWILIO_SMS))
            Security.hasAccessPermission(Security.ProfilePermissions.INTEGRATIONS_SMS);
        else Security.hasAccessPermission(Security.ProfilePermissions.INTEGRATIONS);
    }

}

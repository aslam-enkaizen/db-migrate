package com.exrade.controllers.userprofile;

import com.exrade.api.MemberInvitationAPI;
import com.exrade.api.MembershipAPI;
import com.exrade.api.ProfileAPI;
import com.exrade.api.impl.MemberInvitationAdapter;
import com.exrade.api.impl.MembershipManagerAdapter;
import com.exrade.api.impl.ProfileManagerAdapter;
import com.exrade.core.FieldsAvailable;
import com.exrade.dto.userprofile.ProfileDTO;
import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.security.ProfileStatus;
import com.exrade.models.userprofile.usage.UsageSummary;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.exception.RecordNotFoundException;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.ProfileFields;
import com.exrade.runtime.userprofile.QuotaUsageManager;
import com.exrade.runtime.userprofile.providers.local.LocalWalletProvider;
import com.exrade.util.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.RawTransaction;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Map;

import static com.exrade.controllers.MemberInvitations.MEMBER_INVITATIONS_FIELDS;
import static com.exrade.controllers.userprofile.Memberships.MEMBERSHIP_FIELDS;
import static com.exrade.util.ContextHelper.getRequestEnvelope;
import static com.exrade.util.ContextHelper.ok;

/**
 * @Author Md. Aslam Hossain
 * @Date Jun 29, 2022
 */
@RestController()
@RequestMapping("1/profiles")
public class Profiles {
    public final static FieldsAvailable PROFILE_FIELDS = new FieldsAvailable() {

        @Override
        public List<String> getValidFields() {
            return ProfileFields.VALID_FIELDS;
        }

        @Override
        public List<String> getExpandFields() {
            return ProfileFields.EXPAND_FIELDS;
        }

        @Override
        public List<String> getDefaultFields() {
            return ProfileFields.DEFAULT_FIELDS;
        }

        @Override
        public Class<?> getType() {
            return Profile.class;
        }
    };
    private static final ProfileAPI profileManager = new ProfileManagerAdapter();
    private static final MembershipAPI membershipManager = new MembershipManagerAdapter();
    private static final LocalWalletProvider localWalletProvider = new LocalWalletProvider();
    private final static MemberInvitationAPI memberInvitationManager = new MemberInvitationAdapter();
    private static final String SIGNIN_PROVIDER = ExConfiguration.getStringProperty("data-signing-service-provider-name");
    private final ModelMapper modelMapper;


    public Profiles(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<JsonNode> getProfile(
            @PathVariable("uuid") String uuid,
            @RequestParam Map<String, String> params
    ) {

        Profile profile = profileManager.findByUUID(null, uuid);

        if (profile == null) {
            throw new RecordNotFoundException(String.format("Profile %s not found", uuid));
        }

        List<String> fieldsParameters = ControllerUtil.getFieldsToInclude(params, profile.getFieldsSerializable());

        JsonNode json = JSONUtil.toJsonFieldsFiltered(profile, fieldsParameters, profile.getClass());

        return ResponseEntity.ok(json);
    }

    @GetMapping()
    public ResponseEntity<JsonNode> getProfiles(
            @RequestParam Map<String, String> params) {
        List<Profile> profiles = profileManager.getProfiles(null, params);
        JsonNode result = ControllerUtil.buildListObjects(RestParameters.Resources.PROFILES, profiles, PROFILE_FIELDS, params);
        return ResponseEntity.ok(result);
    }

    @PostMapping()
    public ResponseEntity<JsonNode> create(@RequestBody @Valid ProfileDTO profileDTO) {
        Profile profile = modelMapper.map(profileDTO, Profile.class);
        Profile created = profileManager.create(null, profile);
        ObjectNode result = new ObjectMapper().createObjectNode();
        result.put(RestParameters.UUID, created.getUuid());
        return new ResponseEntity<>(ControllerUtil.toJsonResponseNoPersistence(result), HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public void update(
            @PathVariable("uuid") String uuid,
            @RequestBody @Valid ProfileDTO profileDTO
    ) {
        if (Strings.isNullOrEmpty(uuid)) {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.UUID);
        }
        Profile oldProfile = profileManager.findByUUID(null, uuid);

        if (oldProfile == null) {
            throw new RecordNotFoundException(String.format("Profile %s not found", uuid));
        }
        modelMapper.map(profileDTO, oldProfile);
        profileManager.update(null, oldProfile);
    }

    @PutMapping("/{uuid}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(
            @PathVariable("uuid") String uuid,
            @RequestBody JsonNode jsonObject
    ) {
        if (Strings.isNullOrEmpty(uuid)) {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.UUID);
        }
        Profile profile = profileManager.findByUUID(null, uuid);
        if (profile == null)
            throw new RecordNotFoundException(String.format("Profile %s not found", uuid));

        String jsonProfileStatus = jsonObject.path(ProfileFields.PROFILE_STATUS).textValue();
        String jsonComment = jsonObject.path(RestParameters.ProfileFilters.COMMENT).textValue();

        ProfileStatus profileStatus = ProfileStatus.valueOf(jsonProfileStatus);
        profileManager.updateStatus(null, profile, profileStatus, jsonComment);
    }

    @PutMapping("/{uuid}/signed-data")
    public ResponseEntity<ObjectNode> signedData(
            @PathVariable("uuid") String uuid,
            @RequestBody JsonNode jsonObject
    ) throws IOException, CipherException {
        if (Strings.isNullOrEmpty(uuid)) {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.UUID);
        }
        Profile profile = profileManager.findByUUID(null, uuid);
        if (profile == null)
            throw new RecordNotFoundException(String.format("Profile %s not found", uuid));

        String signedData = "";
        if (!jsonObject.path("nonce").isMissingNode() && SIGNIN_PROVIDER.equalsIgnoreCase(Constants.LOCAL_SIGNIN_PROVIDER)) {
            BigInteger nonce = BigInteger.valueOf(jsonObject.path("nonce").asLong());
            BigInteger gasPrice = BigInteger.valueOf(jsonObject.path("gasPrice").asLong());
            BigInteger gasLimit = BigInteger.valueOf(jsonObject.path("gasLimit").asLong());
            String to = jsonObject.path("to").asText();
            String data = jsonObject.path("data").asText();
            BigInteger value = BigInteger.valueOf(jsonObject.path("value").asLong());
            //BigInteger chainId = BigInteger.valueOf(jsonObject.path("chainId").asInt());
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
            signedData = localWalletProvider.signedDataWithRawTransaction(profile, rawTransaction);
        } else {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, "provider");
        }

        ObjectNode result = new ObjectMapper().createObjectNode();
        result.put("signedData", signedData);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{uuid}/wallet")
    public ResponseEntity<ObjectNode> generateWalletFile(
            @PathVariable("uuid") String uuid
    ) throws InvalidAlgorithmParameterException,
            NoSuchAlgorithmException,
            NoSuchProviderException,
            IOException,
            CipherException {
        if (Strings.isNullOrEmpty(uuid)) {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.UUID);
        }
        Profile profile = profileManager.findByUUID(null, uuid);
        if (profile == null)
            throw new RecordNotFoundException(String.format("Profile %s not found", uuid));

        String address = localWalletProvider.getWalletAddress();
        profile.setWalletAddress(address);
        profileManager.update(null, profile);

        ObjectNode result = new ObjectMapper().createObjectNode();
        result.put(ProfileFields.WALLET_ADDRESS, address);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/{profileuuid}/members")
    public ResponseEntity<JsonNode> getProfileMemberships(
            @PathVariable("profileuuid") String profileUUID,
            @RequestParam Map<String, String> params) {
        if (Strings.isNullOrEmpty(profileUUID)) {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.UUID);
        }
        params.put(RestParameters.MembershipFilters.PROFILE, profileUUID);

        List<Membership> memberships = membershipManager.find(null, params);

        JsonNode jsonNode = ControllerUtil.buildListObjects(RestParameters.Resources.MEMBERSHIPS, memberships, MEMBERSHIP_FIELDS, params);

        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("/{profileUUID}/usage")
    public ResponseEntity<JsonNode> getPlanUsage(
            @PathVariable("profileUUID") String profileUUID
    ) {
        Negotiator negotiator = membershipManager
                .getOwnerMembership(null, profileUUID);
        UsageSummary usageSummary = QuotaUsageManager.getUsageSummary(negotiator);
        String json = JSONUtil.toJsonNoPersistence(usageSummary);
        return ResponseEntity.ok(JSONUtil.toJsonNode(json));
    }

    @GetMapping("/invitations/")
    public ResponseEntity<JsonNode> getAllProfileInvitations(
            @RequestParam Map<String, String> param
    ) {
        List<MemberInvitation> invitations = memberInvitationManager
                .getAllIncomingInvitations(getRequestEnvelope(), param);
        JsonNode jsonNode = ControllerUtil
                .buildListObjects(
                        RestParameters.Resources.INVITATIONS,
                        invitations,
                        MEMBER_INVITATIONS_FIELDS, param);
        return ok(jsonNode);
    }

    public static abstract class Constants {
        private static final String LOCAL_SIGNIN_PROVIDER = "local";
    }
}

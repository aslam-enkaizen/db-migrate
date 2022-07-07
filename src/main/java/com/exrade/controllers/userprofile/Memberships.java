package com.exrade.controllers.userprofile;

import com.exrade.api.AccountAPI;
import com.exrade.api.MembershipAPI;
import com.exrade.api.impl.AccountManagerAdapter;
import com.exrade.api.impl.MembershipManagerAdapter;
import com.exrade.core.FieldsAvailable;
import com.exrade.dto.userprofile.MembershipCreateDTO;
import com.exrade.dto.userprofile.MembershipUpdateDTO;
import com.exrade.models.Role;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.MemberRole;
import com.exrade.models.userprofile.security.MemberStatus;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.security.RoleManager;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("1/memberships")
public class Memberships {

    private static final MembershipAPI membershipManager = new MembershipManagerAdapter();
    private static final AccountAPI userManager = new AccountManagerAdapter();

    public final static FieldsAvailable MEMBERSHIP_FIELDS = new FieldsAvailable() {

        @Override
        public List<String> getValidFields() {
            return RestParameters.MembershipFields.VALID_FIELDS;
        }

        @Override
        public List<String> getExpandFields() {
            return RestParameters.MembershipFields.EXPAND_FIELDS;
        }

        @Override
        public List<String> getDefaultFields() {
            return RestParameters.MembershipFields.DEFAULT_FIELDS;
        }

        @Override
        public Class<?> getType() {
            return Membership.class;
        }
    };

    @GetMapping("/{membershipuuid}")
    public ResponseEntity<JsonNode> getMembership(
            @PathVariable("membershipuuid") String membershipuuid,
            @RequestParam Map<String, String> params
    ) {
        Membership memberProfile = membershipManager.findByUUID(null, membershipuuid);
        JsonNode json = ControllerUtil.buildExpandableObject(memberProfile, MEMBERSHIP_FIELDS, params);
        return ResponseEntity.ok(json);
    }

    @GetMapping("/profile/{profileUUID}/user/{userUUID}")
    public ResponseEntity<JsonNode> getProfileMembership(
            @PathVariable("profileUUID") String profileUUID,
            @PathVariable("userUUID") String userUUID,
            @RequestParam Map<String, String> params
    ) {
        Membership memberProfile = membershipManager.getMembershipOf(null, userUUID, profileUUID);
        JsonNode json = ControllerUtil.buildExpandableObject(memberProfile, MEMBERSHIP_FIELDS, params);

        return ResponseEntity.ok(json);
    }

    @GetMapping()
    public ResponseEntity<JsonNode> getMemberships(
            @RequestParam Map<String, String> params
    ) {
        List<Membership> memberships = membershipManager.find(null, params);

        JsonNode jsonNode = ControllerUtil.buildListObjects(RestParameters.Resources.MEMBERSHIPS, memberships, MEMBERSHIP_FIELDS, params);

        return ResponseEntity.ok(jsonNode);
    }

    @PostMapping()
    public ResponseEntity<JsonNode> createMembership(
            @RequestBody @Valid MembershipCreateDTO createDTO
    ) {
        Membership membership;
        JsonNode jsonNode;
        if (StringUtils.isNotBlank(createDTO.userUUID)) {
            membership = membershipManager.addMembership(
                    null,
                    createDTO.userUUID,
                    createDTO.profileUUID,
                    createDTO.title,
                    createDTO.roleName,
                    createDTO.expirationDate,
                    createDTO.authorizationDocuments,
                    createDTO.maxNegotiationAmount,
                    createDTO.agreementSigner,
                    createDTO.supervisor
            );
        } else {
            User user = User.createUser(createDTO.email);
            user.setFirstName(createDTO.firstName);
            user.setLastName(createDTO.lastName);
            user.setTimezone(createDTO.timezone);
            user.setLanguage(createDTO.language);
            user.setAvatar(createDTO.avatar);
            user = userManager.createAccount(null, user);

            membership = membershipManager.addMembership(null,
                    user.getUuid(),
                    createDTO.profileUUID,
                    createDTO.title,
                    createDTO.roleName,
                    createDTO.expirationDate,
                    createDTO.authorizationDocuments,
                    createDTO.maxNegotiationAmount,
                    createDTO.agreementSigner,
                    createDTO.supervisor);
        }

        jsonNode = ControllerUtil.buildObject(membership, MEMBERSHIP_FIELDS);
        return new ResponseEntity<>(jsonNode, HttpStatus.CREATED);
    }

    @PutMapping("/{membershipuuid}")
    public ResponseEntity<JsonNode> updateMembership(
            @PathVariable("membershipuuid") String membershipuuid,
            @RequestBody @Valid MembershipUpdateDTO updateDTO
    ) {
        Role role = new RoleManager().findByName(updateDTO.roleName);
        Membership membership = membershipManager
                .findByUUID(null, membershipuuid);
        membership.setAgreementSigner(updateDTO.agreementSigner);
        membership.setAuthorizationDocuments(updateDTO.authorizationDocuments);
        membership.setExpirationDate(updateDTO.expirationDate > 0 ? new Date(updateDTO.expirationDate) : null);
        membership.setMaxNegotiationAmount(updateDTO.maxNegotiationAmount);
        membership.setRole((MemberRole) role);
        membership.setStatus(updateDTO.status);
        membership.setSupervisor(membershipManager
                .findByUUID(null, updateDTO.supervisor));
        membership.setTitle(updateDTO.title);
        membership = membershipManager
                .updateMembership(null, membership);
        JsonNode jsonNode = ControllerUtil
                .buildObject(membership, MEMBERSHIP_FIELDS);
        return new ResponseEntity<>(jsonNode, HttpStatus.CREATED);
    }

    @PutMapping("/{membershipuuid}/role/{rolename}")
    @ResponseStatus(HttpStatus.OK)
    public void updateMembershipRole(
            @PathVariable("membershipuuid") String memberUUID,
            @PathVariable("rolename") String roleName) {
        membershipManager
                .updateRole(null, memberUUID, roleName);
    }

    @PutMapping("/{membershipuuid}/status/{memberStatus}")
    @ResponseStatus(HttpStatus.OK)
    public void updateMembershipStatus(
            @PathVariable("membershipuuid") String memberUUID,
            @PathVariable("memberStatus") String memberStatus) {
        membershipManager
                .updateMemberStatus(null, memberUUID,
                        MemberStatus.valueOf(memberStatus));
    }

    @PostMapping("/{membershipuuid}/setdefault")
    @ResponseStatus(HttpStatus.OK)
    public void setDefaultMembership(
            @PathVariable("membershipuuid") String memberUUID,
            @RequestBody JsonNode jsonobject
    ) {
        boolean defaultProfileValue = jsonobject
                .path(RestParameters.MembershipFields.IS_DEFAULT_PROFILE).booleanValue();
        membershipManager
                .setDefaultMembership(null, memberUUID, defaultProfileValue);
    }

    @DeleteMapping("/{membershipuuid}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void deleteMembership(
            @PathVariable("membershipuuid") String uuid
    ) {

    }

    @PostMapping("/guests")
    public ResponseEntity<JsonNode> createGuestMembership(
            @RequestBody JsonNode jsonobject
    ) {
        String email = jsonobject.path(RestParameters.UserFields.EMAIL).asText(null);
        Membership membership = membershipManager
                .createGuestMembership(null,
                        jsonobject.path(RestParameters.UserFields.FIRST_NAME).asText(null),
                        jsonobject.path(RestParameters.UserFields.LAST_NAME).asText(null),
                        email == null ? null : email.toLowerCase(),
                        jsonobject.path(RestParameters.UserFields.PHONE).asText(null),
                        jsonobject.path(RestParameters.MembershipFields.TITLE).asText(null));
        JsonNode jsonNode = ControllerUtil.buildObject(membership, MEMBERSHIP_FIELDS);
        return new ResponseEntity<>(jsonNode, HttpStatus.CREATED);
    }

    @GetMapping("/guests/{email}")
    public ResponseEntity<JsonNode> getGuestMembership(
            @PathVariable("email") String email,
            @RequestParam Map<String, String> params) {
        Membership memberProfile = membershipManager
                .findGuestMembershipByEmail(null, email.toLowerCase());
        JsonNode json = ControllerUtil
                .buildExpandableObject(memberProfile, MEMBERSHIP_FIELDS, params);
        return ResponseEntity.ok(json);
    }
}

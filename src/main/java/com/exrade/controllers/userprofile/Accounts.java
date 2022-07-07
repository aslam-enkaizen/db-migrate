package com.exrade.controllers.userprofile;

import com.exrade.api.AccountAPI;
import com.exrade.api.impl.AccountManagerAdapter;
import com.exrade.core.FieldsAvailable;
import com.exrade.dto.userprofile.UserDTO;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.AccountStatus;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.exrade.runtime.rest.RestParameters;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author Md. Aslam Hossain
 * @Date Jun 29, 2022
 */
@RestController
@RequestMapping("1/users")
public class Accounts {
    public final static FieldsAvailable ACCOUNTS_FIELDS = new FieldsAvailable() {

        @Override
        public List<String> getValidFields() {
            return RestParameters.UserFields.VALID_FIELDS;
        }

        @Override
        public List<String> getExpandFields() {
            return RestParameters.UserFields.EXPAND_FIELDS;
        }

        @Override
        public List<String> getDefaultFields() {
            return RestParameters.UserFields.DEFAULT_FIELDS;
        }

        @Override
        public Class<?> getType() {
            return User.class;
        }
    };
    private static final AccountAPI accountManager = new AccountManagerAdapter();
    private final ModelMapper modelMapper;

    public Accounts(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<JsonNode> createUser(@RequestBody @Valid UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        User createdUser = accountManager.createAccount(null, user);
        JsonNode json = ControllerUtil.buildObject(createdUser, ACCOUNTS_FIELDS);
        return ResponseEntity.ok(json);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<JsonNode> getUser(@PathVariable("uuid") String uuid, @RequestParam Map<String, String> params) {
        if (Strings.isNullOrEmpty(uuid)) {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.UUID);
        }
        User user = accountManager.findByUUID(null, uuid);
        JsonNode json = ControllerUtil.buildObject(user, ACCOUNTS_FIELDS, params);
        return ResponseEntity.ok(json);
    }

    @GetMapping
    public ResponseEntity<JsonNode> getUsers(@RequestParam Map<String, String> params) {
        List<User> users = accountManager.find(null, params);
        JsonNode jsonNode = ControllerUtil.buildListObjects(RestParameters.Resources.USERS, users, ACCOUNTS_FIELDS, params);
        return ResponseEntity.ok(jsonNode);
    }

    @PostMapping("/{uuid}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable("uuid") String userAccountUUID, @RequestParam Map<String, String> params) {
        if (Strings.isNullOrEmpty(userAccountUUID)) {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.UUID);
        }
        AccountStatus status = AccountStatus.valueOf(params.get(RestParameters.UserFields.ACCOUNT_STATUS));
        accountManager.updateAccountStatus(null, userAccountUUID, status);
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable("uuid") String iUserUUID, @RequestBody @Valid UserDTO userDTO) {
        if (Strings.isNullOrEmpty(iUserUUID)) {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.UUID);
        }
        User oldUser = accountManager.findByUUID(null, iUserUUID);
        modelMapper.map(userDTO, oldUser);
        accountManager.updateAccount(null, oldUser);
    }

    @PutMapping("/{useruuid}/role/{rolename}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserRole(@PathVariable("useruuid") String userUUID, @PathVariable("rolename") String platformRoleName) {
        if (Strings.isNullOrEmpty(userUUID)) {
            throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.UUID);
        }
        accountManager.updateRole(null, userUUID, platformRoleName);
    }

    @GetMapping("/exists")
    public ResponseEntity<?> existsAccount(@RequestParam Map<String, String> params) {
        if (accountManager.existsAccount(null, params)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(Joiner.on(",").withKeyValueSeparator("=").join(params), HttpStatus.NOT_FOUND);
    }
}

package com.exrade.dto.userprofile;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

import static com.exrade.util.ValidationMessage.*;

/**
 * @author Rhidoy
 * @created 6/28/22
 */
@Data
@NoArgsConstructor
public class MembershipCreateDTO {
    public String userUUID;
    @Email(message = VALID_EMAIL)
    public String email;
    public String firstName;
    public String lastName;
    public String timezone = "UTC";
    public String language;
    public String avatar;
    @NotBlank(message = NOT_BLANK)
    public String profileUUID;
    public String roleName = "profile.member";
    public boolean agreementSigner;
    @Digits(integer = 20, fraction = 2)
    public Double maxNegotiationAmount;
    public Date expirationDate;
    public List<String> authorizationDocuments;
    public String title;
    public String supervisor;

    @AssertFalse(message = "userUUID" + IS_REQUIRED)
    private boolean isNotValid() { //method name is error field, if false then success
      return (StringUtils.isBlank(userUUID) && StringUtils.isBlank(email));
    }
}

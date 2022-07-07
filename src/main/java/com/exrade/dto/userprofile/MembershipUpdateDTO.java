package com.exrade.dto.userprofile;

import com.exrade.models.userprofile.security.MemberStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.exrade.util.ValidationMessage.NOT_BLANK;

/**
 * @author Rhidoy
 * @created 6/28/22
 */
@Data
@NoArgsConstructor
public class MembershipUpdateDTO {
//    @NotBlank(message = NOT_BLANK)
//    public String uuid;
    @NotBlank(message = NOT_BLANK)
    public String roleName = "profile.member";
    @Valid
    public MemberStatus status;
    public boolean agreementSigner;
    @Digits(integer=20, fraction=2)
    public Double maxNegotiationAmount;
    public long expirationDate;
    public List<String> authorizationDocuments;
    public String title;
    public String supervisor;
}

package com.exrade.dto.contact;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author Md. Aslam Hossain
 * @Date Jun 28, 2022
 */
@Data
@NoArgsConstructor
public class ContactDTO {
    @NotBlank
    @Email
    public String email;

    public String name;

    public String organization;

    public String phone;

    public String address;

    public String city;

    public String country;

    public String note;

    public String externalId;

    public List<String> tags;

    public String linkedMembershipIdentifier;
}

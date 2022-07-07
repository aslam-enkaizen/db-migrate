package com.exrade.dto.userprofile;

import com.exrade.models.common.Image;
import com.exrade.validator.ProfileSubDomainValidator;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Author Md. Aslam Hossain
 * @Date Jun 29, 2022
 */
@Data
@NoArgsConstructor
public class ProfileDTO {
    public String address;

    public String city;

    public String competences;

    public String country;

    public Map<String, Object> customFields;

    public String description;

    public String facebook;

    public List<String> files;

    public List<Image> images;

    public String interests;

    public String legalEmail;

    public String linkedin;

    public String logo;

    public String nace;

    public String name;

    public String phone;

    public String postcode;

    public boolean publicProfile = true;

    @ProfileSubDomainValidator
    public String subdomain;

    public String twitter;

    public String vat;

    public String video;

    public String walletAddress;

    public String website;
}

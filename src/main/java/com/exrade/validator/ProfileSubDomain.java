package com.exrade.validator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Md. Aslam Hossain
 * @Date Jun 27, 2022
 */


public class ProfileSubDomain implements ConstraintValidator<ProfileSubDomainValidator, String> {
    final static List<String> RESERVED_SUBDOMAINS = Arrays.asList("www", "deals", "mail", "remote", "blog", "webmail", "server", "ns1", "ns2", "smtp",
            "secure", "vpn", "m", "shop", "ftp", "mail2", "test", "portal", "ns", "ww1", "host", "support", "dev", "web", "bbs", "ww42",
            "mx", "email", "cloud", "1", "mail1", "2", "forum", "owa", "www2", "gw", "admin", "cdn", "api", "store", "exchange", "app",
            "gov", "2tty", "vps", "govty", "news", "imap", "pop", "wiki", "kb", "help", "info", "static", "media", "feeds", "events",
            "groups", "beta", "files", "resources", "live", "videos", "ssl", "videos", "apps", "sites", "img", "stage");

    @Override
    public void initialize(ProfileSubDomainValidator profileSubDomainValidator) {
        ConstraintValidator.super.initialize(profileSubDomainValidator);
    }

    @Override
    public boolean isValid(String subDomain, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotBlank(subDomain) && !RESERVED_SUBDOMAINS.contains(subDomain);
    }


}

package com.exrade.security;

import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.userprofile.MembershipManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TraktiUserDetailsService implements UserDetailsService {

    private final MembershipManager membershipManager = new MembershipManager();
    private final ObjectMapper mapper;

    public TraktiUserDetailsService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //todo temporary
        Negotiator tempNegotiator = membershipManager
                .findDefaultMembershipByEmail(username);

        if (tempNegotiator == null)
            throw new UsernameNotFoundException(String
                    .format("The username %s " +
                            "doesn't exist", username));

//        JsonNode jsonObject = toJsonResponseNoPersistence(tempNegotiator);
//        Membership membership = new Membership();
//
//        membership.setUser(mapper.treeToValue(jsonObject.get(RestParameters.MembershipFields.USER), User.class));
//        membership.setProfile(mapper.treeToValue(jsonObject.get(RestParameters.MembershipFields.PROFILE), Profile.class));
//        membership.setStatus(MemberStatus.valueOf(jsonObject.path(RestParameters.MembershipFields.STATUS).asText()));
//        membership.setUuid(jsonObject.get(RestParameters.UUID).asText());
//        membership.setTitle(jsonObject.get(RestParameters.MembershipFields.TITLE).asText());
//        membership.setRole(new MemberRole(jsonObject.path(RestParameters.MembershipFields.ROLE).asText()));

//        negotiator = membership;

        return new CustomUserDetails("{noop}password", tempNegotiator);

    }
}

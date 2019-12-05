package org.itrunner.wechat.base;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2User> {
    @Override
    public SecurityContext createSecurityContext(WithMockOAuth2User oauth2User) {
        OAuth2User principal = new OAuth2User() {
            @Override
            public Map<String, Object> getAttributes() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("openid", oauth2User.name());
                return attributes;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public String getName() {
                return oauth2User.name();
            }
        };

        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(principal, Collections.emptyList(), "weixin");
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        return context;
    }
}
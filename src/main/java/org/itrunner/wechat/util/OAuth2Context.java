package org.itrunner.wechat.util;

import org.itrunner.wechat.config.WeChatOAuth2User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class OAuth2Context {
    private OAuth2Context() {

    }

    public static String getOpenId() {
        return getOAuth2User().getOpenid();
    }

    public static String getPrincipalName() {
        return getOAuth2AuthenticationToken().getName();
    }

    public static String getClientRegistrationId() {
        return getOAuth2AuthenticationToken().getAuthorizedClientRegistrationId();
    }

    public static WeChatOAuth2User getOAuth2User() {
        return (WeChatOAuth2User) getOAuth2AuthenticationToken().getPrincipal();
    }

    public static OAuth2AuthenticationToken getOAuth2AuthenticationToken() {
        return (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

}

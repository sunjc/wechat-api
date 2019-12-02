package org.itrunner.wechat.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

public class WeChatUserRequestEntityConverter implements Converter<OAuth2UserRequest, RequestEntity<?>> {
    private static final String USER_INFO_URL_FORMAT = "%s?access_token=%s&openid=%s&lang=%s";

    @Override
    public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));

        URI uri = buildUri(userRequest);
        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }

    private URI buildUri(OAuth2UserRequest userRequest) {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String uri = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String openId = (String) userRequest.getAdditionalParameters().get("openid");

        String userInfoUrl = String.format(USER_INFO_URL_FORMAT, uri, accessToken, openId, "zh_CN");
        return UriComponentsBuilder.fromUriString(userInfoUrl).build().toUri();
    }
}

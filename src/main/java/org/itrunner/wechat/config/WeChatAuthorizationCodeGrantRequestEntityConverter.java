package org.itrunner.wechat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Slf4j
public class WeChatAuthorizationCodeGrantRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {
    private static final String ACCESS_TOKEN_URL_FORMAT = "%s?appid=%s&secret=%s&code=%s&grant_type=%s";

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        HttpHeaders headers = getDefaultTokenRequestHeaders();
        URI uri = buildUri(authorizationCodeGrantRequest);

        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }

    private URI buildUri(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();
        String appid = clientRegistration.getClientId();
        String secret = clientRegistration.getClientSecret();
        String code = authorizationCodeGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode();
        String grantType = authorizationCodeGrantRequest.getGrantType().getValue();

        String uriString = String.format(ACCESS_TOKEN_URL_FORMAT, tokenUri, appid, secret, code, grantType);
        return UriComponentsBuilder.fromUriString(uriString).build().toUri();
    }

    private static HttpHeaders getDefaultTokenRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        return headers;
    }
}

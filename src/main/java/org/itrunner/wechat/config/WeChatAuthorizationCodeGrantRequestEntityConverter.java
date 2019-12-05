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

import static org.itrunner.wechat.config.WeChatConstants.WEIXIN_ACCESS_TOKEN_URL_FORMAT;

@Slf4j
public class WeChatAuthorizationCodeGrantRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {
    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        HttpHeaders headers = getTokenRequestHeaders();
        URI uri = buildUri(authorizationCodeGrantRequest);
        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }

    private HttpHeaders getTokenRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
        return headers;
    }

    private URI buildUri(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();
        String appid = clientRegistration.getClientId();
        String secret = clientRegistration.getClientSecret();
        String code = authorizationCodeGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode();
        String grantType = authorizationCodeGrantRequest.getGrantType().getValue();

        String uriString = String.format(WEIXIN_ACCESS_TOKEN_URL_FORMAT, tokenUri, appid, secret, code, grantType);
        return UriComponentsBuilder.fromUriString(uriString).build().toUri();
    }
}

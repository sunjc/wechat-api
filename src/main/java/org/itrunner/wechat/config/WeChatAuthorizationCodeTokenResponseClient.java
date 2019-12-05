package org.itrunner.wechat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static org.itrunner.wechat.config.WeChatConstants.WEIXIN_REGISTRATION_ID;

@Slf4j
public class WeChatAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    private static final String INVALID_TOKEN_RESPONSE_ERROR_CODE = "invalid_token_response";

    private Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> requestEntityConverter = new WeChatAuthorizationCodeGrantRequestEntityConverter();

    private RestOperations restOperations;

    private DefaultAuthorizationCodeTokenResponseClient defaultAuthorizationCodeTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();

    public WeChatAuthorizationCodeTokenResponseClient() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        Assert.notNull(authorizationCodeGrantRequest, "authorizationCodeGrantRequest cannot be null");

        if (!authorizationCodeGrantRequest.getClientRegistration().getRegistrationId().equals(WEIXIN_REGISTRATION_ID)) {
            return defaultAuthorizationCodeTokenResponseClient.getTokenResponse(authorizationCodeGrantRequest);
        }

        RequestEntity<?> request = this.requestEntityConverter.convert(authorizationCodeGrantRequest);

        ResponseEntity<String> response;
        try {
            response = this.restOperations.exchange(request, String.class);
        } catch (RestClientException ex) {
            String description = "An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: ";
            log.error(description, ex);
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_TOKEN_RESPONSE_ERROR_CODE, description + ex.getMessage(), null);
            throw new OAuth2AuthorizationException(oauth2Error, ex);
        }

        OAuth2AccessTokenResponse tokenResponse = WeChatAccessTokenResponse.build(response.getBody()).toOAuth2AccessTokenResponse();

        if (CollectionUtils.isEmpty(tokenResponse.getAccessToken().getScopes())) {
            tokenResponse = OAuth2AccessTokenResponse.withResponse(tokenResponse)
                    .scopes(authorizationCodeGrantRequest.getClientRegistration().getScopes())
                    .build();
        }

        return tokenResponse;
    }
}

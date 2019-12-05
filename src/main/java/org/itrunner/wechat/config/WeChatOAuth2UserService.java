package org.itrunner.wechat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;

import static org.itrunner.wechat.config.WeChatConstants.WEIXIN_REGISTRATION_ID;

@Slf4j
public class WeChatOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private static final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";
    private static final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";
    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

    private Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter = new WeChatUserRequestEntityConverter();
    private RestOperations restOperations;

    private DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    public WeChatOAuth2UserService() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");

        if (!userRequest.getClientRegistration().getRegistrationId().equals(WEIXIN_REGISTRATION_ID)) {
            return defaultOAuth2UserService.loadUser(userRequest);
        }

        if (!StringUtils.hasText(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())) {
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
                    "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
                            + userRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (!StringUtils.hasText(userNameAttributeName)) {
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
                    "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                            + userRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        RequestEntity<?> request = this.requestEntityConverter.convert(userRequest);

        ResponseEntity<String> response;
        try {
            response = this.restOperations.exchange(request, String.class);
        } catch (OAuth2AuthorizationException ex) {
            OAuth2Error oauth2Error = ex.getError();
            StringBuilder errorDetails = new StringBuilder();
            errorDetails.append("Error details: [");
            errorDetails.append("UserInfo Uri: ").append(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri());
            errorDetails.append(", Error Code: ").append(oauth2Error.getErrorCode());
            if (oauth2Error.getDescription() != null) {
                errorDetails.append(", Error Description: ").append(oauth2Error.getDescription());
            }
            errorDetails.append("]");
            oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                    "An error occurred while attempting to retrieve the UserInfo Resource: " + errorDetails.toString(), null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        } catch (RestClientException ex) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                    "An error occurred while attempting to retrieve the UserInfo Resource: " + ex.getMessage(), null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        }

        String userAttributes = response.getBody();
        try {
            userAttributes = new String(userAttributes.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("An error occurred while attempting to encode userAttributes: " + e.getMessage());
        }
        return WeChatOAuth2User.build(userAttributes, userNameAttributeName);
    }
}

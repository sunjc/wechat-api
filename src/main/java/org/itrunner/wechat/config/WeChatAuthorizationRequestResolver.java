package org.itrunner.wechat.config;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

public class WeChatAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private static final String WEIXIN_AUTHORIZATION_REQUEST_URI_FORMAT = "%s?appid=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s#wechat_redirect";
    private static final String WEIXIN_DEFAULT_SCOPE = "snsapi_base";
    private static final String WEIXIN_REGISTRATION_ID = "weixin";
    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";

    private final OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver;
    private final AntPathRequestMatcher authorizationRequestMatcher;

    public WeChatAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultAuthorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
        this.authorizationRequestMatcher = new AntPathRequestMatcher(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String clientRegistrationId = this.resolveRegistrationId(request);

        OAuth2AuthorizationRequest authorizationRequest = this.defaultAuthorizationRequestResolver.resolve(request);

        return resolve(authorizationRequest, clientRegistrationId);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = this.defaultAuthorizationRequestResolver.resolve(request, clientRegistrationId);

        return resolve(authorizationRequest, clientRegistrationId);
    }

    private OAuth2AuthorizationRequest resolve(OAuth2AuthorizationRequest authorizationRequest, String registrationId) {
        if (authorizationRequest == null) {
            return null;
        }

        if (!WEIXIN_REGISTRATION_ID.equals(registrationId)) {
            return authorizationRequest;
        }

        String authorizationRequestUri = String.format(WEIXIN_AUTHORIZATION_REQUEST_URI_FORMAT, authorizationRequest.getAuthorizationUri(), authorizationRequest.getClientId(),
                encodeURL(authorizationRequest.getRedirectUri()), authorizationRequest.getResponseType().getValue(), getScope(authorizationRequest), authorizationRequest.getState());

        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.from(authorizationRequest);
        builder.authorizationRequestUri(authorizationRequestUri);

        return builder.build();
    }

    private String resolveRegistrationId(HttpServletRequest request) {
        if (this.authorizationRequestMatcher.matches(request)) {
            return this.authorizationRequestMatcher.matcher(request).getVariables().get(REGISTRATION_ID_URI_VARIABLE_NAME);
        }
        return null;
    }

    private static String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // The system should always have the platform default
            return null;
        }
    }

    private static String getScope(OAuth2AuthorizationRequest authorizationRequest) {
        return authorizationRequest.getScopes().stream().findFirst().orElse(WEIXIN_DEFAULT_SCOPE);
    }
}
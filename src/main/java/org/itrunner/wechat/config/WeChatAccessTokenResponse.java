package org.itrunner.wechat.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Slf4j
public class WeChatAccessTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String openid;
    private String scope;

    private WeChatAccessTokenResponse() {

    }

    public static WeChatAccessTokenResponse build(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, WeChatAccessTokenResponse.class);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while attempting to parse the weixin Access Token Response: " + e.getMessage());
            return null;
        }
    }

    public OAuth2AccessTokenResponse toOAuth2AccessTokenResponse() {
        OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse.withToken(accessToken);
        builder.tokenType(OAuth2AccessToken.TokenType.BEARER);
        builder.expiresIn(expiresIn);
        builder.refreshToken(refreshToken);
        String[] scopes = scope.split(",");

        Set<String> scopeSet = new HashSet<>();
        for (String scope : scopes) {
            scopeSet.add(scope);
        }
        builder.scopes(scopeSet);

        Map<String, Object> additionalParameters = new LinkedHashMap<>();
        additionalParameters.put("openid", openid);

        builder.additionalParameters(additionalParameters);
        return builder.build();
    }
}

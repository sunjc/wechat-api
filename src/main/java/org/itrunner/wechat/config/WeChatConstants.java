package org.itrunner.wechat.config;

public final class WeChatConstants {
    private WeChatConstants() {
    }

    public static final String WEIXIN_REGISTRATION_ID = "weixin";
    public static final String WEIXIN_AUTHORIZATION_REQUEST_URL_FORMAT = "%s?appid=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s#wechat_redirect";
    public static final String WEIXIN_ACCESS_TOKEN_URL_FORMAT = "%s?appid=%s&secret=%s&code=%s&grant_type=%s";
    public static final String WEIXIN_USER_INFO_URL_FORMAT = "%s?access_token=%s&openid=%s&lang=%s";
}

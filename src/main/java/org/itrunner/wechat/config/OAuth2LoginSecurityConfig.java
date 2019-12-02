package org.itrunner.wechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@EnableWebSecurity
public class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().headers().disable()
                .oauth2Login(oauth2Login ->
                        oauth2Login.authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.authorizationRequestResolver(new WeChatAuthorizationRequestResolver(this.clientRegistrationRepository))
                        ).tokenEndpoint(tokenEndpoint ->
                                tokenEndpoint.accessTokenResponseClient(new WeChatAuthorizationCodeTokenResponseClient())
                        ).userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint.userService(new WeChatOAuth2UserService()))
                ).authorizeRequests(authorizeRequests ->
                authorizeRequests.anyRequest().authenticated());
    }

}
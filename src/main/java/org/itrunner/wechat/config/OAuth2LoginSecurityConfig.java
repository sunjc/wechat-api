package org.itrunner.wechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@EnableWebSecurity
public class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${security.ignore-paths}")
    private String[] ignorePaths;

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2LoginSecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(ignorePaths);
    }

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
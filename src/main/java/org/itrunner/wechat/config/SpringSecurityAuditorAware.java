package org.itrunner.wechat.config;

import org.itrunner.wechat.util.OAuth2Context;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(OAuth2Context.getOpenId());
    }
}

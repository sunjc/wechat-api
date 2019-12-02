package org.itrunner.wechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"org.itrunner.wechat.repository"})
@EntityScan(basePackages = {"org.itrunner.wechat.domain"})
@EnableJpaAuditing
public class WeChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeChatApplication.class, args);
    }
}

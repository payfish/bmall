package com.hmall.api.config;

import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    RequestInterceptor feignRequestInterceptor() {
        return requestTemplate -> {
            Long userId = UserContext.getUser();
            if (userId != null) {
                requestTemplate.header("user-id", userId.toString());
            }
        };
    }
}

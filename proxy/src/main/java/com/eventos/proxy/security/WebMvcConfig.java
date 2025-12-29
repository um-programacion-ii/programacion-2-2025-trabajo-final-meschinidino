package com.eventos.proxy.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ServiceAuthInterceptor serviceAuthInterceptor;

    public WebMvcConfig(ServiceAuthInterceptor serviceAuthInterceptor) {
        this.serviceAuthInterceptor = serviceAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serviceAuthInterceptor)
                .addPathPatterns("/api/proxy/**");
    }
}

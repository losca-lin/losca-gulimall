package com.losca.gulimall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许哪些头跨域
        corsConfiguration.addAllowedHeader("*");
        // 允许哪些方式跨域   get  post  delete 等方式
        corsConfiguration.addAllowedMethod("*");
        //允许哪些请求来源跨域    *  任意来源
        corsConfiguration.addAllowedOrigin("*");
        // 是否允许携带cooker跨域
        corsConfiguration.setAllowCredentials(true);
        //注册跨越配置       /**配置请求路径
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }



}

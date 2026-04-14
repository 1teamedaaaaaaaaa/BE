package com.hoppin.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())   // WebConfig의 CORS 설정 사용
                .csrf(csrf -> csrf.disable())      // 프론트-백 분리 개발 중이면 보통 일단 꺼둠
                .formLogin(form -> form.disable()) // React에서 직접 로그인 처리하면 비활성화
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/public/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
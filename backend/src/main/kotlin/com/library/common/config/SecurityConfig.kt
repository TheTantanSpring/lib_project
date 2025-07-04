package com.library.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/api/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .anyRequest().permitAll()
            }
            .csrf { csrf -> csrf.disable() }
            .formLogin { form -> form.disable() }
            .httpBasic { basic -> basic.disable() }
            // CORS 설정을 활성화하고 상세 정책을 구성합니다.
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
        
        return http.build()
    }

    // CORS 정책을 정의하는 Bean
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        
        // 프론트엔드 개발 서버의 주소인 http://localhost:3000을 허용된 출처로 추가합니다.
        configuration.allowedOrigins = listOf("http://localhost:3000")
        
        // 허용할 HTTP 메서드를 지정합니다. (GET, POST, PUT, DELETE 등)
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        
        // 허용할 HTTP 헤더를 지정합니다.
        configuration.allowedHeaders = listOf("*")
        
        // 자격 증명(쿠키, 인증 헤더 등)을 허용합니다.
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        // 모든 경로(/api/**)에 대해 위에서 정의한 CORS 정책을 적용합니다.
        source.registerCorsConfiguration("/api/**", configuration)
        
        return source
    }
} 
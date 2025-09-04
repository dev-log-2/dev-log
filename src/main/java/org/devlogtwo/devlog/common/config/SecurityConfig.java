package org.devlogtwo.devlog.common.config;

import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF(Cross-Site Request Forgery) 보호 비활성화
        // JWT 기반의 stateless API에서는 CSRF 보호가 불필요하므로 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 세션 관리 방식을 Stateless로 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // API 엔드포인트별 접근 권한 설정
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/**").permitAll()
//                        // '/api/auth/'로 시작하는 모든 요청은 인증 없이 허용
//                        .requestMatchers("/api/auth/**").permitAll()
//                        // 그 외 모든 요청은 인증이 필요함
//                        .anyRequest().authenticated()
        );

        // JWT 인증 필터 추가
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


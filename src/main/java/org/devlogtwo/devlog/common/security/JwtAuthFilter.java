package org.devlogtwo.devlog.common.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Header로부터 Token 추출
            String authHeaderValue = getAuthHeaderValue(request);
            String bearerToken = getToken(authHeaderValue);

            // 토큰 존재 여부 확인
            if (bearerToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 검증
            Claims claims = jwtTokenProvider.validateToken(bearerToken);
            String currentUsername = claims.getSubject();
            log.debug("[JwtAuthFilter] JWT Authenticated user: " + currentUsername);

            // 사용자 정보 추출 및 인증 객체 생성
            setAuthentication(currentUsername);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }

        // 필터 보내기
        filterChain.doFilter(request, response);
    }

    private String getAuthHeaderValue(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    private String getToken(String authHeaderValue) {
        if (StringUtils.hasText(authHeaderValue) && authHeaderValue.startsWith(BEARER_PREFIX)) {
            return authHeaderValue.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    private void setAuthentication(String username) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 1. 사용자 정보(UserDetails) 조회
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

        // 2. 비밀번호 접근 방지를 위해 별도의 JwtUserDetails가 아닌 UserPrincipal 객체 생성
        User user = ((JwtUserDetails) userDetails).getUser();
        UserPrincipal principal = UserPrincipal.from(user);

        // 3. Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
                userDetails.getAuthorities());

        // 4. SecurityContext에 저장
        context.setAuthentication(authentication);

        // 5. SecurityContextHolder에 최종 저장
        SecurityContextHolder.setContext(context);
        log.debug("[JwtAuthFilter] @AuthenticationPrincipal 생성 성공: " + principal.username());
    }
}

package org.devlogtwo.devlog.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

            String authHeaderValue = getAuthHeaderValue(request);
            String bearerToken = getToken(authHeaderValue);

            if (bearerToken == null) {
                log.warn("[JwtAuthFilter] 토큰이 존재하지 않는 요청입니다.");
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 검증
            log.debug("[JwtAuthFilter] 인증 요청 토큰: JWT Authenticated token: " + bearerToken);
            Claims claims = jwtTokenProvider.validateToken(bearerToken);
            String currentUsername = claims.getSubject();
            log.debug("[JwtAuthFilter] 인증 요청 아이디: JWT Authenticated user: " + currentUsername);

            // 사용자 정보 추출 및 인증 객체 생성
            setAuthentication(currentUsername);
        } catch (ExpiredJwtException e) {
            log.warn("[JwtAuthFilter] 인증 실패: 만료된 토큰 - username={}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null,
                    new CustomBusinessException(ErrorCode.EXPIRED_TOKEN));
            return;
        } catch (UsernameNotFoundException e) { // 토큰은 유효하지만 DB에 사용자가 없는 경우 -> 필터체인에서 url에 따라 허용 판단
            log.warn("[JwtAuthFilter] 인증 보류: 회원가입과 로그인은 수행가능합니다. 보류 토큰 - username={}", e.getMessage());
        } catch (MalformedJwtException e) { // 토큰의 형식이 올바르지 않은 경우
            log.warn("[JwtAuthFilter] 인증 실패: 유효하지 않은 토큰 - username={}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null,
                    new CustomBusinessException(ErrorCode.INVALID_TOKEN));
            return;
        } catch (SignatureException e) { // 토큰의 시그니처가 올바르지 않은 경우
            log.warn("[JwtAuthFilter] 인증 실패: 유효하지 않은 토큰 - username={}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null,
                    new CustomBusinessException(ErrorCode.INVALID_TOKEN));
            return;
        } catch (Exception e) {
            log.warn("[JwtAuthFilter] 인증 실패: 토큰 인증 과정 중 오류 발생 - {}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null,
                    new CustomBusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
            return;
        }

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

        // 요청마다 사용자 정보를 최신 정보로 갱신하기 위해 DB 조회 -> 회원 탈퇴시 토큰을 만료시키는 로직도 필요 없어짐
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        // 비밀번호 접근 방지를 위해 별도의 JwtUserDetails가 아닌 UserPrincipal 객체 생성
        User user = ((JwtUserDetails) userDetails).getUser();
        UserPrincipal principal = UserPrincipal.from(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                userDetails.getAuthorities());
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        log.debug("[JwtAuthFilter] @AuthenticationPrincipal 생성 성공: " + principal.username());
    }
}

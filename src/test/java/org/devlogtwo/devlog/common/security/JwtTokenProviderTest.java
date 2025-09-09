package org.devlogtwo.devlog.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

    private static final String BASE64_SECRET = "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE="; // 32 bytes Base64
    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "secretKeyString", BASE64_SECRET);
        ReflectionTestUtils.setField(provider, "tokenExpirationTime", 3600_000L); // 1 hour
        provider.init();
    }

    @Test
    @DisplayName("정상 토큰 생성 및 검증에 성공한다")
    void createAndValidate_Success() {
        String token = provider.createToken("tester");
        Claims claims = provider.validateToken(token);
        assertThat(claims.getSubject()).isEqualTo("tester");
        assertThat(claims.get("username", String.class)).isEqualTo("tester");
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 ExpiredJwtException이 발생한다")
    void validate_ExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(BASE64_SECRET));
        Instant now = Instant.now();
        String expiredToken = Jwts.builder()
                .subject("oldUser")
                .issuedAt(Date.from(now.minusSeconds(3600)))
                .expiration(Date.from(now.minusSeconds(10)))
                .signWith(key)
                .compact();

        assertThatThrownBy(() -> provider.validateToken(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("서명이 다른 토큰 검증 시 SignatureException이 발생한다")
    void validate_InvalidSignature() {
        SecretKey otherKey = Keys.hmacShaKeyFor(
                java.util.Base64.getDecoder().decode("QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo0NTY3ODkwMTI="));
        Instant now = Instant.now();
        String otherSigned = Jwts.builder()
                .subject("tester")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(60)))
                .signWith(otherKey)
                .compact();

        assertThatThrownBy(() -> provider.validateToken(otherSigned))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("형식이 잘못된 토큰 검증 시 MalformedJwtException이 발생한다")
    void validate_MalformedToken() {
        String malformed = "this.is.not.valid.jwt";
        assertThatThrownBy(() -> provider.validateToken(malformed))
                .isInstanceOf(MalformedJwtException.class);
    }
}


package org.devlogtwo.devlog.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // Token 식별자
    private final static String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret.key}")
    private String secretKeyString;
    @Value("${jwt.expiration}")
    private long tokenExpirationTime;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }


    public String createToken(Long userId, User user) {

        Claims claims = Jwts.claims()
                .add("userId", userId)
                .add("role", user.getRole().name())
                .build();

        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(Instant.ofEpochMilli(tokenExpirationTime)))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

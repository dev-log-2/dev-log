package org.devlogtwo.devlog.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("SecurityConfig & JwtAuthFilter 통합 테스트")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private User saveUser(String username, UserRole role) {
        User user = User.signUp(username, username + "이름", username + "@example.com", "encodedPass", role);
        return userRepository.save(user);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    @Nested
    @DisplayName("인증 필요 여부 검증")
    class AuthenticationRequired {
        @Test
        @DisplayName("토큰 없이 보호된 엔드포인트 접근 시 401을 반환한다")
        void protectedWithoutToken() throws Exception {
            mockMvc.perform(get("/api/users/me")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("회원가입 공개 엔드포인트는 토큰 없이 400(검증 실패) 응답 (401 아님)")
        void registerPublicEndpointAccessible() throws Exception {
            mockMvc.perform(
                            post("/api/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("로그인 공개 엔드포인트는 토큰 없이 400(검증 실패) 응답 (401 아님)")
        void loginPublicEndpointAccessible() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("정상 토큰")
    class ValidToken {
        @Test
        @DisplayName("정상 토큰으로 /api/users/me 접근 시 200")
        void accessWithValidToken() throws Exception {
            User user = saveUser("validUser", UserRole.USER);
            String token = jwtTokenProvider.createToken(user.getUsername());

            mockMvc.perform(get("/api/users/me")
                            .header("Authorization", bearer(token))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.username").value(user.getUsername()));
        }

        @Test
        @DisplayName("동일 토큰 재사용(로그아웃 미적용)시에도 계속 접근 가능하다")
        void tokenReuse() throws Exception {
            User user = saveUser("reuseUser", UserRole.USER);
            String token = jwtTokenProvider.createToken(user.getUsername());

            // 1차 호출
            mockMvc.perform(get("/api/users/me")
                            .header("Authorization", bearer(token)))
                    .andExpect(status().isOk());
            // 2차 동일 토큰 호출
            mockMvc.perform(get("/api/users/me")
                            .header("Authorization", bearer(token)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("비정상 토큰")
    class InvalidTokenCases {

        @Test
        @DisplayName("만료된 토큰으로 접근 시 401 및 success=false")
        void expiredToken() throws Exception {
            User user = saveUser("expiredUser", UserRole.USER);
            // 동일 secretKey 로 직접 만료 토큰 생성
            String secretKeyString = (String) ReflectionTestUtils.getField(jwtTokenProvider, "secretKeyString");
            SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString));
            Instant now = Instant.now();
            String expired = Jwts.builder()
                    .subject(user.getUsername())
                    .issuedAt(Date.from(now.minusSeconds(3600)))
                    .expiration(Date.from(now.minusSeconds(10)))
                    .signWith(secretKey)
                    .compact();

            mockMvc.perform(get("/api/users/me")
                            .header("Authorization", bearer(expired)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("만료된 토큰입니다. 다시 로그인하세요."));
        }

        @Test
        @DisplayName("서명 키가 다른 토큰은 401")
        void signatureInvalid() throws Exception {
            User user = saveUser("sigUser", UserRole.USER);
            SecretKey otherKey = Keys.hmacShaKeyFor(
                    Base64.getDecoder().decode("QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo0NTY3ODkwMTI="));
            Instant now = Instant.now();
            String otherSigned = Jwts.builder()
                    .subject(user.getUsername())
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plusSeconds(60)))
                    .signWith(otherKey)
                    .compact();

            mockMvc.perform(get("/api/users/me")
                            .header("Authorization", bearer(otherSigned)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다. 다시 로그인하세요"));
        }

        @Test
        @DisplayName("형식이 잘못된 토큰은 401")
        void malformedToken() throws Exception {
            saveUser("malUser", UserRole.USER);
            mockMvc.perform(get("/api/users/me")
                            .header("Authorization", bearer("not.a.jwt")))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다. 다시 로그인하세요"));
        }

        @Test
        @DisplayName("Subject 누락 토큰은 인증 실패")
        void subjectMissingToken() throws Exception {
            saveUser("subMissUser", UserRole.USER);
            String secretKeyString = (String) ReflectionTestUtils.getField(jwtTokenProvider, "secretKeyString");
            SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString));
            Instant now = Instant.now();
            String noSub = Jwts.builder()
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plusSeconds(60)))
                    .signWith(secretKey)
                    .compact();

            mockMvc.perform(get("/api/users/me")
                            .header("Authorization", bearer(noSub)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 username 토큰은 401")
        void tokenWithNonExistingUser() throws Exception {
            // 사용자는 생성하지 않음
            String token = jwtTokenProvider.createToken("ghostUser");
            mockMvc.perform(get("/api/users/me").header("Authorization", bearer(token)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("역할 관련 (현재 설정 상 차이 없음)")
    class RoleAccess {
        @Test
        @DisplayName("ADMIN 역할 유저도 동일하게 접근 가능 (Role 별 제약 아직 없음)")
        void adminAccessSameAsUser() throws Exception {
            User admin = saveUser("adminUser", UserRole.ADMIN);
            String token = jwtTokenProvider.createToken(admin.getUsername());
            String body = mockMvc.perform(get("/api/users/me").header("Authorization", bearer(token)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(body);
            assertThat(node.get("success").asBoolean()).isTrue();
        }
    }
}

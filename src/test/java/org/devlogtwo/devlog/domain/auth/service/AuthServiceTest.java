package org.devlogtwo.devlog.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.security.JwtTokenProvider;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.auth.dto.request.AuthLoginRequest;
import org.devlogtwo.devlog.domain.auth.dto.request.AuthRegisterRequest;
import org.devlogtwo.devlog.domain.auth.dto.request.AuthWithdrawRequest;
import org.devlogtwo.devlog.domain.auth.dto.response.AuthLoginResponse;
import org.devlogtwo.devlog.domain.auth.dto.response.AuthRegisterResponse;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.service.UserServiceApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserServiceApi userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("회원가입")
    class SignUp {

        @Test
        @DisplayName("사용자는 회원가입에 성공한다.")
        void signUp_Success() {
            // given
            AuthRegisterRequest request = new AuthRegisterRequest("newUser", "새유저", "new@example.com", "password123");
            User user = User.signUp(request.username(), request.name(), request.email(), "encodedPassword",
                    UserRole.USER);

            given(userService.isUsernameTaken(request.username())).willReturn(false);
            given(userService.isEmailTaken(request.email())).willReturn(false);
            given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");
            given(userService.registerNewUser(any(User.class))).willReturn(user);

            // when
            AuthRegisterResponse response = authService.signUp(request);

            // then
            assertThat(response.username()).isEqualTo(request.username());
            assertThat(response.email()).isEqualTo(request.email());
            verify(userService).registerNewUser(any(User.class));
        }

        @Test
        @DisplayName("사용자는 중복 아이디를 입력하면 회원가입에 실패한다.")
        void signUp_Fail_DuplicateUsername() {
            // given
            AuthRegisterRequest request = new AuthRegisterRequest("existUser", "유저", "new@example.com", "password123");
            given(userService.isUsernameTaken(request.username())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(CustomBusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_USERNAME);
        }

        @Test
        @DisplayName("사용자는 중복 이메일을 입력하면 회원가입에 실패한다.")
        void signUp_Fail_DuplicateEmail() {
            // given
            AuthRegisterRequest request = new AuthRegisterRequest("uniqueUserX", "이메일중복", "dup@example.com",
                    "Password1!");
            given(userService.isUsernameTaken(request.username())).willReturn(false);
            given(userService.isEmailTaken(request.email())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(CustomBusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("사용자는 로그인에 성공한다.")
        void login_Success() {
            // given
            AuthLoginRequest request = new AuthLoginRequest("testuser", "password123");
            User user = User.signUp(request.username(), "테스트유저", "test@example.com", "encodedPassword", UserRole.USER);
            String expectedToken = "sample.jwt.token";

            given(userService.findUserByUsername(request.username())).willReturn(user);
            given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(true);
            given(jwtTokenProvider.createToken(user.getUsername())).willReturn(expectedToken);

            // when
            AuthLoginResponse response = authService.login(request);

            // then
            assertThat(response.token()).isEqualTo(expectedToken);
        }

        @Test
        @DisplayName("사용자는 잘못된 비밀번호를 입력하면 로그인에 실패한다.")
        void login_Fail_InvalidCredentials() {
            // given
            AuthLoginRequest request = new AuthLoginRequest("testuser", "wrongPassword");
            User user = User.signUp(request.username(), "테스트유저", "test@example.com", "encodedPassword", UserRole.USER);

            given(userService.findUserByUsername(request.username())).willReturn(user);
            given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CustomBusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
        }

        @Test
        @DisplayName("존재하지 않는 사용자명으로 로그인 시 USER_NOT_FOUND")
        void login_Fail_UserNotFound() {
            // given
            AuthLoginRequest request = new AuthLoginRequest("ghostUserY", "Password1!");
            given(userService.findUserByUsername(request.username()))
                    .willThrow(new CustomBusinessException(ErrorCode.USER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CustomBusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class Withdraw {

        @Test
        @DisplayName("사용자는 회원 탈퇴를 할 수 있다.")
        void withdraw_Success() {
            // given
            Long userId = 1L;
            AuthWithdrawRequest request = new AuthWithdrawRequest("password123");
            User user = User.signUp("testuser", "테스트유저", "test@example.com", "encodedPassword", UserRole.USER);

            given(userService.findUserById(userId)).willReturn(user);
            given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(true);

            // when
            authService.withdraw(request, userId);

            // then
            verify(userService).deleteUser(user);
        }

        @Test
        @DisplayName("사용자는 비밀번호를 틀리게 입력할 경우 회원 탈퇴를 할 수 없다.")
        void withdraw_Fail_PasswordMismatch() {
            // given
            Long userId = 1L;
            AuthWithdrawRequest request = new AuthWithdrawRequest("wrongPassword");
            User user = User.signUp("testuser", "테스트유저", "test@example.com", "encodedPassword", UserRole.USER);

            given(userService.findUserById(userId)).willReturn(user);
            given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.withdraw(request, userId))
                    .isInstanceOf(CustomBusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 탈퇴 시 USER_NOT_FOUND")
        void withdraw_Fail_UserNotFound() {
            // given
            Long userId = 99999L;
            AuthWithdrawRequest request = new AuthWithdrawRequest("Password1!");
            given(userService.findUserById(userId)).willThrow(new CustomBusinessException(ErrorCode.USER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> authService.withdraw(request, userId))
                    .isInstanceOf(CustomBusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }
}

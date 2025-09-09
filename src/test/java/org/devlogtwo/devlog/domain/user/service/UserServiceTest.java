package org.devlogtwo.devlog.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.dto.response.UserDetailsResponse;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.signUp("testuser", "테스트유저", "test@example.com", "password", UserRole.USER);
    }

    @Nested
    @DisplayName("사용자는 userId를 통해 상세 정보를 조회할 수 있다.")
    class GetUserDetails {

        @Test
        @DisplayName("성공")
        void getUserDetails_Success() {
            // given
            Long userId = 1L;
            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));

            // when
            UserDetailsResponse response = userService.getUserDetails(userId);

            // then
            assertThat(response.username()).isEqualTo(testUser.getUsername());
            assertThat(response.email()).isEqualTo(testUser.getEmail());
        }

        @Test
        @DisplayName("실패 - 사용자를 찾을 수 없음")
        void getUserDetails_Fail_UserNotFound() {
            // given
            Long nonExistentUserId = 999L;
            given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserDetails(nonExistentUserId))
                    .isInstanceOf(CustomBusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @DisplayName("사용자는 회원가입할 수 있다.")
    class RegisterNewUser {
        @Test
        @DisplayName("성공")
        void registerNewUser_Success() {
            // given
            given(userRepository.save(any(User.class))).willReturn(testUser);

            // when
            User savedUser = userService.registerNewUser(testUser);

            // then
            verify(userRepository, times(1)).save(testUser);
            assertThat(savedUser).isEqualTo(testUser);
        }
    }

    @DisplayName("사용자는 회원 탈퇴를 할 수 있다.")
    class DeleteUser {
        @Test
        @DisplayName("성공")
        void deleteUser_Success() {
            // when
            userService.deleteUser(testUser);

            // then
            verify(userRepository, times(1)).delete(testUser);
        }
    }
}

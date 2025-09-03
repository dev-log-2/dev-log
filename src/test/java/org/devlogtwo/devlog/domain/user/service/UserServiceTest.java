package org.devlogtwo.devlog.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.common.type.UserRole;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("userId를 이용해 사용자를 조회할 수 있다.")
    void findUserById() {

        // given
        Long userId = 1L;
        User expectedUser = User.signUp(
                "testuser",
                "테스트유저",
                "test@example.com",
                "encodedPassword",
                UserRole.USER
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(expectedUser));

        // when
        User actualUser = userService.findUserById(userId);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
        assertThat(actualUser.getUsername()).isEqualTo("testuser");
        assertThat(actualUser.getName()).isEqualTo("테스트유저");
        assertThat(actualUser.getEmail()).isEqualTo("test@example.com");
        assertThat(actualUser.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("존재하지 않는 userId로 조회 시 예외가 발생한다.")
    void findUserById_UserNotFound() {

        // given
        Long nonExistentUserId = 999L;

        given(userRepository.findById(nonExistentUserId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findUserById(nonExistentUserId))
                .isInstanceOf(CustomBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}
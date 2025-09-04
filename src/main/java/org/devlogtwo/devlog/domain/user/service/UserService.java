package org.devlogtwo.devlog.domain.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.domain.user.dto.response.UserDetailsResponse;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceApi {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDetailsResponse getUserDetails(Long id) {

        User user = findUserById(id);
        return UserDetailsResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAvailableUsersForTeam(Long teamId) {
        return userRepository.findAvailableUsersForTeam(teamId);
    }
}

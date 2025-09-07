package org.devlogtwo.devlog.domain.user.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.devlogtwo.devlog.common.code.ErrorCode;
import org.devlogtwo.devlog.common.exception.CustomBusinessException;
import org.devlogtwo.devlog.domain.user.dto.response.UserAssignableResponse;
import org.devlogtwo.devlog.domain.user.dto.response.UserDetailsResponse;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.devlogtwo.devlog.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserServiceApi {

    private final UserRepository userRepository;

    public UserDetailsResponse getUserDetails(Long id) {

        User user = findUserById(id);
        return UserDetailsResponse.from(user);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAvailableUsersForTeam(Long teamId) {
        return userRepository.findAvailableUsersForTeam(teamId);
    }

    @Override
    public List<User> findAllByUsernameContainsOrNameContains(String username, String name) {
        return userRepository.findAllByUsernameContainsOrNameContains(username, name);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsernameIgnoringSoftDelete(username) > 0;
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmailIgnoringSoftDelete(email) > 0;
    }

    @Override
    @Transactional
    public User registerNewUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public List<UserAssignableResponse> getAssignableUsers() {

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserAssignableResponse::from)
                .toList();
    }
}

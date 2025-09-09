package org.devlogtwo.devlog.domain.user.service;

import java.util.List;
import java.util.Optional;
import org.devlogtwo.devlog.domain.user.entity.User;

public interface UserServiceApi {

    User findUserById(Long userId);

    User findUserByUsername(String username);

    List<User> getAvailableUsersForTeam(Long teamId);

    List<User> findAllByUsernameContainsOrNameContains(String username, String name);

    boolean isUsernameTaken(String username);

    boolean isEmailTaken(String email);

    User registerNewUser(User user);

    void deleteUser(User user);

    Optional<User> findByUsername(String username);

    User getReferenceById(Long userId);
}

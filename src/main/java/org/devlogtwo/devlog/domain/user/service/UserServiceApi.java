package org.devlogtwo.devlog.domain.user.service;

import java.util.List;
import org.devlogtwo.devlog.domain.user.entity.User;

public interface UserServiceApi {

    User findUserById(Long userId);

    User findUserByUsername(String username);

    List<User> getAvailableUsersForTeam(Long teamId);

    List<User> findAllByUsernameContainsOrNameContains(String username, String name);
}

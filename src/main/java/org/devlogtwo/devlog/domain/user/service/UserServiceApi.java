package org.devlogtwo.devlog.domain.user.service;

import org.devlogtwo.devlog.domain.user.entity.User;

public interface UserServiceApi {

    User findUserById(Long userId);

    User findUserByUsername(String username);
}

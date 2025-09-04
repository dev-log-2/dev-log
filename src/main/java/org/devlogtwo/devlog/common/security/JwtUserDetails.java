package org.devlogtwo.devlog.common.security;

import java.util.Collection;
import java.util.Collections;
import lombok.Builder;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * AuthenticationManager로 로그인 과정을 전환하면 로그인 과정에서만 사용 예정
 *
 * @param user
 */
@Builder
public record JwtUserDetails(User user) implements UserDetails {

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority()));
    }

    public User getUser() {
        return user;
    }
}

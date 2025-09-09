package org.devlogtwo.devlog.domain.auth.dto.request;

import org.devlogtwo.devlog.domain.user.validation.Password;

public record AuthWithdrawRequest(
        @Password
        String password
) {
}

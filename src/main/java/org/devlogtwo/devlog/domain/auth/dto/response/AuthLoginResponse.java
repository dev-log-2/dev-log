package org.devlogtwo.devlog.domain.auth.dto.response;

public record AuthLoginResponse(
        String token
) {
    public static AuthLoginResponse from(String token) {
        return new AuthLoginResponse(token);
    }
}

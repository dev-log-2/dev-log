package org.devlogtwo.devlog.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.devlogtwo.devlog.domain.user.validation.Password;

public record AuthLoginRequest(

        @NotBlank(message = "id는 필수 입력값입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영문과 숫자만 사용할 수 있습니다.")
        String username,

        @Password
        String password
) {

}

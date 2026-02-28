package com.whatshouldwedo.security.application.port.in.input.command;

import com.whatshouldwedo.core.dto.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginInput extends SelfValidating<LoginInput> {

    @NotBlank(message = "아이디는 필수입니다.")
    private final String serialId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private final String password;

    public LoginInput(
            String serialId,
            String password
    ) {
        this.serialId = serialId;
        this.password = password;
        this.validateSelf();
    }

    public static LoginInput of(
            String serialId,
            String password
    ) {
        return new LoginInput(
                serialId,
                password
        );
    }
}

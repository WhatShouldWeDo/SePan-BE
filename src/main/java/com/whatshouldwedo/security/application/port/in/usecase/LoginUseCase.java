package com.whatshouldwedo.security.application.port.in.usecase;

import com.whatshouldwedo.core.annotation.bean.UseCase;
import com.whatshouldwedo.security.application.port.in.input.command.LoginInput;
import com.whatshouldwedo.security.application.port.in.output.result.LoginResult;

@UseCase
public interface LoginUseCase {

    LoginResult execute(LoginInput input);
}

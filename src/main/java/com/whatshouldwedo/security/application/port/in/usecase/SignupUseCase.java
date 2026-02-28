package com.whatshouldwedo.security.application.port.in.usecase;

import com.whatshouldwedo.core.annotation.bean.UseCase;
import com.whatshouldwedo.security.application.port.in.input.command.SignupInput;
import com.whatshouldwedo.security.application.port.in.output.result.SignupResult;

@UseCase
public interface SignupUseCase {

    SignupResult execute(SignupInput input);
}

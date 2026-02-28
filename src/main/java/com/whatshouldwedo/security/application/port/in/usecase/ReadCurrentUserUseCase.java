package com.whatshouldwedo.security.application.port.in.usecase;

import com.whatshouldwedo.core.annotation.bean.UseCase;
import com.whatshouldwedo.security.application.port.in.input.query.ReadCurrentUserInput;
import com.whatshouldwedo.security.application.port.in.output.result.ReadCurrentUserResult;

@UseCase
public interface ReadCurrentUserUseCase {

    ReadCurrentUserResult execute(ReadCurrentUserInput input);
}

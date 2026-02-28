package com.whatshouldwedo.security.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.security.application.port.in.input.query.ReadCurrentUserInput;
import com.whatshouldwedo.security.application.port.in.output.result.ReadCurrentUserResult;
import com.whatshouldwedo.security.application.port.in.usecase.ReadCurrentUserUseCase;
import com.whatshouldwedo.user.application.port.out.UserRepository;
import com.whatshouldwedo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReadCurrentUserService implements ReadCurrentUserUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ReadCurrentUserResult execute(ReadCurrentUserInput input) {
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return ReadCurrentUserResult.from(user);
    }
}

package com.whatshouldwedo.security.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.security.application.port.in.input.command.SignupInput;
import com.whatshouldwedo.security.application.port.in.output.result.SignupResult;
import com.whatshouldwedo.security.application.port.in.usecase.SignupUseCase;
import com.whatshouldwedo.user.application.port.out.UserRepository;
import com.whatshouldwedo.user.domain.User;
import com.whatshouldwedo.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class SignupService implements SignupUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SignupResult execute(SignupInput input) {
        // 아이디 중복 체크
        if (userRepository.existsBySerialId(input.getSerialId())) {
            throw new CommonException(ErrorCode.AUTH_USERNAME_DUPLICATE);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(input.getPassword());

        // 사용자 생성
        User user = User.create(
                UserId.generate(),
                input.getSerialId(),
                encodedPassword,
                input.getNickname()
        );

        User savedUser = userRepository.save(user);
        return SignupResult.from(savedUser);
    }
}

package com.whatshouldwedo.security.adapter.in.web.command;

import com.whatshouldwedo.core.utility.HttpServletUtil;
import com.whatshouldwedo.core.utility.JsonWebTokenUtil;
import com.whatshouldwedo.security.adapter.in.web.dto.request.LoginRequestDto;
import com.whatshouldwedo.security.adapter.in.web.dto.request.SignupRequestDto;
import com.whatshouldwedo.security.application.dto.DefaultJsonWebTokenDto;
import com.whatshouldwedo.security.application.port.in.input.command.LoginInput;
import com.whatshouldwedo.security.application.port.in.input.command.SignupInput;
import com.whatshouldwedo.security.application.port.in.output.result.LoginResult;
import com.whatshouldwedo.security.application.port.in.output.result.SignupResult;
import com.whatshouldwedo.security.application.port.in.usecase.LoginUseCase;
import com.whatshouldwedo.security.application.port.in.usecase.SignupUseCase;
import com.whatshouldwedo.security.type.ESecurityRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthCommandController {

    private final SignupUseCase signupUseCase;
    private final LoginUseCase loginUseCase;
    private final JsonWebTokenUtil jsonWebTokenUtil;
    private final HttpServletUtil httpServletUtil;

    @PostMapping("/signup")
    public void signup(
            @RequestBody SignupRequestDto request,
            HttpServletResponse httpResponse
    ) throws IOException {
        SignupResult result = signupUseCase.execute(
                SignupInput.of(
                        request.serialId(),
                        request.password(),
                        request.nickname()
                )
        );

        DefaultJsonWebTokenDto tokenDto = jsonWebTokenUtil.generateDefaultJsonWebTokens(
                UUID.fromString(result.getUserId()),
                ESecurityRole.USER
        );

        httpServletUtil.onSuccessBodyResponseWithJWTCookie(httpResponse, tokenDto);
    }

    @PostMapping("/login")
    public void login(
            @RequestBody LoginRequestDto request,
            HttpServletResponse httpResponse
    ) throws IOException {
        LoginResult result = loginUseCase.execute(
                LoginInput.of(
                        request.serialId(),
                        request.password()
                )
        );

        DefaultJsonWebTokenDto tokenDto = jsonWebTokenUtil.generateDefaultJsonWebTokens(
                UUID.fromString(result.getUserId()),
                ESecurityRole.USER
        );

        httpServletUtil.onSuccessBodyResponseWithJWTCookie(httpResponse, tokenDto);
    }

    @PostMapping("/logout")
    public void logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws IOException {
        httpServletUtil.onSuccessBodyResponseWithDeletedJWTCookie(httpRequest, httpResponse);
    }
}

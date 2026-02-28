package com.whatshouldwedo.security.adapter.in.web.dto.request;

public record SignupRequestDto(
        String serialId,
        String password,
        String nickname
) {
}

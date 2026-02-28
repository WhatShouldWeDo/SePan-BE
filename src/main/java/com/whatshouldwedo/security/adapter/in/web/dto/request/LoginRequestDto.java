package com.whatshouldwedo.security.adapter.in.web.dto.request;

public record LoginRequestDto(
        String serialId,
        String password
) {
}

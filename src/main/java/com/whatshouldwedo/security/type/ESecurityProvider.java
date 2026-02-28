package com.whatshouldwedo.security.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ESecurityProvider {

    DEFAULT("기본", "Default")

    ;

    private final String koName;
    private final String enName;

    public static ESecurityProvider fromString(String value) {
        return switch (value.toUpperCase()) {
            case "DEFAULT" -> DEFAULT;
            default -> throw new IllegalArgumentException("잘못된 Provider입니다: " + value);
        };
    }
}

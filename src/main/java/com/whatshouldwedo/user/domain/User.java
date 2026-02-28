package com.whatshouldwedo.user.domain;

import com.whatshouldwedo.security.type.ESecurityProvider;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User Aggregate Root
 */
@Getter
public class User {
    private final UserId id;
    private final String serialId;
    private final String passwordHash;
    private final String nickname;

    private ESecurityProvider provider;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    private User(UserId id, String serialId, String passwordHash, String nickname) {
        this.id = Objects.requireNonNull(id);
        this.serialId = Objects.requireNonNull(serialId);
        this.passwordHash = passwordHash;
        this.nickname = Objects.requireNonNull(nickname);
        this.provider = ESecurityProvider.DEFAULT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static User create(UserId id, String serialId, String passwordHash, String nickname) {
        return new User(id, serialId, passwordHash, nickname);
    }

    public static User reconstitute(
            UserId id, String serialId, String passwordHash, String nickname,
            ESecurityProvider provider,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLoginAt) {
        User user = new User(id, serialId, passwordHash, nickname);
        user.provider = provider != null ? provider : ESecurityProvider.DEFAULT;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.lastLoginAt = lastLoginAt;
        return user;
    }

    /**
     * 로그인 기록
     */
    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getPassword() {
        return passwordHash;
    }

}

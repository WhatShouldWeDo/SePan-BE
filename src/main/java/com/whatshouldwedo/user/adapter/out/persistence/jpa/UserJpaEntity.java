package com.whatshouldwedo.user.adapter.out.persistence.jpa;

import com.whatshouldwedo.security.type.ESecurityProvider;
import com.whatshouldwedo.user.domain.User;
import com.whatshouldwedo.user.domain.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "serial_id", nullable = false, unique = true, length = 100)
    private String serialId;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private ESecurityProvider provider = ESecurityProvider.DEFAULT;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public static UserJpaEntity fromDomain(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.id = user.getId().getValue().toString();
        entity.serialId = user.getSerialId();
        entity.passwordHash = user.getPasswordHash();
        entity.nickname = user.getNickname();
        entity.provider = user.getProvider();
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();
        entity.lastLoginAt = user.getLastLoginAt();
        return entity;
    }

    public User toDomain() {
        return User.reconstitute(
                UserId.of(id),
                serialId,
                passwordHash,
                nickname,
                provider != null ? provider : ESecurityProvider.DEFAULT,
                createdAt,
                updatedAt,
                lastLoginAt
        );
    }
}

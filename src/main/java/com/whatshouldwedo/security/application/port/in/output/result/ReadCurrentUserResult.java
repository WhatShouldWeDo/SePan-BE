package com.whatshouldwedo.security.application.port.in.output.result;

import com.whatshouldwedo.core.dto.SelfValidating;
import com.whatshouldwedo.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReadCurrentUserResult extends SelfValidating<ReadCurrentUserResult> {

    private final String userId;

    private final String serialId;

    private final String nickname;

    private final LocalDateTime createdAt;

    public ReadCurrentUserResult(
            String userId,
            String serialId,
            String nickname,
            LocalDateTime createdAt
    ) {
        this.userId = userId;
        this.serialId = serialId;
        this.nickname = nickname;
        this.createdAt = createdAt;

        this.validateSelf();
    }

    public static ReadCurrentUserResult from(User user) {
        return new ReadCurrentUserResult(
                user.getId().getValue().toString(),
                user.getSerialId(),
                user.getNickname(),
                user.getCreatedAt()
        );
    }
}

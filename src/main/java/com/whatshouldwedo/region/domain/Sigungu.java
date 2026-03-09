package com.whatshouldwedo.region.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Sigungu {
    private final SigunguId id;
    private final SidoId sidoId;
    private final String code;
    private final String name;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Sigungu(SigunguId id, SidoId sidoId, String code, String name) {
        this.id = Objects.requireNonNull(id);
        this.sidoId = Objects.requireNonNull(sidoId);
        this.code = Objects.requireNonNull(code);
        this.name = Objects.requireNonNull(name);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Sigungu create(SigunguId id, SidoId sidoId, String code, String name) {
        return new Sigungu(id, sidoId, code, name);
    }

    public static Sigungu reconstitute(SigunguId id, SidoId sidoId, String code, String name,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        Sigungu sigungu = new Sigungu(id, sidoId, code, name);
        sigungu.createdAt = createdAt;
        sigungu.updatedAt = updatedAt;
        return sigungu;
    }
}

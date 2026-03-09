package com.whatshouldwedo.region.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Sido {
    private final SidoId id;
    private final String code;
    private final String name;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Sido(SidoId id, String code, String name) {
        this.id = Objects.requireNonNull(id);
        this.code = Objects.requireNonNull(code);
        this.name = Objects.requireNonNull(name);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Sido create(SidoId id, String code, String name) {
        return new Sido(id, code, name);
    }

    public static Sido reconstitute(SidoId id, String code, String name,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        Sido sido = new Sido(id, code, name);
        sido.createdAt = createdAt;
        sido.updatedAt = updatedAt;
        return sido;
    }
}

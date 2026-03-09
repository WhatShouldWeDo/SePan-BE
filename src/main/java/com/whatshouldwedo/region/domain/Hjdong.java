package com.whatshouldwedo.region.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Hjdong {
    private final HjdongId id;
    private final SigunguId sigunguId;
    private final HjdongVersionId versionId;
    private final String code;
    private final String name;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Hjdong(HjdongId id, SigunguId sigunguId, HjdongVersionId versionId,
                   String code, String name) {
        this.id = Objects.requireNonNull(id);
        this.sigunguId = Objects.requireNonNull(sigunguId);
        this.versionId = Objects.requireNonNull(versionId);
        this.code = Objects.requireNonNull(code);
        this.name = Objects.requireNonNull(name);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Hjdong create(HjdongId id, SigunguId sigunguId, HjdongVersionId versionId,
                                 String code, String name) {
        return new Hjdong(id, sigunguId, versionId, code, name);
    }

    public static Hjdong reconstitute(HjdongId id, SigunguId sigunguId, HjdongVersionId versionId,
                                       String code, String name,
                                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        Hjdong hjdong = new Hjdong(id, sigunguId, versionId, code, name);
        hjdong.createdAt = createdAt;
        hjdong.updatedAt = updatedAt;
        return hjdong;
    }
}

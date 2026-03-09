package com.whatshouldwedo.election.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class ElectoralDistrictHjdong {
    private final ElectoralDistrictHjdongId id;
    private final ElectoralDistrictId electoralDistrictId;
    private final String hjdongCode;

    private LocalDateTime createdAt;

    private ElectoralDistrictHjdong(ElectoralDistrictHjdongId id,
                                     ElectoralDistrictId electoralDistrictId,
                                     String hjdongCode) {
        this.id = Objects.requireNonNull(id);
        this.electoralDistrictId = Objects.requireNonNull(electoralDistrictId);
        this.hjdongCode = Objects.requireNonNull(hjdongCode);
        this.createdAt = LocalDateTime.now();
    }

    public static ElectoralDistrictHjdong create(ElectoralDistrictHjdongId id,
                                                   ElectoralDistrictId electoralDistrictId,
                                                   String hjdongCode) {
        return new ElectoralDistrictHjdong(id, electoralDistrictId, hjdongCode);
    }

    public static ElectoralDistrictHjdong reconstitute(ElectoralDistrictHjdongId id,
                                                         ElectoralDistrictId electoralDistrictId,
                                                         String hjdongCode, LocalDateTime createdAt) {
        ElectoralDistrictHjdong edh = new ElectoralDistrictHjdong(id, electoralDistrictId, hjdongCode);
        edh.createdAt = createdAt;
        return edh;
    }
}

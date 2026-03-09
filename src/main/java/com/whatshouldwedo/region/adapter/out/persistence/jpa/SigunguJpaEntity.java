package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import com.whatshouldwedo.region.domain.Sigungu;
import com.whatshouldwedo.region.domain.SidoId;
import com.whatshouldwedo.region.domain.SigunguId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sigungus")
public class SigunguJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "sido_id", nullable = false, length = 36)
    private String sidoId;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static SigunguJpaEntity fromDomain(Sigungu sigungu) {
        SigunguJpaEntity entity = new SigunguJpaEntity();
        entity.id = sigungu.getId().getValue().toString();
        entity.sidoId = sigungu.getSidoId().getValue().toString();
        entity.code = sigungu.getCode();
        entity.name = sigungu.getName();
        entity.createdAt = sigungu.getCreatedAt();
        entity.updatedAt = sigungu.getUpdatedAt();
        return entity;
    }

    public Sigungu toDomain() {
        return Sigungu.reconstitute(
                SigunguId.of(id),
                SidoId.of(sidoId),
                code,
                name,
                createdAt,
                updatedAt
        );
    }
}

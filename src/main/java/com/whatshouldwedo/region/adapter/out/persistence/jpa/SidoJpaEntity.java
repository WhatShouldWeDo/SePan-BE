package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import com.whatshouldwedo.region.domain.Sido;
import com.whatshouldwedo.region.domain.SidoId;
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
@Table(name = "sidos")
public class SidoJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static SidoJpaEntity fromDomain(Sido sido) {
        SidoJpaEntity entity = new SidoJpaEntity();
        entity.id = sido.getId().getValue().toString();
        entity.code = sido.getCode();
        entity.name = sido.getName();
        entity.createdAt = sido.getCreatedAt();
        entity.updatedAt = sido.getUpdatedAt();
        return entity;
    }

    public Sido toDomain() {
        return Sido.reconstitute(
                SidoId.of(id),
                code,
                name,
                createdAt,
                updatedAt
        );
    }
}

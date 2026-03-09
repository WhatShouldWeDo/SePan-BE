package com.whatshouldwedo.region.adapter.out.persistence.jpa;

import com.whatshouldwedo.region.domain.Hjdong;
import com.whatshouldwedo.region.domain.HjdongId;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import com.whatshouldwedo.region.domain.SigunguId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hjdongs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_hjdong_code_version", columnNames = {"code", "version_id"})
})
public class HjdongJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "sigungu_id", nullable = false, length = 36)
    private String sigunguId;

    @Column(name = "version_id", nullable = false, length = 36)
    private String versionId;

    @Column(name = "code", nullable = false, length = 15)
    private String code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static HjdongJpaEntity fromDomain(Hjdong hjdong) {
        HjdongJpaEntity entity = new HjdongJpaEntity();
        entity.id = hjdong.getId().getValue().toString();
        entity.sigunguId = hjdong.getSigunguId().getValue().toString();
        entity.versionId = hjdong.getVersionId().getValue().toString();
        entity.code = hjdong.getCode();
        entity.name = hjdong.getName();
        entity.createdAt = hjdong.getCreatedAt();
        entity.updatedAt = hjdong.getUpdatedAt();
        return entity;
    }

    public Hjdong toDomain() {
        return Hjdong.reconstitute(
                HjdongId.of(id),
                SigunguId.of(sigunguId),
                HjdongVersionId.of(versionId),
                code,
                name,
                createdAt,
                updatedAt
        );
    }
}

package com.whatshouldwedo.election.adapter.out.persistence.jpa;

import com.whatshouldwedo.election.domain.ElectionId;
import com.whatshouldwedo.election.domain.ElectoralDistrict;
import com.whatshouldwedo.election.domain.ElectoralDistrictId;
import com.whatshouldwedo.election.domain.type.EElectoralDistrictType;
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
@Table(name = "electoral_districts")
public class ElectoralDistrictJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "election_id", nullable = false, length = 36)
    private String electionId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "district_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EElectoralDistrictType districtType;

    @Column(name = "sido_code", length = 10)
    private String sidoCode;

    @Column(name = "sigungu_code", length = 10)
    private String sigunguCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static ElectoralDistrictJpaEntity fromDomain(ElectoralDistrict district) {
        ElectoralDistrictJpaEntity entity = new ElectoralDistrictJpaEntity();
        entity.id = district.getId().getValue().toString();
        entity.electionId = district.getElectionId().getValue().toString();
        entity.name = district.getName();
        entity.districtType = district.getDistrictType();
        entity.sidoCode = district.getSidoCode();
        entity.sigunguCode = district.getSigunguCode();
        entity.createdAt = district.getCreatedAt();
        entity.updatedAt = district.getUpdatedAt();
        return entity;
    }

    public ElectoralDistrict toDomain() {
        return ElectoralDistrict.reconstitute(
                ElectoralDistrictId.of(id), ElectionId.of(electionId),
                name, districtType, sidoCode, sigunguCode, createdAt, updatedAt
        );
    }
}

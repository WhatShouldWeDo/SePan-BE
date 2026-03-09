package com.whatshouldwedo.election.adapter.out.persistence.jpa;

import com.whatshouldwedo.election.domain.ElectoralDistrictHjdong;
import com.whatshouldwedo.election.domain.ElectoralDistrictHjdongId;
import com.whatshouldwedo.election.domain.ElectoralDistrictId;
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
@Table(name = "electoral_district_hjdongs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_district_hjdong", columnNames = {"electoral_district_id", "hjdong_code"})
})
public class ElectoralDistrictHjdongJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "electoral_district_id", nullable = false, length = 36)
    private String electoralDistrictId;

    @Column(name = "hjdong_code", nullable = false, length = 15)
    private String hjdongCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static ElectoralDistrictHjdongJpaEntity fromDomain(ElectoralDistrictHjdong edh) {
        ElectoralDistrictHjdongJpaEntity entity = new ElectoralDistrictHjdongJpaEntity();
        entity.id = edh.getId().getValue().toString();
        entity.electoralDistrictId = edh.getElectoralDistrictId().getValue().toString();
        entity.hjdongCode = edh.getHjdongCode();
        entity.createdAt = edh.getCreatedAt();
        return entity;
    }

    public ElectoralDistrictHjdong toDomain() {
        return ElectoralDistrictHjdong.reconstitute(
                ElectoralDistrictHjdongId.of(id),
                ElectoralDistrictId.of(electoralDistrictId),
                hjdongCode, createdAt
        );
    }
}

package com.whatshouldwedo.election.domain;

import com.whatshouldwedo.election.domain.type.EElectoralDistrictType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class ElectoralDistrict {
    private final ElectoralDistrictId id;
    private final ElectionId electionId;
    private final String name;
    private final EElectoralDistrictType districtType;
    private String sidoCode;
    private String sigunguCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ElectoralDistrict(ElectoralDistrictId id, ElectionId electionId,
                               String name, EElectoralDistrictType districtType) {
        this.id = Objects.requireNonNull(id);
        this.electionId = Objects.requireNonNull(electionId);
        this.name = Objects.requireNonNull(name);
        this.districtType = Objects.requireNonNull(districtType);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static ElectoralDistrict create(ElectoralDistrictId id, ElectionId electionId,
                                             String name, EElectoralDistrictType districtType,
                                             String sidoCode, String sigunguCode) {
        ElectoralDistrict district = new ElectoralDistrict(id, electionId, name, districtType);
        district.sidoCode = sidoCode;
        district.sigunguCode = sigunguCode;
        return district;
    }

    public static ElectoralDistrict reconstitute(ElectoralDistrictId id, ElectionId electionId,
                                                   String name, EElectoralDistrictType districtType,
                                                   String sidoCode, String sigunguCode,
                                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        ElectoralDistrict district = new ElectoralDistrict(id, electionId, name, districtType);
        district.sidoCode = sidoCode;
        district.sigunguCode = sigunguCode;
        district.createdAt = createdAt;
        district.updatedAt = updatedAt;
        return district;
    }
}

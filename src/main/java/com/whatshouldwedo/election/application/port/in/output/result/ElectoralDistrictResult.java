package com.whatshouldwedo.election.application.port.in.output.result;

import com.whatshouldwedo.election.domain.ElectoralDistrict;
import com.whatshouldwedo.election.domain.type.EElectoralDistrictType;
import lombok.Getter;

@Getter
public class ElectoralDistrictResult {
    private final String id;
    private final String electionId;
    private final String name;
    private final EElectoralDistrictType districtType;
    private final String sidoCode;
    private final String sigunguCode;

    private ElectoralDistrictResult(String id, String electionId, String name,
                                     EElectoralDistrictType districtType,
                                     String sidoCode, String sigunguCode) {
        this.id = id;
        this.electionId = electionId;
        this.name = name;
        this.districtType = districtType;
        this.sidoCode = sidoCode;
        this.sigunguCode = sigunguCode;
    }

    public static ElectoralDistrictResult from(ElectoralDistrict district) {
        return new ElectoralDistrictResult(
                district.getId().getValue().toString(),
                district.getElectionId().getValue().toString(),
                district.getName(),
                district.getDistrictType(),
                district.getSidoCode(),
                district.getSigunguCode()
        );
    }
}

package com.whatshouldwedo.election.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EElectoralDistrictType {
    SIDO_GOVERNOR("시도지사"),
    EDUCATION_SUPERINTENDENT("교육감"),
    MAYOR("시장/군수/구청장"),
    METROPOLITAN_COUNCIL("광역의원(지역구)"),
    METROPOLITAN_COUNCIL_PROPORTIONAL("광역의원(비례)"),
    BASIC_COUNCIL("기초의원(지역구)"),
    BASIC_COUNCIL_PROPORTIONAL("기초의원(비례)"),
    NATIONAL_ASSEMBLY("국회의원(지역구)"),
    NATIONAL_ASSEMBLY_PROPORTIONAL("국회의원(비례)");

    private final String description;
}

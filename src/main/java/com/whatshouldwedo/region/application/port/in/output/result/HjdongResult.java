package com.whatshouldwedo.region.application.port.in.output.result;

import com.whatshouldwedo.region.domain.Hjdong;
import lombok.Getter;

@Getter
public class HjdongResult {
    private final String id;
    private final String sigunguId;
    private final String versionId;
    private final String code;
    private final String name;

    private HjdongResult(String id, String sigunguId, String versionId, String code, String name) {
        this.id = id;
        this.sigunguId = sigunguId;
        this.versionId = versionId;
        this.code = code;
        this.name = name;
    }

    public static HjdongResult from(Hjdong hjdong) {
        return new HjdongResult(
                hjdong.getId().getValue().toString(),
                hjdong.getSigunguId().getValue().toString(),
                hjdong.getVersionId().getValue().toString(),
                hjdong.getCode(),
                hjdong.getName()
        );
    }
}

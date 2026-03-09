package com.whatshouldwedo.region.application.port.in.output.result;

import com.whatshouldwedo.region.domain.Sigungu;
import lombok.Getter;

@Getter
public class SigunguResult {
    private final String id;
    private final String sidoId;
    private final String code;
    private final String name;

    private SigunguResult(String id, String sidoId, String code, String name) {
        this.id = id;
        this.sidoId = sidoId;
        this.code = code;
        this.name = name;
    }

    public static SigunguResult from(Sigungu sigungu) {
        return new SigunguResult(
                sigungu.getId().getValue().toString(),
                sigungu.getSidoId().getValue().toString(),
                sigungu.getCode(),
                sigungu.getName()
        );
    }
}

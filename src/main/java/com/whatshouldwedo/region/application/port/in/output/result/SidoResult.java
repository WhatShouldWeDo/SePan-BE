package com.whatshouldwedo.region.application.port.in.output.result;

import com.whatshouldwedo.region.domain.Sido;
import lombok.Getter;

@Getter
public class SidoResult {
    private final String id;
    private final String code;
    private final String name;

    private SidoResult(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public static SidoResult from(Sido sido) {
        return new SidoResult(
                sido.getId().getValue().toString(),
                sido.getCode(),
                sido.getName()
        );
    }
}

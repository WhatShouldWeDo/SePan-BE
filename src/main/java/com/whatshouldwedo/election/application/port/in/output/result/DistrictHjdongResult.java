package com.whatshouldwedo.election.application.port.in.output.result;

import com.whatshouldwedo.election.domain.ElectoralDistrictHjdong;
import lombok.Getter;

@Getter
public class DistrictHjdongResult {
    private final String id;
    private final String electoralDistrictId;
    private final String hjdongCode;

    private DistrictHjdongResult(String id, String electoralDistrictId, String hjdongCode) {
        this.id = id;
        this.electoralDistrictId = electoralDistrictId;
        this.hjdongCode = hjdongCode;
    }

    public static DistrictHjdongResult from(ElectoralDistrictHjdong edh) {
        return new DistrictHjdongResult(
                edh.getId().getValue().toString(),
                edh.getElectoralDistrictId().getValue().toString(),
                edh.getHjdongCode()
        );
    }
}

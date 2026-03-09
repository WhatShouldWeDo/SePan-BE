package com.whatshouldwedo.election.application.port.in.input.command;

import com.whatshouldwedo.core.dto.SelfValidating;
import com.whatshouldwedo.election.domain.type.EElectoralDistrictType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateElectoralDistrictInput extends SelfValidating<CreateElectoralDistrictInput> {

    @NotBlank(message = "선거구명은 필수입니다.")
    private final String name;

    @NotNull(message = "선거구 유형은 필수입니다.")
    private final EElectoralDistrictType districtType;

    private final String sidoCode;
    private final String sigunguCode;

    public CreateElectoralDistrictInput(String name, EElectoralDistrictType districtType,
                                         String sidoCode, String sigunguCode) {
        this.name = name;
        this.districtType = districtType;
        this.sidoCode = sidoCode;
        this.sigunguCode = sigunguCode;
        this.validateSelf();
    }

    public static CreateElectoralDistrictInput of(String name, EElectoralDistrictType districtType,
                                                    String sidoCode, String sigunguCode) {
        return new CreateElectoralDistrictInput(name, districtType, sidoCode, sigunguCode);
    }
}

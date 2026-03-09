package com.whatshouldwedo.region.application.port.in.input.command;

import com.whatshouldwedo.core.dto.SelfValidating;
import com.whatshouldwedo.region.domain.type.EMappingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateHjdongMappingInput extends SelfValidating<CreateHjdongMappingInput> {

    @NotBlank(message = "소스 버전 ID는 필수입니다.")
    private final String sourceVersionId;

    @NotBlank(message = "소스 행정동 코드는 필수입니다.")
    private final String sourceHjdongCode;

    @NotBlank(message = "타겟 행정동 코드는 필수입니다.")
    private final String targetHjdongCode;

    @NotNull(message = "매핑 타입은 필수입니다.")
    private final EMappingType mappingType;

    private final Double ratio;

    private final String description;

    public CreateHjdongMappingInput(String sourceVersionId, String sourceHjdongCode,
                                     String targetHjdongCode, EMappingType mappingType,
                                     Double ratio, String description) {
        this.sourceVersionId = sourceVersionId;
        this.sourceHjdongCode = sourceHjdongCode;
        this.targetHjdongCode = targetHjdongCode;
        this.mappingType = mappingType;
        this.ratio = ratio;
        this.description = description;
        this.validateSelf();
    }

    public static CreateHjdongMappingInput of(String sourceVersionId, String sourceHjdongCode,
                                                String targetHjdongCode, EMappingType mappingType,
                                                Double ratio, String description) {
        return new CreateHjdongMappingInput(sourceVersionId, sourceHjdongCode,
                targetHjdongCode, mappingType, ratio, description);
    }
}

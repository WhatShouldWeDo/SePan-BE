package com.whatshouldwedo.region.application.port.in.input.command;

import com.whatshouldwedo.core.dto.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateSigunguInput extends SelfValidating<CreateSigunguInput> {

    @NotBlank(message = "시도 ID는 필수입니다.")
    private final String sidoId;

    @NotBlank(message = "시군구 코드는 필수입니다.")
    @Size(max = 10, message = "시군구 코드는 10자 이내여야 합니다.")
    private final String code;

    @NotBlank(message = "시군구명은 필수입니다.")
    @Size(max = 50, message = "시군구명은 50자 이내여야 합니다.")
    private final String name;

    public CreateSigunguInput(String sidoId, String code, String name) {
        this.sidoId = sidoId;
        this.code = code;
        this.name = name;
        this.validateSelf();
    }

    public static CreateSigunguInput of(String sidoId, String code, String name) {
        return new CreateSigunguInput(sidoId, code, name);
    }
}

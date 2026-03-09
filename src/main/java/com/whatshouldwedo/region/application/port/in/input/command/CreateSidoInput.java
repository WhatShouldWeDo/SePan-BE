package com.whatshouldwedo.region.application.port.in.input.command;

import com.whatshouldwedo.core.dto.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateSidoInput extends SelfValidating<CreateSidoInput> {

    @NotBlank(message = "시도 코드는 필수입니다.")
    @Size(max = 10, message = "시도 코드는 10자 이내여야 합니다.")
    private final String code;

    @NotBlank(message = "시도명은 필수입니다.")
    @Size(max = 50, message = "시도명은 50자 이내여야 합니다.")
    private final String name;

    public CreateSidoInput(String code, String name) {
        this.code = code;
        this.name = name;
        this.validateSelf();
    }

    public static CreateSidoInput of(String code, String name) {
        return new CreateSidoInput(code, name);
    }
}

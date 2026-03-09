package com.whatshouldwedo.region.application.port.in.input.command;

import com.whatshouldwedo.core.dto.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateHjdongVersionInput extends SelfValidating<CreateHjdongVersionInput> {

    @NotBlank(message = "버전명은 필수입니다.")
    @Size(max = 30, message = "버전명은 30자 이내여야 합니다.")
    private final String versionName;

    @NotNull(message = "시행일은 필수입니다.")
    private final LocalDate effectiveDate;

    @Size(max = 500, message = "설명은 500자 이내여야 합니다.")
    private final String description;

    public CreateHjdongVersionInput(String versionName, LocalDate effectiveDate, String description) {
        this.versionName = versionName;
        this.effectiveDate = effectiveDate;
        this.description = description;
        this.validateSelf();
    }

    public static CreateHjdongVersionInput of(String versionName, LocalDate effectiveDate, String description) {
        return new CreateHjdongVersionInput(versionName, effectiveDate, description);
    }
}

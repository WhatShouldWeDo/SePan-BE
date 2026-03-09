package com.whatshouldwedo.region.application.port.in.input.command;

import com.whatshouldwedo.core.dto.SelfValidating;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateHjdongMappingBulkInput extends SelfValidating<CreateHjdongMappingBulkInput> {

    @NotEmpty(message = "매핑 목록은 비어있을 수 없습니다.")
    @Valid
    private final List<@NotNull CreateHjdongMappingInput> mappings;

    public CreateHjdongMappingBulkInput(List<CreateHjdongMappingInput> mappings) {
        this.mappings = mappings;
        this.validateSelf();
    }

    public static CreateHjdongMappingBulkInput of(List<CreateHjdongMappingInput> mappings) {
        return new CreateHjdongMappingBulkInput(mappings);
    }
}

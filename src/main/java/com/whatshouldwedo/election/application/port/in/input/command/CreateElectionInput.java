package com.whatshouldwedo.election.application.port.in.input.command;

import com.whatshouldwedo.core.dto.SelfValidating;
import com.whatshouldwedo.election.domain.type.EElectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateElectionInput extends SelfValidating<CreateElectionInput> {

    @NotBlank(message = "선거명은 필수입니다.")
    private final String name;

    @NotNull(message = "선거 유형은 필수입니다.")
    private final EElectionType electionType;

    @NotNull(message = "선거일은 필수입니다.")
    private final LocalDate electionDate;

    @NotBlank(message = "행정동 버전 ID는 필수입니다.")
    private final String hjdongVersionId;

    private final String description;

    public CreateElectionInput(String name, EElectionType electionType, LocalDate electionDate,
                                String hjdongVersionId, String description) {
        this.name = name;
        this.electionType = electionType;
        this.electionDate = electionDate;
        this.hjdongVersionId = hjdongVersionId;
        this.description = description;
        this.validateSelf();
    }

    public static CreateElectionInput of(String name, EElectionType electionType, LocalDate electionDate,
                                           String hjdongVersionId, String description) {
        return new CreateElectionInput(name, electionType, electionDate, hjdongVersionId, description);
    }
}

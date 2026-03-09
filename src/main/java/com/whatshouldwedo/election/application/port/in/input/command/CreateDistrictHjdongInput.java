package com.whatshouldwedo.election.application.port.in.input.command;

import com.whatshouldwedo.core.dto.SelfValidating;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateDistrictHjdongInput extends SelfValidating<CreateDistrictHjdongInput> {

    @NotEmpty(message = "행정동 코드 목록은 비어있을 수 없습니다.")
    private final List<String> hjdongCodes;

    public CreateDistrictHjdongInput(List<String> hjdongCodes) {
        this.hjdongCodes = hjdongCodes;
        this.validateSelf();
    }

    public static CreateDistrictHjdongInput of(List<String> hjdongCodes) {
        return new CreateDistrictHjdongInput(hjdongCodes);
    }
}

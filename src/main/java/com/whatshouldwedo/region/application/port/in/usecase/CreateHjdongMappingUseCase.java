package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.input.command.CreateHjdongMappingInput;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongMappingResult;

public interface CreateHjdongMappingUseCase {
    HjdongMappingResult execute(String versionId, CreateHjdongMappingInput input);
}

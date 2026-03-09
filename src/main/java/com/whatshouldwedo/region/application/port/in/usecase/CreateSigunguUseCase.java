package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.input.command.CreateSigunguInput;
import com.whatshouldwedo.region.application.port.in.output.result.SigunguResult;

public interface CreateSigunguUseCase {
    SigunguResult execute(CreateSigunguInput input);
}

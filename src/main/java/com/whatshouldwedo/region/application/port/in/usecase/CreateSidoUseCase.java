package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.input.command.CreateSidoInput;
import com.whatshouldwedo.region.application.port.in.output.result.SidoResult;

public interface CreateSidoUseCase {
    SidoResult execute(CreateSidoInput input);
}

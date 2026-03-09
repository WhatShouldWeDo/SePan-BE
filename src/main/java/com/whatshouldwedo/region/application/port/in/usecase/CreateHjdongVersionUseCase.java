package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.input.command.CreateHjdongVersionInput;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongVersionResult;

public interface CreateHjdongVersionUseCase {
    HjdongVersionResult execute(CreateHjdongVersionInput input);
}

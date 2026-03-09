package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.output.result.HjdongVersionResult;

public interface ActivateHjdongVersionUseCase {
    HjdongVersionResult execute(String versionId);
}

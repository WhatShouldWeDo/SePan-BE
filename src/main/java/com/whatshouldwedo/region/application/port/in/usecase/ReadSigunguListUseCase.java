package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.output.result.SigunguResult;

import java.util.List;

public interface ReadSigunguListUseCase {
    List<SigunguResult> execute(String sidoId);
}

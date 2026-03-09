package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.output.result.SidoResult;

import java.util.List;

public interface ReadSidoListUseCase {
    List<SidoResult> execute();
}

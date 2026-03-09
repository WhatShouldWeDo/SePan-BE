package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.output.result.HjdongVersionResult;

import java.util.List;

public interface ReadHjdongVersionListUseCase {
    List<HjdongVersionResult> execute();
}

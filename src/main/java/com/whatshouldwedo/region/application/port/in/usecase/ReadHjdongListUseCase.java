package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.output.result.HjdongResult;

import java.util.List;

public interface ReadHjdongListUseCase {
    List<HjdongResult> execute(String sigunguId);
}

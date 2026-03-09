package com.whatshouldwedo.region.application.port.in.usecase;

import com.whatshouldwedo.region.application.port.in.input.command.CreateHjdongMappingBulkInput;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongMappingResult;

import java.util.List;

public interface CreateHjdongMappingBulkUseCase {
    List<HjdongMappingResult> execute(String versionId, CreateHjdongMappingBulkInput input);
}

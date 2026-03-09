package com.whatshouldwedo.region.adapter.in.web.command;

import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.region.adapter.in.web.dto.request.CreateHjdongMappingBulkRequestDto;
import com.whatshouldwedo.region.adapter.in.web.dto.request.CreateHjdongMappingRequestDto;
import com.whatshouldwedo.region.adapter.in.web.dto.request.CreateHjdongVersionRequestDto;
import com.whatshouldwedo.region.application.port.in.input.command.CreateHjdongMappingBulkInput;
import com.whatshouldwedo.region.application.port.in.input.command.CreateHjdongMappingInput;
import com.whatshouldwedo.region.application.port.in.input.command.CreateHjdongVersionInput;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongMappingResult;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongVersionResult;
import com.whatshouldwedo.region.application.port.in.usecase.ActivateHjdongVersionUseCase;
import com.whatshouldwedo.region.application.port.in.usecase.CreateHjdongMappingBulkUseCase;
import com.whatshouldwedo.region.application.port.in.usecase.CreateHjdongMappingUseCase;
import com.whatshouldwedo.region.application.port.in.usecase.CreateHjdongVersionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hjdong-versions")
@RequiredArgsConstructor
public class HjdongVersionCommandController {

    private final CreateHjdongVersionUseCase createHjdongVersionUseCase;
    private final ActivateHjdongVersionUseCase activateHjdongVersionUseCase;
    private final CreateHjdongMappingUseCase createHjdongMappingUseCase;
    private final CreateHjdongMappingBulkUseCase createHjdongMappingBulkUseCase;

    @PostMapping
    public ResponseDto<HjdongVersionResult> createVersion(@RequestBody CreateHjdongVersionRequestDto request) {
        HjdongVersionResult result = createHjdongVersionUseCase.execute(
                CreateHjdongVersionInput.of(request.versionName(), request.effectiveDate(), request.description())
        );
        return ResponseDto.created(result);
    }

    @PatchMapping("/{versionId}/activate")
    public ResponseDto<HjdongVersionResult> activateVersion(@PathVariable String versionId) {
        return ResponseDto.ok(activateHjdongVersionUseCase.execute(versionId));
    }

    @PostMapping("/{versionId}/mappings")
    public ResponseDto<HjdongMappingResult> createMapping(
            @PathVariable String versionId,
            @RequestBody CreateHjdongMappingRequestDto request) {
        HjdongMappingResult result = createHjdongMappingUseCase.execute(versionId,
                CreateHjdongMappingInput.of(
                        request.sourceVersionId(), request.sourceHjdongCode(),
                        request.targetHjdongCode(), request.mappingType(),
                        request.ratio(), request.description()
                )
        );
        return ResponseDto.created(result);
    }

    @PostMapping("/{versionId}/mappings/bulk")
    public ResponseDto<List<HjdongMappingResult>> createMappingBulk(
            @PathVariable String versionId,
            @RequestBody CreateHjdongMappingBulkRequestDto request) {
        List<CreateHjdongMappingInput> inputs = request.mappings().stream()
                .map(m -> CreateHjdongMappingInput.of(
                        m.sourceVersionId(), m.sourceHjdongCode(),
                        m.targetHjdongCode(), m.mappingType(),
                        m.ratio(), m.description()
                )).toList();

        List<HjdongMappingResult> results = createHjdongMappingBulkUseCase.execute(
                versionId, CreateHjdongMappingBulkInput.of(inputs)
        );
        return ResponseDto.created(results);
    }
}

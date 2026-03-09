package com.whatshouldwedo.region.adapter.in.web.command;

import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.region.adapter.in.web.dto.request.CreateSidoRequestDto;
import com.whatshouldwedo.region.adapter.in.web.dto.request.CreateSigunguRequestDto;
import com.whatshouldwedo.region.application.port.in.input.command.CreateSidoInput;
import com.whatshouldwedo.region.application.port.in.input.command.CreateSigunguInput;
import com.whatshouldwedo.region.application.port.in.output.result.SidoResult;
import com.whatshouldwedo.region.application.port.in.output.result.SigunguResult;
import com.whatshouldwedo.region.application.port.in.usecase.CreateSidoUseCase;
import com.whatshouldwedo.region.application.port.in.usecase.CreateSigunguUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionCommandController {

    private final CreateSidoUseCase createSidoUseCase;
    private final CreateSigunguUseCase createSigunguUseCase;

    @PostMapping("/sido")
    public ResponseDto<SidoResult> createSido(@RequestBody CreateSidoRequestDto request) {
        SidoResult result = createSidoUseCase.execute(
                CreateSidoInput.of(request.code(), request.name())
        );
        return ResponseDto.created(result);
    }

    @PostMapping("/sigungu")
    public ResponseDto<SigunguResult> createSigungu(@RequestBody CreateSigunguRequestDto request) {
        SigunguResult result = createSigunguUseCase.execute(
                CreateSigunguInput.of(request.sidoId(), request.code(), request.name())
        );
        return ResponseDto.created(result);
    }
}

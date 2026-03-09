package com.whatshouldwedo.region.adapter.in.web.query;

import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongResult;
import com.whatshouldwedo.region.application.port.in.output.result.SidoResult;
import com.whatshouldwedo.region.application.port.in.output.result.SigunguResult;
import com.whatshouldwedo.region.application.port.in.usecase.ReadHjdongListUseCase;
import com.whatshouldwedo.region.application.port.in.usecase.ReadSidoListUseCase;
import com.whatshouldwedo.region.application.port.in.usecase.ReadSigunguListUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionQueryController {

    private final ReadSidoListUseCase readSidoListUseCase;
    private final ReadSigunguListUseCase readSigunguListUseCase;
    private final ReadHjdongListUseCase readHjdongListUseCase;

    @GetMapping("/sido")
    public ResponseDto<List<SidoResult>> getSidoList() {
        return ResponseDto.ok(readSidoListUseCase.execute());
    }

    @GetMapping("/sido/{sidoId}/sigungu")
    public ResponseDto<List<SigunguResult>> getSigunguList(@PathVariable String sidoId) {
        return ResponseDto.ok(readSigunguListUseCase.execute(sidoId));
    }

    @GetMapping("/sigungu/{sigunguId}/hjdong")
    public ResponseDto<List<HjdongResult>> getHjdongList(@PathVariable String sigunguId) {
        return ResponseDto.ok(readHjdongListUseCase.execute(sigunguId));
    }
}

package com.whatshouldwedo.region.adapter.in.web.query;

import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongVersionResult;
import com.whatshouldwedo.region.application.port.in.usecase.ReadHjdongVersionListUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hjdong-versions")
@RequiredArgsConstructor
public class HjdongVersionQueryController {

    private final ReadHjdongVersionListUseCase readHjdongVersionListUseCase;

    @GetMapping
    public ResponseDto<List<HjdongVersionResult>> getVersionList() {
        return ResponseDto.ok(readHjdongVersionListUseCase.execute());
    }
}

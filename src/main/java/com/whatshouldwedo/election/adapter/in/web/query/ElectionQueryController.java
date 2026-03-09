package com.whatshouldwedo.election.adapter.in.web.query;

import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.election.application.port.in.output.result.DistrictHjdongResult;
import com.whatshouldwedo.election.application.port.in.output.result.ElectionResult;
import com.whatshouldwedo.election.application.port.in.output.result.ElectoralDistrictResult;
import com.whatshouldwedo.election.application.port.in.usecase.ReadDistrictHjdongListUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.ReadElectionDetailUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.ReadElectionListUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.ReadElectoralDistrictListUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/elections")
@RequiredArgsConstructor
public class ElectionQueryController {

    private final ReadElectionListUseCase readElectionListUseCase;
    private final ReadElectionDetailUseCase readElectionDetailUseCase;
    private final ReadElectoralDistrictListUseCase readElectoralDistrictListUseCase;
    private final ReadDistrictHjdongListUseCase readDistrictHjdongListUseCase;

    @GetMapping
    public ResponseDto<List<ElectionResult>> getElections() {
        return ResponseDto.ok(readElectionListUseCase.execute());
    }

    @GetMapping("/{electionId}")
    public ResponseDto<ElectionResult> getElection(@PathVariable String electionId) {
        return ResponseDto.ok(readElectionDetailUseCase.execute(electionId));
    }

    @GetMapping("/{electionId}/districts")
    public ResponseDto<List<ElectoralDistrictResult>> getDistricts(@PathVariable String electionId) {
        return ResponseDto.ok(readElectoralDistrictListUseCase.execute(electionId));
    }

    @GetMapping("/{electionId}/districts/{districtId}/hjdongs")
    public ResponseDto<List<DistrictHjdongResult>> getDistrictHjdongs(
            @PathVariable String electionId,
            @PathVariable String districtId) {
        return ResponseDto.ok(readDistrictHjdongListUseCase.execute(electionId, districtId));
    }
}

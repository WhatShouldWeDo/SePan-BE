package com.whatshouldwedo.election.adapter.in.web.command;

import com.whatshouldwedo.core.dto.ResponseDto;
import com.whatshouldwedo.election.adapter.in.web.dto.request.CreateDistrictHjdongRequestDto;
import com.whatshouldwedo.election.adapter.in.web.dto.request.CreateElectionRequestDto;
import com.whatshouldwedo.election.adapter.in.web.dto.request.CreateElectoralDistrictRequestDto;
import com.whatshouldwedo.election.application.port.in.input.command.CreateDistrictHjdongInput;
import com.whatshouldwedo.election.application.port.in.input.command.CreateElectionInput;
import com.whatshouldwedo.election.application.port.in.input.command.CreateElectoralDistrictInput;
import com.whatshouldwedo.election.application.port.in.output.result.DistrictHjdongResult;
import com.whatshouldwedo.election.application.port.in.output.result.ElectionResult;
import com.whatshouldwedo.election.application.port.in.output.result.ElectoralDistrictResult;
import com.whatshouldwedo.election.application.port.in.usecase.CreateDistrictHjdongUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.CreateElectionUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.CreateElectoralDistrictUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/elections")
@RequiredArgsConstructor
public class ElectionCommandController {

    private final CreateElectionUseCase createElectionUseCase;
    private final CreateElectoralDistrictUseCase createElectoralDistrictUseCase;
    private final CreateDistrictHjdongUseCase createDistrictHjdongUseCase;

    @PostMapping
    public ResponseDto<ElectionResult> createElection(@RequestBody CreateElectionRequestDto request) {
        ElectionResult result = createElectionUseCase.execute(
                CreateElectionInput.of(request.name(), request.electionType(),
                        request.electionDate(), request.hjdongVersionId(), request.description())
        );
        return ResponseDto.created(result);
    }

    @PostMapping("/{electionId}/districts")
    public ResponseDto<ElectoralDistrictResult> createDistrict(
            @PathVariable String electionId,
            @RequestBody CreateElectoralDistrictRequestDto request) {
        ElectoralDistrictResult result = createElectoralDistrictUseCase.execute(electionId,
                CreateElectoralDistrictInput.of(request.name(), request.districtType(),
                        request.sidoCode(), request.sigunguCode())
        );
        return ResponseDto.created(result);
    }

    @PostMapping("/{electionId}/districts/{districtId}/hjdongs")
    public ResponseDto<List<DistrictHjdongResult>> createDistrictHjdongs(
            @PathVariable String electionId,
            @PathVariable String districtId,
            @RequestBody CreateDistrictHjdongRequestDto request) {
        List<DistrictHjdongResult> results = createDistrictHjdongUseCase.execute(
                electionId, districtId, CreateDistrictHjdongInput.of(request.hjdongCodes())
        );
        return ResponseDto.created(results);
    }
}

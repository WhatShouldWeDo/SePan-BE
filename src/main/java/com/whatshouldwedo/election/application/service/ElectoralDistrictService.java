package com.whatshouldwedo.election.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.election.application.port.in.input.command.CreateDistrictHjdongInput;
import com.whatshouldwedo.election.application.port.in.input.command.CreateElectoralDistrictInput;
import com.whatshouldwedo.election.application.port.in.output.result.DistrictHjdongResult;
import com.whatshouldwedo.election.application.port.in.output.result.ElectoralDistrictResult;
import com.whatshouldwedo.election.application.port.in.usecase.CreateDistrictHjdongUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.CreateElectoralDistrictUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.ReadDistrictHjdongListUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.ReadElectoralDistrictListUseCase;
import com.whatshouldwedo.election.application.port.out.ElectionRepository;
import com.whatshouldwedo.election.application.port.out.ElectoralDistrictHjdongRepository;
import com.whatshouldwedo.election.application.port.out.ElectoralDistrictRepository;
import com.whatshouldwedo.election.domain.ElectionId;
import com.whatshouldwedo.election.domain.ElectoralDistrict;
import com.whatshouldwedo.election.domain.ElectoralDistrictHjdong;
import com.whatshouldwedo.election.domain.ElectoralDistrictHjdongId;
import com.whatshouldwedo.election.domain.ElectoralDistrictId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectoralDistrictService implements CreateElectoralDistrictUseCase,
        ReadElectoralDistrictListUseCase, CreateDistrictHjdongUseCase, ReadDistrictHjdongListUseCase {

    private final ElectionRepository electionRepository;
    private final ElectoralDistrictRepository districtRepository;
    private final ElectoralDistrictHjdongRepository edHjdongRepository;

    @Override
    @Transactional
    public ElectoralDistrictResult execute(String electionId, CreateElectoralDistrictInput input) {
        ElectionId eId = ElectionId.of(electionId);
        electionRepository.findById(eId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ELECTION));

        ElectoralDistrict district = ElectoralDistrict.create(
                ElectoralDistrictId.generate(), eId, input.getName(),
                input.getDistrictType(), input.getSidoCode(), input.getSigunguCode()
        );

        ElectoralDistrict saved = districtRepository.save(district);
        return ElectoralDistrictResult.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElectoralDistrictResult> execute(String electionId) {
        return districtRepository.findAllByElectionId(ElectionId.of(electionId)).stream()
                .map(ElectoralDistrictResult::from)
                .toList();
    }

    @Override
    @Transactional
    public List<DistrictHjdongResult> execute(String electionId, String districtId,
                                                CreateDistrictHjdongInput input) {
        ElectoralDistrictId dId = ElectoralDistrictId.of(districtId);
        districtRepository.findById(dId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ELECTORAL_DISTRICT));

        List<ElectoralDistrictHjdong> edhs = input.getHjdongCodes().stream()
                .map(code -> ElectoralDistrictHjdong.create(
                        ElectoralDistrictHjdongId.generate(), dId, code
                )).toList();

        return edHjdongRepository.saveAll(edhs).stream()
                .map(DistrictHjdongResult::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictHjdongResult> execute(String electionId, String districtId) {
        return edHjdongRepository.findAllByElectoralDistrictId(ElectoralDistrictId.of(districtId)).stream()
                .map(DistrictHjdongResult::from)
                .toList();
    }
}

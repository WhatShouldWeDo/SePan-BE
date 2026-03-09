package com.whatshouldwedo.election.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.election.application.port.in.input.command.CreateElectionInput;
import com.whatshouldwedo.election.application.port.in.output.result.ElectionResult;
import com.whatshouldwedo.election.application.port.in.usecase.CreateElectionUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.ReadElectionDetailUseCase;
import com.whatshouldwedo.election.application.port.in.usecase.ReadElectionListUseCase;
import com.whatshouldwedo.election.application.port.out.ElectionRepository;
import com.whatshouldwedo.election.domain.Election;
import com.whatshouldwedo.election.domain.ElectionId;
import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectionService implements CreateElectionUseCase, ReadElectionListUseCase, ReadElectionDetailUseCase {

    private final ElectionRepository electionRepository;
    private final HjdongVersionRepository hjdongVersionRepository;

    @Override
    @Transactional
    public ElectionResult execute(CreateElectionInput input) {
        HjdongVersionId versionId = HjdongVersionId.of(input.getHjdongVersionId());
        hjdongVersionRepository.findById(versionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_HJDONG_VERSION));

        Election election = Election.create(
                ElectionId.generate(), input.getName(), input.getElectionType(),
                input.getElectionDate(), versionId, input.getDescription()
        );

        Election saved = electionRepository.save(election);
        return ElectionResult.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElectionResult> execute() {
        return electionRepository.findAll().stream()
                .map(ElectionResult::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ElectionResult execute(String electionId) {
        Election election = electionRepository.findById(ElectionId.of(electionId))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ELECTION));
        return ElectionResult.from(election);
    }
}

package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.region.application.port.in.input.command.CreateHjdongMappingBulkInput;
import com.whatshouldwedo.region.application.port.in.input.command.CreateHjdongMappingInput;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongMappingResult;
import com.whatshouldwedo.region.application.port.in.usecase.CreateHjdongMappingBulkUseCase;
import com.whatshouldwedo.region.application.port.in.usecase.CreateHjdongMappingUseCase;
import com.whatshouldwedo.region.application.port.out.HjdongMappingRepository;
import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import com.whatshouldwedo.region.domain.HjdongMapping;
import com.whatshouldwedo.region.domain.HjdongMappingId;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateHjdongMappingService implements CreateHjdongMappingUseCase, CreateHjdongMappingBulkUseCase {

    private final HjdongMappingRepository hjdongMappingRepository;
    private final HjdongVersionRepository hjdongVersionRepository;

    @Override
    @Transactional
    public HjdongMappingResult execute(String versionId, CreateHjdongMappingInput input) {
        HjdongVersionId targetVersionId = HjdongVersionId.of(versionId);
        validateVersionExists(targetVersionId);
        validateVersionExists(HjdongVersionId.of(input.getSourceVersionId()));

        HjdongMapping mapping = HjdongMapping.create(
                HjdongMappingId.generate(),
                HjdongVersionId.of(input.getSourceVersionId()),
                targetVersionId,
                input.getSourceHjdongCode(),
                input.getTargetHjdongCode(),
                input.getMappingType(),
                input.getRatio(),
                input.getDescription()
        );

        HjdongMapping saved = hjdongMappingRepository.save(mapping);
        return HjdongMappingResult.from(saved);
    }

    @Override
    @Transactional
    public List<HjdongMappingResult> execute(String versionId, CreateHjdongMappingBulkInput input) {
        HjdongVersionId targetVersionId = HjdongVersionId.of(versionId);
        validateVersionExists(targetVersionId);

        List<HjdongMapping> mappings = input.getMappings().stream()
                .map(m -> {
                    validateVersionExists(HjdongVersionId.of(m.getSourceVersionId()));
                    return HjdongMapping.create(
                            HjdongMappingId.generate(),
                            HjdongVersionId.of(m.getSourceVersionId()),
                            targetVersionId,
                            m.getSourceHjdongCode(),
                            m.getTargetHjdongCode(),
                            m.getMappingType(),
                            m.getRatio(),
                            m.getDescription()
                    );
                })
                .toList();

        return hjdongMappingRepository.saveAll(mappings).stream()
                .map(HjdongMappingResult::from)
                .toList();
    }

    private void validateVersionExists(HjdongVersionId versionId) {
        hjdongVersionRepository.findById(versionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_HJDONG_VERSION));
    }
}

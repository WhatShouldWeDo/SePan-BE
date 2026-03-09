package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.region.application.port.in.input.command.CreateHjdongVersionInput;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongVersionResult;
import com.whatshouldwedo.region.application.port.in.usecase.CreateHjdongVersionUseCase;
import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import com.whatshouldwedo.region.domain.HjdongVersion;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateHjdongVersionService implements CreateHjdongVersionUseCase {

    private final HjdongVersionRepository hjdongVersionRepository;

    @Override
    @Transactional
    public HjdongVersionResult execute(CreateHjdongVersionInput input) {
        if (hjdongVersionRepository.existsByVersionName(input.getVersionName())) {
            throw new CommonException(ErrorCode.REGION_HJDONG_VERSION_DUPLICATE);
        }

        HjdongVersion version = HjdongVersion.create(
                HjdongVersionId.generate(),
                input.getVersionName(),
                input.getEffectiveDate(),
                input.getDescription()
        );

        HjdongVersion saved = hjdongVersionRepository.save(version);
        return HjdongVersionResult.from(saved);
    }
}

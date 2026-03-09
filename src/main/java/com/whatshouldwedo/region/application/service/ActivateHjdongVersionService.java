package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongVersionResult;
import com.whatshouldwedo.region.application.port.in.usecase.ActivateHjdongVersionUseCase;
import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import com.whatshouldwedo.region.domain.HjdongVersion;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivateHjdongVersionService implements ActivateHjdongVersionUseCase {

    private final HjdongVersionRepository hjdongVersionRepository;

    @Override
    @Transactional
    public HjdongVersionResult execute(String versionId) {
        // 기존 활성 버전 비활성화
        hjdongVersionRepository.findActiveVersion()
                .ifPresent(activeVersion -> {
                    activeVersion.deactivate();
                    hjdongVersionRepository.save(activeVersion);
                });

        // 새 버전 활성화
        HjdongVersion version = hjdongVersionRepository.findById(HjdongVersionId.of(versionId))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_HJDONG_VERSION));

        version.activate();
        HjdongVersion saved = hjdongVersionRepository.save(version);
        return HjdongVersionResult.from(saved);
    }
}

package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.region.application.port.in.output.result.HjdongResult;
import com.whatshouldwedo.region.application.port.in.usecase.ReadHjdongListUseCase;
import com.whatshouldwedo.region.application.port.out.HjdongRepository;
import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import com.whatshouldwedo.region.domain.HjdongVersion;
import com.whatshouldwedo.region.domain.SigunguId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadHjdongListService implements ReadHjdongListUseCase {

    private final HjdongRepository hjdongRepository;
    private final HjdongVersionRepository hjdongVersionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HjdongResult> execute(String sigunguId) {
        HjdongVersion activeVersion = hjdongVersionRepository.findActiveVersion()
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ACTIVE_HJDONG_VERSION));

        return hjdongRepository.findAllBySigunguIdAndVersionId(
                SigunguId.of(sigunguId), activeVersion.getId()
        ).stream().map(HjdongResult::from).toList();
    }
}

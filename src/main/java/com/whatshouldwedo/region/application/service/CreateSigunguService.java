package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.region.application.port.in.input.command.CreateSigunguInput;
import com.whatshouldwedo.region.application.port.in.output.result.SigunguResult;
import com.whatshouldwedo.region.application.port.in.usecase.CreateSigunguUseCase;
import com.whatshouldwedo.region.application.port.out.SidoRepository;
import com.whatshouldwedo.region.application.port.out.SigunguRepository;
import com.whatshouldwedo.region.domain.SidoId;
import com.whatshouldwedo.region.domain.Sigungu;
import com.whatshouldwedo.region.domain.SigunguId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSigunguService implements CreateSigunguUseCase {

    private final SidoRepository sidoRepository;
    private final SigunguRepository sigunguRepository;

    @Override
    @Transactional
    public SigunguResult execute(CreateSigunguInput input) {
        SidoId sidoId = SidoId.of(input.getSidoId());
        sidoRepository.findById(sidoId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_SIDO));

        if (sigunguRepository.existsByCode(input.getCode())) {
            throw new CommonException(ErrorCode.REGION_SIGUNGU_DUPLICATE);
        }

        Sigungu sigungu = Sigungu.create(SigunguId.generate(), sidoId, input.getCode(), input.getName());
        Sigungu saved = sigunguRepository.save(sigungu);
        return SigunguResult.from(saved);
    }
}

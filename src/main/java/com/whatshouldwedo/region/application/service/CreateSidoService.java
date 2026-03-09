package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.core.exception.definition.ErrorCode;
import com.whatshouldwedo.core.exception.type.CommonException;
import com.whatshouldwedo.region.application.port.in.input.command.CreateSidoInput;
import com.whatshouldwedo.region.application.port.in.output.result.SidoResult;
import com.whatshouldwedo.region.application.port.in.usecase.CreateSidoUseCase;
import com.whatshouldwedo.region.application.port.out.SidoRepository;
import com.whatshouldwedo.region.domain.Sido;
import com.whatshouldwedo.region.domain.SidoId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSidoService implements CreateSidoUseCase {

    private final SidoRepository sidoRepository;

    @Override
    @Transactional
    public SidoResult execute(CreateSidoInput input) {
        if (sidoRepository.existsByCode(input.getCode())) {
            throw new CommonException(ErrorCode.REGION_SIDO_DUPLICATE);
        }

        Sido sido = Sido.create(SidoId.generate(), input.getCode(), input.getName());
        Sido saved = sidoRepository.save(sido);
        return SidoResult.from(saved);
    }
}

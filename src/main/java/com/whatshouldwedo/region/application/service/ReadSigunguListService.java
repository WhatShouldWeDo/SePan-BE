package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.region.application.port.in.output.result.SigunguResult;
import com.whatshouldwedo.region.application.port.in.usecase.ReadSigunguListUseCase;
import com.whatshouldwedo.region.application.port.out.SigunguRepository;
import com.whatshouldwedo.region.domain.SidoId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadSigunguListService implements ReadSigunguListUseCase {

    private final SigunguRepository sigunguRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SigunguResult> execute(String sidoId) {
        return sigunguRepository.findAllBySidoId(SidoId.of(sidoId)).stream()
                .map(SigunguResult::from)
                .toList();
    }
}

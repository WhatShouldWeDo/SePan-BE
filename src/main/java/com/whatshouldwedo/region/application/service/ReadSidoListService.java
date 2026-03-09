package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.region.application.port.in.output.result.SidoResult;
import com.whatshouldwedo.region.application.port.in.usecase.ReadSidoListUseCase;
import com.whatshouldwedo.region.application.port.out.SidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadSidoListService implements ReadSidoListUseCase {

    private final SidoRepository sidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SidoResult> execute() {
        return sidoRepository.findAll().stream()
                .map(SidoResult::from)
                .toList();
    }
}

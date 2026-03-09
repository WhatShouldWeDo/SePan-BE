package com.whatshouldwedo.region.application.service;

import com.whatshouldwedo.region.application.port.in.output.result.HjdongVersionResult;
import com.whatshouldwedo.region.application.port.in.usecase.ReadHjdongVersionListUseCase;
import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadHjdongVersionListService implements ReadHjdongVersionListUseCase {

    private final HjdongVersionRepository hjdongVersionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HjdongVersionResult> execute() {
        return hjdongVersionRepository.findAll().stream()
                .map(HjdongVersionResult::from)
                .toList();
    }
}

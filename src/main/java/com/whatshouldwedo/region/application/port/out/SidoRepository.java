package com.whatshouldwedo.region.application.port.out;

import com.whatshouldwedo.region.domain.Sido;
import com.whatshouldwedo.region.domain.SidoId;

import java.util.List;
import java.util.Optional;

public interface SidoRepository {

    Sido save(Sido sido);

    Optional<Sido> findById(SidoId id);

    Optional<Sido> findByCode(String code);

    List<Sido> findAll();

    boolean existsByCode(String code);
}

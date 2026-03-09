package com.whatshouldwedo.region.application.port.out;

import com.whatshouldwedo.region.domain.SidoId;
import com.whatshouldwedo.region.domain.Sigungu;
import com.whatshouldwedo.region.domain.SigunguId;

import java.util.List;
import java.util.Optional;

public interface SigunguRepository {

    Sigungu save(Sigungu sigungu);

    Optional<Sigungu> findById(SigunguId id);

    Optional<Sigungu> findByCode(String code);

    List<Sigungu> findAllBySidoId(SidoId sidoId);

    List<Sigungu> findAll();

    boolean existsByCode(String code);
}

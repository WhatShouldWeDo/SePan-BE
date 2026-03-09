package com.whatshouldwedo.region.application.port.out;

import com.whatshouldwedo.region.domain.Hjdong;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import com.whatshouldwedo.region.domain.SigunguId;

import java.util.List;
import java.util.Optional;

public interface HjdongRepository {

    Hjdong save(Hjdong hjdong);

    List<Hjdong> findAllBySigunguIdAndVersionId(SigunguId sigunguId, HjdongVersionId versionId);

    List<Hjdong> findAllByVersionId(HjdongVersionId versionId);

    Optional<Hjdong> findByCodeAndVersionId(String code, HjdongVersionId versionId);

    List<Hjdong> saveAll(List<Hjdong> hjdongs);

    boolean existsByCodeAndVersionId(String code, HjdongVersionId versionId);
}

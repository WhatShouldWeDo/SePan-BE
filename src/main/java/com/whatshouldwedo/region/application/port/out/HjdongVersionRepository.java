package com.whatshouldwedo.region.application.port.out;

import com.whatshouldwedo.region.domain.HjdongVersion;
import com.whatshouldwedo.region.domain.HjdongVersionId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HjdongVersionRepository {

    HjdongVersion save(HjdongVersion version);

    Optional<HjdongVersion> findById(HjdongVersionId id);

    Optional<HjdongVersion> findActiveVersion();

    List<HjdongVersion> findAll();

    boolean existsByVersionName(String versionName);

    Optional<HjdongVersion> findLatestByEffectiveDateLessThanEqual(LocalDate date);
}

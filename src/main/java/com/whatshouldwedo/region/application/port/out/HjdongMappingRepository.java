package com.whatshouldwedo.region.application.port.out;

import com.whatshouldwedo.region.domain.HjdongMapping;
import com.whatshouldwedo.region.domain.HjdongVersionId;

import java.util.List;

public interface HjdongMappingRepository {

    HjdongMapping save(HjdongMapping mapping);

    List<HjdongMapping> saveAll(List<HjdongMapping> mappings);

    List<HjdongMapping> findAllBySourceAndTargetVersion(HjdongVersionId sourceVersionId,
                                                         HjdongVersionId targetVersionId);

    List<HjdongMapping> findAllByTargetVersionId(HjdongVersionId targetVersionId);

    List<HjdongMapping> findAllBySourceVersionId(HjdongVersionId sourceVersionId);

    List<HjdongMapping> findAllByTargetVersionIdAndTargetHjdongCode(
            HjdongVersionId targetVersionId, String targetHjdongCode);
}

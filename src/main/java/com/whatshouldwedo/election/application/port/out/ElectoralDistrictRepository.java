package com.whatshouldwedo.election.application.port.out;

import com.whatshouldwedo.election.domain.ElectionId;
import com.whatshouldwedo.election.domain.ElectoralDistrict;
import com.whatshouldwedo.election.domain.ElectoralDistrictId;

import java.util.List;
import java.util.Optional;

public interface ElectoralDistrictRepository {

    ElectoralDistrict save(ElectoralDistrict district);

    Optional<ElectoralDistrict> findById(ElectoralDistrictId id);

    List<ElectoralDistrict> findAllByElectionId(ElectionId electionId);
}

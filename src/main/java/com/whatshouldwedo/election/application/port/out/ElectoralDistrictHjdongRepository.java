package com.whatshouldwedo.election.application.port.out;

import com.whatshouldwedo.election.domain.ElectoralDistrictHjdong;
import com.whatshouldwedo.election.domain.ElectoralDistrictId;

import java.util.List;

public interface ElectoralDistrictHjdongRepository {

    ElectoralDistrictHjdong save(ElectoralDistrictHjdong edh);

    List<ElectoralDistrictHjdong> saveAll(List<ElectoralDistrictHjdong> edhs);

    List<ElectoralDistrictHjdong> findAllByElectoralDistrictId(ElectoralDistrictId districtId);
}

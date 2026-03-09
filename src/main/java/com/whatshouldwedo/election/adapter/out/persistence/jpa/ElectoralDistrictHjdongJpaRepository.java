package com.whatshouldwedo.election.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectoralDistrictHjdongJpaRepository extends JpaRepository<ElectoralDistrictHjdongJpaEntity, String> {

    List<ElectoralDistrictHjdongJpaEntity> findAllByElectoralDistrictId(String electoralDistrictId);
}

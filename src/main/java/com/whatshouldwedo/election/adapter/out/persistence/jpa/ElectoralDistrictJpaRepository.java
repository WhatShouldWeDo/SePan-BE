package com.whatshouldwedo.election.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectoralDistrictJpaRepository extends JpaRepository<ElectoralDistrictJpaEntity, String> {

    List<ElectoralDistrictJpaEntity> findAllByElectionId(String electionId);
}

package com.whatshouldwedo.election.adapter.out.persistence;

import com.whatshouldwedo.election.adapter.out.persistence.jpa.ElectionJpaEntity;
import com.whatshouldwedo.election.adapter.out.persistence.jpa.ElectionJpaRepository;
import com.whatshouldwedo.election.adapter.out.persistence.jpa.ElectoralDistrictHjdongJpaEntity;
import com.whatshouldwedo.election.adapter.out.persistence.jpa.ElectoralDistrictHjdongJpaRepository;
import com.whatshouldwedo.election.adapter.out.persistence.jpa.ElectoralDistrictJpaEntity;
import com.whatshouldwedo.election.adapter.out.persistence.jpa.ElectoralDistrictJpaRepository;
import com.whatshouldwedo.election.application.port.out.ElectionRepository;
import com.whatshouldwedo.election.application.port.out.ElectoralDistrictHjdongRepository;
import com.whatshouldwedo.election.application.port.out.ElectoralDistrictRepository;
import com.whatshouldwedo.election.domain.Election;
import com.whatshouldwedo.election.domain.ElectionId;
import com.whatshouldwedo.election.domain.ElectoralDistrict;
import com.whatshouldwedo.election.domain.ElectoralDistrictHjdong;
import com.whatshouldwedo.election.domain.ElectoralDistrictId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ElectionPersistenceAdapter implements ElectionRepository, ElectoralDistrictRepository,
        ElectoralDistrictHjdongRepository {

    private final ElectionJpaRepository electionJpaRepository;
    private final ElectoralDistrictJpaRepository districtJpaRepository;
    private final ElectoralDistrictHjdongJpaRepository edHjdongJpaRepository;

    // === Election ===

    @Override
    public Election save(Election election) {
        return electionJpaRepository.save(ElectionJpaEntity.fromDomain(election)).toDomain();
    }

    @Override
    public Optional<Election> findById(ElectionId id) {
        return electionJpaRepository.findById(id.getValue().toString())
                .map(ElectionJpaEntity::toDomain);
    }

    @Override
    public List<Election> findAll() {
        return electionJpaRepository.findAll().stream()
                .map(ElectionJpaEntity::toDomain)
                .toList();
    }

    // === ElectoralDistrict ===

    @Override
    public ElectoralDistrict save(ElectoralDistrict district) {
        return districtJpaRepository.save(ElectoralDistrictJpaEntity.fromDomain(district)).toDomain();
    }

    @Override
    public Optional<ElectoralDistrict> findById(ElectoralDistrictId id) {
        return districtJpaRepository.findById(id.getValue().toString())
                .map(ElectoralDistrictJpaEntity::toDomain);
    }

    @Override
    public List<ElectoralDistrict> findAllByElectionId(ElectionId electionId) {
        return districtJpaRepository.findAllByElectionId(electionId.getValue().toString()).stream()
                .map(ElectoralDistrictJpaEntity::toDomain)
                .toList();
    }

    // === ElectoralDistrictHjdong ===

    @Override
    public ElectoralDistrictHjdong save(ElectoralDistrictHjdong edh) {
        return edHjdongJpaRepository.save(ElectoralDistrictHjdongJpaEntity.fromDomain(edh)).toDomain();
    }

    @Override
    public List<ElectoralDistrictHjdong> saveAll(List<ElectoralDistrictHjdong> edhs) {
        List<ElectoralDistrictHjdongJpaEntity> entities = edhs.stream()
                .map(ElectoralDistrictHjdongJpaEntity::fromDomain).toList();
        return edHjdongJpaRepository.saveAll(entities).stream()
                .map(ElectoralDistrictHjdongJpaEntity::toDomain).toList();
    }

    @Override
    public List<ElectoralDistrictHjdong> findAllByElectoralDistrictId(ElectoralDistrictId districtId) {
        return edHjdongJpaRepository.findAllByElectoralDistrictId(districtId.getValue().toString()).stream()
                .map(ElectoralDistrictHjdongJpaEntity::toDomain).toList();
    }
}

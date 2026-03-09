package com.whatshouldwedo.region.adapter.out.persistence;

import com.whatshouldwedo.region.adapter.out.persistence.jpa.HjdongMappingJpaEntity;
import com.whatshouldwedo.region.adapter.out.persistence.jpa.HjdongMappingJpaRepository;
import com.whatshouldwedo.region.adapter.out.persistence.jpa.HjdongVersionJpaEntity;
import com.whatshouldwedo.region.adapter.out.persistence.jpa.HjdongVersionJpaRepository;
import com.whatshouldwedo.region.application.port.out.HjdongMappingRepository;
import com.whatshouldwedo.region.application.port.out.HjdongVersionRepository;
import com.whatshouldwedo.region.domain.HjdongMapping;
import com.whatshouldwedo.region.domain.HjdongVersion;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HjdongVersionPersistenceAdapter implements HjdongVersionRepository, HjdongMappingRepository {

    private final HjdongVersionJpaRepository versionJpaRepository;
    private final HjdongMappingJpaRepository mappingJpaRepository;

    // === HjdongVersion ===

    @Override
    public HjdongVersion save(HjdongVersion version) {
        return versionJpaRepository.save(HjdongVersionJpaEntity.fromDomain(version)).toDomain();
    }

    @Override
    public Optional<HjdongVersion> findById(HjdongVersionId id) {
        return versionJpaRepository.findById(id.getValue().toString())
                .map(HjdongVersionJpaEntity::toDomain);
    }

    @Override
    public Optional<HjdongVersion> findActiveVersion() {
        return versionJpaRepository.findByIsActiveTrue()
                .map(HjdongVersionJpaEntity::toDomain);
    }

    @Override
    public List<HjdongVersion> findAll() {
        return versionJpaRepository.findAllByOrderByEffectiveDateDesc().stream()
                .map(HjdongVersionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByVersionName(String versionName) {
        return versionJpaRepository.existsByVersionName(versionName);
    }

    @Override
    public Optional<HjdongVersion> findLatestByEffectiveDateLessThanEqual(LocalDate date) {
        return versionJpaRepository.findFirstByEffectiveDateLessThanEqualOrderByEffectiveDateDesc(date)
                .map(HjdongVersionJpaEntity::toDomain);
    }

    // === HjdongMapping ===

    @Override
    public HjdongMapping save(HjdongMapping mapping) {
        return mappingJpaRepository.save(HjdongMappingJpaEntity.fromDomain(mapping)).toDomain();
    }

    @Override
    public List<HjdongMapping> saveAll(List<HjdongMapping> mappings) {
        List<HjdongMappingJpaEntity> entities = mappings.stream()
                .map(HjdongMappingJpaEntity::fromDomain)
                .toList();
        return mappingJpaRepository.saveAll(entities).stream()
                .map(HjdongMappingJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<HjdongMapping> findAllBySourceAndTargetVersion(HjdongVersionId sourceVersionId,
                                                                 HjdongVersionId targetVersionId) {
        return mappingJpaRepository.findAllBySourceVersionIdAndTargetVersionId(
                sourceVersionId.getValue().toString(), targetVersionId.getValue().toString()
        ).stream().map(HjdongMappingJpaEntity::toDomain).toList();
    }

    @Override
    public List<HjdongMapping> findAllByTargetVersionId(HjdongVersionId targetVersionId) {
        return mappingJpaRepository.findAllByTargetVersionId(targetVersionId.getValue().toString()).stream()
                .map(HjdongMappingJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<HjdongMapping> findAllBySourceVersionId(HjdongVersionId sourceVersionId) {
        return mappingJpaRepository.findAllBySourceVersionId(sourceVersionId.getValue().toString()).stream()
                .map(HjdongMappingJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<HjdongMapping> findAllByTargetVersionIdAndTargetHjdongCode(
            HjdongVersionId targetVersionId, String targetHjdongCode) {
        return mappingJpaRepository.findAllByTargetVersionIdAndTargetHjdongCode(
                targetVersionId.getValue().toString(), targetHjdongCode
        ).stream().map(HjdongMappingJpaEntity::toDomain).toList();
    }
}

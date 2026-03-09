package com.whatshouldwedo.region.adapter.out.persistence;

import com.whatshouldwedo.region.adapter.out.persistence.jpa.HjdongJpaEntity;
import com.whatshouldwedo.region.adapter.out.persistence.jpa.HjdongJpaRepository;
import com.whatshouldwedo.region.application.port.out.HjdongRepository;
import com.whatshouldwedo.region.domain.Hjdong;
import com.whatshouldwedo.region.domain.HjdongVersionId;
import com.whatshouldwedo.region.domain.SigunguId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HjdongPersistenceAdapter implements HjdongRepository {

    private final HjdongJpaRepository hjdongJpaRepository;

    @Override
    public Hjdong save(Hjdong hjdong) {
        return hjdongJpaRepository.save(HjdongJpaEntity.fromDomain(hjdong)).toDomain();
    }

    @Override
    public List<Hjdong> findAllBySigunguIdAndVersionId(SigunguId sigunguId, HjdongVersionId versionId) {
        return hjdongJpaRepository.findAllBySigunguIdAndVersionId(
                sigunguId.getValue().toString(), versionId.getValue().toString()
        ).stream().map(HjdongJpaEntity::toDomain).toList();
    }

    @Override
    public List<Hjdong> findAllByVersionId(HjdongVersionId versionId) {
        return hjdongJpaRepository.findAllByVersionId(versionId.getValue().toString()).stream()
                .map(HjdongJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Hjdong> findByCodeAndVersionId(String code, HjdongVersionId versionId) {
        return hjdongJpaRepository.findByCodeAndVersionId(code, versionId.getValue().toString())
                .map(HjdongJpaEntity::toDomain);
    }

    @Override
    public List<Hjdong> saveAll(List<Hjdong> hjdongs) {
        List<HjdongJpaEntity> entities = hjdongs.stream()
                .map(HjdongJpaEntity::fromDomain)
                .toList();
        return hjdongJpaRepository.saveAll(entities).stream()
                .map(HjdongJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCodeAndVersionId(String code, HjdongVersionId versionId) {
        return hjdongJpaRepository.findByCodeAndVersionId(code, versionId.getValue().toString()).isPresent();
    }
}

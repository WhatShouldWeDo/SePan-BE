package com.whatshouldwedo.region.adapter.out.persistence;

import com.whatshouldwedo.region.adapter.out.persistence.jpa.SigunguJpaEntity;
import com.whatshouldwedo.region.adapter.out.persistence.jpa.SigunguJpaRepository;
import com.whatshouldwedo.region.application.port.out.SigunguRepository;
import com.whatshouldwedo.region.domain.Sigungu;
import com.whatshouldwedo.region.domain.SidoId;
import com.whatshouldwedo.region.domain.SigunguId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SigunguPersistenceAdapter implements SigunguRepository {

    private final SigunguJpaRepository sigunguJpaRepository;

    @Override
    public Sigungu save(Sigungu sigungu) {
        return sigunguJpaRepository.save(SigunguJpaEntity.fromDomain(sigungu)).toDomain();
    }

    @Override
    public Optional<Sigungu> findById(SigunguId id) {
        return sigunguJpaRepository.findById(id.getValue().toString())
                .map(SigunguJpaEntity::toDomain);
    }

    @Override
    public Optional<Sigungu> findByCode(String code) {
        return sigunguJpaRepository.findByCode(code)
                .map(SigunguJpaEntity::toDomain);
    }

    @Override
    public List<Sigungu> findAllBySidoId(SidoId sidoId) {
        return sigunguJpaRepository.findAllBySidoId(sidoId.getValue().toString()).stream()
                .map(SigunguJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Sigungu> findAll() {
        return sigunguJpaRepository.findAll().stream()
                .map(SigunguJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return sigunguJpaRepository.existsByCode(code);
    }
}

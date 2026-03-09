package com.whatshouldwedo.region.adapter.out.persistence;

import com.whatshouldwedo.region.adapter.out.persistence.jpa.SidoJpaEntity;
import com.whatshouldwedo.region.adapter.out.persistence.jpa.SidoJpaRepository;
import com.whatshouldwedo.region.application.port.out.SidoRepository;
import com.whatshouldwedo.region.domain.Sido;
import com.whatshouldwedo.region.domain.SidoId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SidoPersistenceAdapter implements SidoRepository {

    private final SidoJpaRepository sidoJpaRepository;

    @Override
    public Sido save(Sido sido) {
        return sidoJpaRepository.save(SidoJpaEntity.fromDomain(sido)).toDomain();
    }

    @Override
    public Optional<Sido> findById(SidoId id) {
        return sidoJpaRepository.findById(id.getValue().toString())
                .map(SidoJpaEntity::toDomain);
    }

    @Override
    public Optional<Sido> findByCode(String code) {
        return sidoJpaRepository.findByCode(code)
                .map(SidoJpaEntity::toDomain);
    }

    @Override
    public List<Sido> findAll() {
        return sidoJpaRepository.findAll().stream()
                .map(SidoJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return sidoJpaRepository.existsByCode(code);
    }
}

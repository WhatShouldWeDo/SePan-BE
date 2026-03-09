package com.whatshouldwedo.policy.adapter.out.persistence;

import com.whatshouldwedo.policy.adapter.out.persistence.jpa.AiRecommendationJpaEntity;
import com.whatshouldwedo.policy.adapter.out.persistence.jpa.AiRecommendationJpaRepository;
import com.whatshouldwedo.policy.adapter.out.persistence.jpa.PledgeJpaEntity;
import com.whatshouldwedo.policy.adapter.out.persistence.jpa.PledgeJpaRepository;
import com.whatshouldwedo.policy.application.port.out.AiRecommendationRepository;
import com.whatshouldwedo.policy.application.port.out.PledgeRepository;
import com.whatshouldwedo.policy.domain.AiRecommendation;
import com.whatshouldwedo.policy.domain.Pledge;
import com.whatshouldwedo.policy.domain.PledgeId;
import com.whatshouldwedo.policy.domain.type.EPledgeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PolicyPersistenceAdapter implements PledgeRepository, AiRecommendationRepository {

    private final PledgeJpaRepository pledgeJpaRepository;
    private final AiRecommendationJpaRepository aiRecJpaRepository;

    @Override
    public Pledge save(Pledge pledge) {
        return pledgeJpaRepository.save(PledgeJpaEntity.fromDomain(pledge)).toDomain();
    }

    @Override
    public Optional<Pledge> findById(PledgeId id) {
        return pledgeJpaRepository.findById(id.getValue().toString())
                .map(PledgeJpaEntity::toDomain);
    }

    @Override
    public List<Pledge> findAllByAuthorId(String authorId) {
        return pledgeJpaRepository.findAllByAuthorId(authorId).stream()
                .map(PledgeJpaEntity::toDomain).toList();
    }

    @Override
    public List<Pledge> findAllByStatus(EPledgeStatus status) {
        return pledgeJpaRepository.findAllByStatus(status).stream()
                .map(PledgeJpaEntity::toDomain).toList();
    }

    @Override
    public AiRecommendation save(AiRecommendation recommendation) {
        return aiRecJpaRepository.save(AiRecommendationJpaEntity.fromDomain(recommendation)).toDomain();
    }

    @Override
    public List<AiRecommendation> findAllByRequestedBy(String requestedBy) {
        return aiRecJpaRepository.findAllByRequestedByOrderByCreatedAtDesc(requestedBy).stream()
                .map(AiRecommendationJpaEntity::toDomain).toList();
    }
}

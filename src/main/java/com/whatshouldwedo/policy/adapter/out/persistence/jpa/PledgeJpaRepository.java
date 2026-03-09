package com.whatshouldwedo.policy.adapter.out.persistence.jpa;

import com.whatshouldwedo.policy.domain.type.EPledgeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PledgeJpaRepository extends JpaRepository<PledgeJpaEntity, String> {

    List<PledgeJpaEntity> findAllByAuthorId(String authorId);

    List<PledgeJpaEntity> findAllByStatus(EPledgeStatus status);
}

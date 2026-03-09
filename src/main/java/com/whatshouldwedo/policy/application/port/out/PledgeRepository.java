package com.whatshouldwedo.policy.application.port.out;

import com.whatshouldwedo.policy.domain.Pledge;
import com.whatshouldwedo.policy.domain.PledgeId;
import com.whatshouldwedo.policy.domain.type.EPledgeStatus;

import java.util.List;
import java.util.Optional;

public interface PledgeRepository {

    Pledge save(Pledge pledge);

    Optional<Pledge> findById(PledgeId id);

    List<Pledge> findAllByAuthorId(String authorId);

    List<Pledge> findAllByStatus(EPledgeStatus status);
}

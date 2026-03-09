package com.whatshouldwedo.election.application.port.out;

import com.whatshouldwedo.election.domain.Election;
import com.whatshouldwedo.election.domain.ElectionId;

import java.util.List;
import java.util.Optional;

public interface ElectionRepository {

    Election save(Election election);

    Optional<Election> findById(ElectionId id);

    List<Election> findAll();
}

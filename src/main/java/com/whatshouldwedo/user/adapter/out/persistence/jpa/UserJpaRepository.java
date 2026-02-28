package com.whatshouldwedo.user.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {

    Optional<UserJpaEntity> findBySerialId(String username);

    boolean existsBySerialId(String username);
}

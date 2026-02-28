package com.whatshouldwedo.user.adapter.out.persistence;

import com.whatshouldwedo.user.adapter.out.persistence.jpa.UserJpaEntity;
import com.whatshouldwedo.user.adapter.out.persistence.jpa.UserJpaRepository;
import com.whatshouldwedo.user.application.port.out.UserRepository;
import com.whatshouldwedo.user.domain.User;
import com.whatshouldwedo.user.domain.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.fromDomain(user);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.getValue().toString())
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findBySerialId(String username) {
        return jpaRepository.findBySerialId(username)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsBySerialId(String username) {
        return jpaRepository.existsBySerialId(username);
    }
}

package com.whatshouldwedo.user.application.port.out;

import com.whatshouldwedo.user.domain.User;
import com.whatshouldwedo.user.domain.UserId;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findBySerialId(String username);

    boolean existsBySerialId(String username);
}

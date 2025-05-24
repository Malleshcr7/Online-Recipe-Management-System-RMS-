package com.Online.Recipe.Management.System.RMS.repositories;

import com.Online.Recipe.Management.System.RMS.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByPasswordResetToken(String token);
}

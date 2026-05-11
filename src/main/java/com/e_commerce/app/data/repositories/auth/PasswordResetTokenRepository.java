package com.e_commerce.app.data.repositories.auth;

import com.e_commerce.app.data.entities.auth.PasswordResetToken;
import com.e_commerce.app.data.entities.auth.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(UserEntity user);
}

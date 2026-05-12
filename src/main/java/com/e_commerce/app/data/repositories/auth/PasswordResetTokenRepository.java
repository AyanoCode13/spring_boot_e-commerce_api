package com.e_commerce.app.data.repositories.auth;

import com.e_commerce.app.domain.entities.auth.PasswordResetToken;
import com.e_commerce.app.domain.entities.auth.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(UserEntity user);
}

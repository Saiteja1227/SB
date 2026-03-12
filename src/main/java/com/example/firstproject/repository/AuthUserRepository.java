package com.example.firstproject.repository;

import com.example.firstproject.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByFirebaseUid(String firebaseUid);
    Optional<AuthUser> findByPhoneNumber(String phoneNumber);
    boolean existsByFirebaseUid(String firebaseUid);
    boolean existsByPhoneNumber(String phoneNumber);
}

package com.example.firstproject;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    List<CartItem> findByUser(Users user);
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserId(Long userId);
}

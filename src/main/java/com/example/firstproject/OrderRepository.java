package com.example.firstproject;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByUserOrderByCreatedAtDesc(Users user);
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findByStatus(String status);
    Long countByStatus(String status);
}

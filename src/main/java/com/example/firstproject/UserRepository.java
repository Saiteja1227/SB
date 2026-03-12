package com.example.firstproject;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,Integer>
{
   Users findByUsername(String n);
   Users findByEmail(String email);
   Users findByResetToken(String resetToken);
}

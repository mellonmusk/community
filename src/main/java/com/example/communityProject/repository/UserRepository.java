package com.example.communityProject.repository;

import com.example.communityProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    ArrayList<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
}

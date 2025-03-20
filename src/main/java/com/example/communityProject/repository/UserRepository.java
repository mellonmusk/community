package com.example.communityProject.repository;

import com.example.communityProject.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    @Override
    ArrayList<User> findAll();

    Optional<User> findByEmail(String email);
    Optional<User> findUserById(Long id);
}

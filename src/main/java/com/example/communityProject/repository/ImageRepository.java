package com.example.communityProject.repository;

import com.example.communityProject.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    void deleteByPost_Id(Long id);
    List<Image> findByPost_Id(Long id);
}

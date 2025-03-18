package com.example.communityProject.repository;

import com.example.communityProject.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 게시글의 좋아요 개수 조회
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    Long countByPostId(Long postId);

    void deleteByPost_Id(Long id);

    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    Optional<Like> findByUser_IdAndPost_Id(Long userId, Long postId);
}

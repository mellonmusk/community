package com.example.communityProject.repository;

import com.example.communityProject.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    ArrayList<Post> findAll();

    // 특정 닉네임의 모든 게시글 조회
    @Query(value = "SELECT * FROM Post WHERE author_id = :userId", nativeQuery = true)
    List<Post> findByUserId(Long userId);

    void deleteByUser_Id(Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes + 1 WHERE p.id = :postId")
    void incrementLikes(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes - 1 WHERE p.id = :postId")
    void decrementLikes(@Param("postId") Long postId);

    @Query("SELECT p.id FROM Post p WHERE p.user.id = :userId")
    List<Long> findIdsByUser_Id(@Param("userId") Long userId);

}

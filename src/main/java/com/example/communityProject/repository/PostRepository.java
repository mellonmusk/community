package com.example.communityProject.repository;

import com.example.communityProject.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
//    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.user")
//    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.user LEFT JOIN FETCH Comment c ON c.post = p")
//    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.comments")
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT p FROM Post p")
    ArrayList<Post> findAll();

    // 특정 닉네임의 모든 게시글 조회
    @Query(value = "SELECT * FROM Post WHERE author_id = :userId", nativeQuery = true)
    List<Post> findByUserId(Long userId);

    void deleteByUserId(Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes + 1 WHERE p.id = :postId")
    void incrementLikes(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes - 1 WHERE p.id = :postId")
    void decrementLikes(@Param("postId") Long postId);

    @Query("SELECT p.id FROM Post p WHERE p.user.id = :userId")
    List<Long> findIdsByUserId(@Param("userId") Long userId);

}

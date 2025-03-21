package com.example.communityProject.repository;

import com.example.communityProject.entity.Comment;
import com.example.communityProject.entity.Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    @Override
    ArrayList<Post> findAll();

    // 특정 닉네임의 모든 게시글 조회
    @Query(value = "SELECT * FROM post WHERE author_id = :userId", nativeQuery = true)
    List<Post> findByUserId(Long userId);

    void deleteByUser_Id(Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes + 1 WHERE p.id = :postId")
    void incrementLikes(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likes = p.likes - 1 WHERE p.id = :postId")
    void decrementLikes(@Param("postId") Long postId);
}

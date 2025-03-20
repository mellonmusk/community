package com.example.communityProject.repository;

import com.example.communityProject.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 모든 댓글 조회
    @Query(value = "SELECT * FROM comment WHERE post_id = :postId", nativeQuery = true)
    List<Comment> findByPostId(Long postId);

    // 특정 닉네임의 모든 댓글 조회
    @Query(value = "SELECT * FROM comment WHERE author_id = :userId", nativeQuery = true)
    List<Comment> findByUserId(Long userId);

    void deleteByPost_Id(Long id);

    void deleteByUser_Id(Long id);
}

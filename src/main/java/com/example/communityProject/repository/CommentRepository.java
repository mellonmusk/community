package com.example.communityProject.repository;

import com.example.communityProject.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 모든 댓글 조회
    @Query(value = "SELECT * FROM Comment WHERE post_id = :postId", nativeQuery = true)
    List<Comment> findByPostId(Long postId);

    // 특정 닉네임의 모든 댓글 조회
    @Query(value = "SELECT * FROM Comment WHERE author_id = :userId", nativeQuery = true)
    List<Comment> findByUserId(Long userId);

    void deleteByPost_Id(Long id);

    void deleteByUserId(Long id);

    // 게시글 ID 목록에 해당하는 댓글 모두 삭제
    @Modifying
    @Query("DELETE FROM Comment l WHERE l.post.id IN :postIds")
    void deleteByPost_IdIn(@Param("postIds") List<Long> postIds);

}

package com.example.communityProject.repository;

import com.example.communityProject.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 게시글의 좋아요 개수 조회
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    Long countByPostId(Long postId);

    void deleteByPostId(Long id);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
    boolean existsByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
 
    void deleteByUserId(@Param("id") Long id);

    // 게시글 ID 목록에 해당하는 좋아요 모두 삭제
    @Modifying
    @Query("DELETE FROM Like l WHERE l.post.id IN :postIds")
    void deleteByPost_IdIn(@Param("postIds") List<Long> postIds);

    // 사용자 ID에 대응되는 게시글 ID 목록을 가져오는 메서드 추가
    @Query("SELECT l.post.id FROM Like l WHERE l.user.id = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);
}

package com.example.communityProject.repository;

import com.example.communityProject.entity.Like;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LikeRepositoryTest {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private Post testPost1;
    private Post testPost2;
    private Like testLike1;
    private Like testLike2;
    private Like testLike3;

    @BeforeEach
    void setUp() {
        testUser1 = userRepository.save(User.builder()
                .email("test1@example.com")
                .password("User@123")
                .nickname("tester1")
                .build());
        testUser2 = userRepository.save(User.builder()
                .email("test2@example.com")
                .password("User@456")
                .nickname("tester2")
                .build());

        testPost1 = postRepository.save(Post.builder()
                .title("Post 1")
                .content("Content of Post 1")
                .user(testUser1)
                .likes(0L)
                .build());

        testPost2 = postRepository.save(Post.builder()
                .title("Post 2")
                .content("Content of Post 2")
                .user(testUser2)
                .likes(0L)
                .build());

        testLike1 = new Like(null, testPost1, testUser1);
        likeRepository.save(testLike1);
        testLike2 = new Like(null, testPost1, testUser2);
        likeRepository.save(testLike2);
        testLike3 = new Like(null, testPost2, testUser1);
        likeRepository.save(testLike3);
    }

    @Test
    @DisplayName("게시글 ID로 좋아요 개수 세기")
    void countByPostId() {
        Long cnt = likeRepository.countByPostId(testPost1.getId());
        assertEquals(cnt, 2);
    }

    @Test
    @DisplayName("게시글 ID로 좋아요 삭제하기")
    void deleteByPost_Id() {
        likeRepository.deleteByPostId(testPost1.getId());
        Long cnt = likeRepository.countByPostId(testPost1.getId());
        assertEquals(0, cnt);
    }

    @Test
    @DisplayName("사용자 ID와 게시글 ID에 대응되는 좋아요가 이미 존재하는지 확인하기")
    void existsByUser_IdAndPost_Id() {
        boolean exists = likeRepository.existsByUserIdAndPostId(testUser1.getId(), testPost1.getId());
        assertTrue(exists);
    }

    @Test
    @DisplayName("사용자 ID와 게시글 ID에 대응되는 좋아요 찾기")
    void findByUser_IdAndPost_Id() {
        Optional<Like> like = likeRepository.findByUserIdAndPostId(testUser1.getId(), testPost1.getId());
        assertTrue(like.isPresent());
        assertEquals(testUser1.getId(), like.get().getUser().getId());
        assertEquals(testPost1.getId(), like.get().getPost().getId());
    }

    @Test
    @DisplayName("사용자 ID로 좋아요 삭제하기")
    void deleteByUser_Id() {
        likeRepository.deleteByUserId(testUser1.getId());
        boolean exists = likeRepository.existsByUserIdAndPostId(testUser1.getId(), testPost1.getId());
        assertFalse(exists);
    }

    @Test
    @DisplayName("게시글 ID로 좋아요 삭제하기")
    void deleteByPost_IdIn() {
        likeRepository.deleteByPost_IdIn(List.of(testPost1.getId(), testPost2.getId()));
        Long cntPost1 = likeRepository.countByPostId(testPost1.getId());
        Long cntPost2 = likeRepository.countByPostId(testPost2.getId());
        assertEquals(0, cntPost1);
        assertEquals(0, cntPost2);
    }
}
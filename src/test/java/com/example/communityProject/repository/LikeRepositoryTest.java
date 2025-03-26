package com.example.communityProject.repository;

import com.example.communityProject.entity.Comment;
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
        testUser1 = new User();
        testUser1.setEmail("test1@example.com");
        testUser1.setPassword("User@123");
        testUser1.setNickname("tester1");
        userRepository.save(testUser1);

        testUser2 = new User();
        testUser2.setEmail("test2@example.com");
        testUser2.setPassword("User@456");
        testUser2.setNickname("tester2");
        userRepository.save(testUser2);

        testPost1 = new Post();
        testPost1.setTitle("Post 1");
        testPost1.setContent("Content of Post 1");
        testPost1.setUser(testUser1);
        testPost1.setLikes(0L);
        postRepository.save(testPost1);

        testPost2 = new Post();
        testPost2.setTitle("Post 2");
        testPost2.setContent("Content of Post 2");
        testPost2.setUser(testUser2);
        testPost2.setLikes(0L);
        postRepository.save(testPost2);

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
        likeRepository.deleteByPost_Id(testPost1.getId());
        Long cnt = likeRepository.countByPostId(testPost1.getId());
        assertEquals(0, cnt);
    }

    @Test
    @DisplayName("사용자 ID와 게시글 ID에 대응되는 좋아요가 이미 존재하는지 확인하기")
    void existsByUser_IdAndPost_Id() {
        boolean exists = likeRepository.existsByUser_IdAndPost_Id(testUser1.getId(), testPost1.getId());
        assertTrue(exists);
    }

    @Test
    @DisplayName("사용자 ID와 게시글 ID에 대응되는 좋아요 찾기")
    void findByUser_IdAndPost_Id() {
        Optional<Like> like = likeRepository.findByUser_IdAndPost_Id(testUser1.getId(), testPost1.getId());
        assertTrue(like.isPresent());
        assertEquals(testUser1.getId(), like.get().getUser().getId());
        assertEquals(testPost1.getId(), like.get().getPost().getId());
    }

    @Test
    @DisplayName("사용자 ID로 좋아요 삭제하기")
    void deleteByUser_Id() {
        likeRepository.deleteByUser_Id(testUser1.getId());
        boolean exists = likeRepository.existsByUser_IdAndPost_Id(testUser1.getId(), testPost1.getId());
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
package com.example.communityProject.repository;

import com.example.communityProject.entity.Comment;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private Post testPost1;
    private Post testPost2;
    private Post testPost3;
    private Comment testComment1;
    private Comment testComment2;
    private Comment testComment3;

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

        testPost3 = new Post();
        testPost3.setTitle("Post 3");
        testPost3.setContent("Content of Post 3");
        testPost3.setUser(testUser2);
        testPost3.setLikes(0L);
        postRepository.save(testPost3);

        testComment1 = new Comment();
        testComment1.setBody("Body of Comment 1");
        testComment1.setPost(testPost1);
        testComment1.setUser(testUser2);
        commentRepository.save(testComment1);

        testComment2 = new Comment();
        testComment2.setBody("Body of Comment 2");
        testComment2.setPost(testPost2);
        testComment2.setUser(testUser1);
        commentRepository.save(testComment2);

        testComment3 = new Comment();
        testComment3.setBody("Body of Comment 3");
        testComment3.setPost(testPost3);
        testComment3.setUser(testUser1);
        commentRepository.save(testComment3);

    }

    @Test
    @DisplayName("게시글 ID로 댓글 조회하기")
    void findByPostId() {
        List<Comment> comments = commentRepository.findByPostId(testPost1.getId());
        assertThat(comments).containsExactly(testComment1);
    }

    @Test
    @DisplayName("사용자 ID로 댓글 조회하기")
    void findByUserId() {
        List<Comment> comments = commentRepository.findByUserId(testUser1.getId());
        assertThat(comments).containsExactlyInAnyOrder(testComment2, testComment3);
    }

    @Test
    @DisplayName("게시글 ID로 댓글 삭제하기")
    void deleteByPost_Id() {
        commentRepository.deleteByPost_Id(testPost1.getId());
        List<Comment> comments = commentRepository.findByPostId(testPost1.getId());
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("사용자 ID로 댓글 삭제하기")
    void deleteByUser_Id() {
        commentRepository.deleteByUser_Id(testUser1.getId());
        List<Comment> comments = commentRepository.findByUserId(testUser1.getId());
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("게시글 ID 목록으로 댓글 삭제하기")
    void deleteByPost_IdIn() {
        commentRepository.deleteByPost_IdIn(List.of(testPost1.getId(), testPost2.getId()));

        List<Comment> remainingComments = commentRepository.findAll(); // 남아 있는 댓글 조회

        assertThat(remainingComments).doesNotContain(testComment1, testComment2);
    }
}
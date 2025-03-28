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

        testPost3 = postRepository.save(Post.builder()
                .title("Post 3")
                .content("Content of Post 3")
                .user(testUser2)
                .likes(0L)
                .build());

        testComment1 = commentRepository.save(Comment.builder()
                .body("Body of Comment 1")
                .post(testPost1)
                .user(testUser2)
                .build());

        testComment2 = commentRepository.save(Comment.builder()
                .body("Body of Comment 2")
                .post(testPost2)
                .user(testUser1)
                .build());

        testComment3 = commentRepository.save(Comment.builder()
                .body("Body of Comment 3")
                .post(testPost3)
                .user(testUser1)
                .build());
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
        commentRepository.deleteByUserId(testUser1.getId());
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
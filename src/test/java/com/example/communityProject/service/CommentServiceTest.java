package com.example.communityProject.service;

import com.example.communityProject.dto.CommentDto;
import com.example.communityProject.entity.Comment;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.CommentRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import com.example.communityProject.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    private CommentService commentService;

    private User user;
    private Post post;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postRepository, userRepository, jwtUtil);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        post = new Post();
        post.setUser(user);

        comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setBody("Test Comment");

        commentDto = new CommentDto();
        commentDto.setAuthorId(user.getId());
    }

    @Test
    @DisplayName("게시글 ID로 댓글 목록을 조회하면, 댓글 DTO 리스트를 반환해야 한다.")
    void getComments_ShouldReturnCommentDtoList() {
        when(commentRepository.findByPostId(1L)).thenReturn(List.of(comment));
        List<CommentDto> comments = commentService.getComments(1L);
        assertEquals(1, comments.size());
        assertEquals(comment.getBody(), comments.get(0).getBody());
    }

    @Test
    @DisplayName("댓글을 생성하면, 생성된 댓글 DTO를 반환해야 한다.")
    void createComment_ShouldReturnCreatedCommentDto() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto createdComment = commentService.createComment(1L, commentDto);
        assertNotNull(createdComment);
        assertEquals(comment.getId(), createdComment.getId());
    }

    @Test
    @DisplayName("댓글을 수정하면 수정된 댓글 Dto를 반환해야 한다.")
    void updateComment() {
        String token = "dummyToken";
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto updatedDto = new CommentDto();
        updatedDto.setAuthorId(user.getId());
        CommentDto updatedCommentDto = commentService.updateComment(1L, updatedDto, token);
        assertNotNull(updatedCommentDto);
        assertEquals(comment.getBody(), updatedCommentDto.getBody());
    }

    @Test
    @DisplayName("댓글 수정 시, 작성자가 아닌 경우 예외가 발생해야 한다.")
    void updateComment_ShouldThrowException_WhenUserNotAuthorized() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(2L);

        assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(1L, commentDto, "token"));
    }

    @Test
    @DisplayName("댓글 삭제 시, 작성자가 아닌 경우 예외가 발생해야 한다.")
    void deleteComment_ShouldThrowException_WhenUserNotAuthorized() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(2L);

        assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(1L, "token"));
    }

    @Test
    @DisplayName("댓글을 삭제하면 삭제된 댓글의 Dto를 반환되어야 한다")
    void deleteComment() {
        String token = "dummyToken";
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        CommentDto deletedCommentDto = commentService.deleteComment(1L, token);
        assertNotNull(deletedCommentDto);
        assertEquals(comment.getBody(), deletedCommentDto.getBody());
    }



    //    @Mock
//    private CommentRepository commentRepository;
//
//    @InjectMocks
//    private CommentService commentService;
//
//    @Test
//    void getComments() {
//        // Arrange
//        Comment comment1 = new Comment(1L, );
//        comment1.setId(1L);
//        comment1.setBody("I agree");
//        comment1.setUser(new User());
//        comment1.setPost(new Post());
//        comment1.setCreatedAt(LocalDateTime.parse("2025-01-01T00:00:00"));
//
//        given(commentRepository.findById(comment1.getId())).willReturn(Optional.of(comment1));
//
//        // Act
//        Optional<Comment> foundComment = commentRepository.findById(comment1.getId());
//
//        // Assert
//        assertTrue(foundComment.isPresent());
//        assertEquals("I agree", foundComment.get().getBody());
//        verify(commentRepository).findById(comment1.getId());
//    }
//
//
//    @Test
//    void updateComment() {
//        // Arrange
//        Comment comment = new Comment();
//        comment.setId(3L);
//        comment.setBody("Old Comment");
//        comment.setUser(new User());
//        comment.setPost(new Post());
//
//        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
//        given(commentRepository.save(any(Comment.class))).willReturn(comment);
//
//        // Act
//        comment.setBody("Updated Comment");
//        Comment updatedComment = commentRepository.save(comment);
//
//        // Assert
//        assertEquals("Updated Comment", updatedComment.getBody());
//        verify(commentRepository).save(any(Comment.class));
//    }
//
//    @Test
//    void deleteComment() {
//        // Arrange
//        Comment comment = new Comment();
//        comment.setId(4L);
//        comment.setBody("Will be deleted");
//        comment.setUser(new User());
//        comment.setPost(new Post());
//
//        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
//        doNothing().when(commentRepository).delete(comment);
//
//        // Act
//        commentRepository.delete(comment);
//
//        // Assert
//        verify(commentRepository).delete(comment);
//    }

}
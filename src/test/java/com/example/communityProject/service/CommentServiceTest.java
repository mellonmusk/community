package com.example.communityProject.service;

import com.example.communityProject.dto.CommentDto;
import com.example.communityProject.entity.Comment;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.CommentRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import com.example.communityProject.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    private String token = "mockToken";


    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postRepository, userRepository, jwtUtil);

        user = User.builder()
                .email("test@example.com")
                .id(1L)
                .build();

        post = Post.builder()
                .user(user)
                .build();

        comment = Comment.builder()
                .id(1L)
                .body("Original Comment")
                .user(user)
                .post(post)
                .build();

        commentDto = CommentDto.builder()
                .authorId(1L)
                .body("Updated Comment")
                .build();
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
    @DisplayName("patchComment: 댓글이 성공적으로 수정된다")
    @Transactional
    void patchComment_Success() {
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        commentDto = commentDto.toBuilder().id(1L).build();
        Comment updatedComment =commentService.patchComment(comment, commentDto);

        assertNotNull(updatedComment);
        assertEquals("Updated Comment", updatedComment.getBody());
    }

    @Test
    @DisplayName("patchComment: ID가 일치하지 않으면 예외가 발생한다")
    void patchComment_InvalidId_ExceptionThrown() {
        commentDto = commentDto.toBuilder().id(2L).build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.patchComment(comment, commentDto)
        );

        assertEquals("댓글 수정 실패, 잘못된 id가 입력됐습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updateComment: 사용자가 본인 댓글을 성공적으로 수정한다")
    @Transactional
    void updateComment_Success() {
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        commentDto = commentDto.toBuilder().id(1L).build();
        CommentDto updatedDto = commentService.updateComment(1L, commentDto, token);

        assertNotNull(updatedDto);
        assertEquals("Updated Comment", updatedDto.getBody());
    }

    @Test
    @DisplayName("updateComment: 사용자가 본인 댓글이 아닐 경우 예외 발생")
    void updateComment_Forbidden_ExceptionThrown() {
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(2L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.updateComment(1L, commentDto, token)
        );

        assertEquals("수정 권한이 없습니다.", exception.getMessage());
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
}
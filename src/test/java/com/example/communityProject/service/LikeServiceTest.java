package com.example.communityProject.service;

import com.example.communityProject.dto.LikeDto;
import com.example.communityProject.entity.Like;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.exception.AlreadyLikedException;
import com.example.communityProject.repository.LikeRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    private LikeService likeService;
    private User user1;
    private User user2;
    private LikeDto likeDto;
    private Post post;
    private Like like1;
    private Like like2;

    @BeforeEach
    void setUp() {
        this.likeService = new LikeService(likeRepository, userRepository, postRepository);

        user1 = User.builder()
                .email("test1@example.com")
                .password("encodedPassword")
                .id(1L)
                .build();

        user2 = User.builder()
                .email("test2@example.com")
                .password("encodedPassword")
                .id(2L)
                .build();

        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .user(user1)
                .likes(0L)
                .views(0L)
                .build();

        like1 = new Like(null, post, user1);
        like2 = new Like(null, post, user2);

        likeDto = new LikeDto(null, 1L, 1L);
    }


    @Test
    @DisplayName("좋아요 생성 성공")
    void createLike_Success() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(likeRepository.existsByUserIdAndPostId(post.getId(), user1.getId())).thenReturn(false);
        when(likeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LikeDto createdLike = likeService.createLike(post.getId(), likeDto);

        assertNotNull(createdLike);
        assertEquals(post.getId(), createdLike.getPostId());
        assertEquals(user1.getId(), createdLike.getUserId());

        verify(postRepository, times(1)).incrementLikes(post.getId());
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("게시글이 존재하지 않으면 예외 발생")
    void createLike_Fail_PostNotFound() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> likeService.createLike(post.getId(), likeDto));

        assertEquals("좋아요 생성 실패, 게시글 ID가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("사용자가 존재하지 않으면 예외 발생")
    void createLike_Fail_UserNotFound() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.createLike(post.getId(), likeDto));

        assertEquals("좋아요 생성 실패, 작성자 ID가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("이미 좋아요를 누른 경우 예외 발생")
    void createLike_Fail_AlreadyLiked() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(likeRepository.existsByUserIdAndPostId(post.getId(), user1.getId())).thenReturn(true);

        Exception exception = assertThrows(AlreadyLikedException.class,
                () -> likeService.createLike(post.getId(), likeDto));

        assertEquals("이미 좋아요를 누른 게시글입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("DTO에 ID가 포함된 경우 예외 발생")
    void createLike_Fail_DtoHasId() {
        likeDto = new LikeDto(100L, post.getId(), user1.getId()); // ID가 존재하는 경우

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(likeRepository.existsByUserIdAndPostId(post.getId(), user1.getId())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.createLike(post.getId(), likeDto));

        assertEquals("좋아요 증가 실패, 좋아요의 id가 없어야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("DTO의 postId 또는 userId가 실제 Post와 User의 ID와 일치하지 않으면 예외 발생")
    void createLike_Fail_InvalidPostOrUserId() {
        LikeDto invalidLikeDto = new LikeDto(null, 999L, user1.getId()); // 잘못된 postId

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(likeRepository.existsByUserIdAndPostId(post.getId(), user1.getId())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.createLike(post.getId(), invalidLikeDto));

        assertEquals("좋아요 증가 실패, 게시글의 id가 잘못됐습니다.", exception.getMessage());
    }


    @Test
    void getLike() {
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(true);
        boolean result = likeService.getLike(1L, 1L);
        assertTrue(result);
    }

    @Test
    void deleteLike() {
        when(likeRepository.findByUserIdAndPostId(1L, 1L)).thenReturn(Optional.of(like1));
        doNothing().when(likeRepository).delete(like1);
        doNothing().when(postRepository).decrementLikes(1L);

        LikeDto result = likeService.deleteLike(1L, 1L);
        assertNotNull(result);
    }
}
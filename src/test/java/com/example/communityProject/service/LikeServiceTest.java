package com.example.communityProject.service;

import com.example.communityProject.dto.LikeDto;
import com.example.communityProject.entity.Like;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.LikeRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
    private LikeDto dto;
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

        dto = new LikeDto(null, 1L, 1L);
    }


    @Test
    void createLike() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user1));
        when(likeRepository.existsByUserIdAndPostId(1L, 1L)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like1);

        doNothing().when(postRepository).incrementLikes(1L);

        LikeDto result = likeService.createLike(1L, dto);
        assertNotNull(result);
        assertEquals(result.getPostId(), postId);
        assertEquals(result.getUserId(), dto.getUserId());
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
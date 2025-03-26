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

        user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setPassword("encodedPassword");
        user1.setId(1L);

        user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setPassword("encodedPassword");
        user2.setId(2L);

        post = new Post();
        post.setId(1L);
        post.setUser(user1);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setViews(0L);
        postRepository.save(post);

        like1 = new Like(null, post, user1);
        like2 = new Like(null, post, user2);

        dto = new LikeDto(null,1L, 1L);
    }

    @Test
    void countByPostId() {
        when(likeRepository.countByPostId(1L)).thenReturn(2L);
        Long cnt = likeService.countByPostId(1L);
        assertEquals(2L, cnt);
    }

    @Test
    void createLike() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user1));
        when(likeRepository.existsByUser_IdAndPost_Id(1L, 1L)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like1);


        doNothing().when(postRepository).incrementLikes(1L);

        LikeDto result = likeService.createLike(1L, dto);
        assertNotNull(result);
    }

    @Test
    void getLike() {
        when(likeRepository.existsByUser_IdAndPost_Id(1L, 1L)).thenReturn(true);
        boolean result = likeService.getLike(1L, 1L);
        assertTrue(result);
    }

    @Test
    void deleteLike() {
        when(likeRepository.findByUser_IdAndPost_Id(1L, 1L)).thenReturn(Optional.of(like1));
        doNothing().when(likeRepository).delete(like1);
        doNothing().when(postRepository).decrementLikes(1L);

        LikeDto result = likeService.deleteLike(1L, 1L);
        assertNotNull(result);
    }
}
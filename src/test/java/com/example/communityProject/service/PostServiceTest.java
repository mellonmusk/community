package com.example.communityProject.service;

import com.example.communityProject.dto.PostDto;
import com.example.communityProject.entity.*;
import com.example.communityProject.repository.*;
import com.example.communityProject.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private JwtUtil jwtUtil;

    private PostService postService;

    private Post post;
    private User user;
    private PostDto postDto;
    private Comment comment;
    private Like like;
    private Image image;
    private Image newImage;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository, commentRepository, likeRepository, imageRepository, imageService, jwtUtil);

        user = new User();
        user.setId(1L);

        image = new Image();
        image.setFileName("test file name");

        newImage = new Image();
        newImage.setFileName("new file name");

        post = new Post();
        post.setUser(user);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setViews(0L);
        post.setImages(new ArrayList<>(List.of(image)));
        postRepository.save(post);

        postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setTitle("Test Title");
        postDto.setContent("Test Content");
        postDto.setViews(0L);

        comment = new Comment(1L, post, user, "comment body", LocalDateTime.now());

        like = new Like(1L, post, user);


    }

    @Test
    @DisplayName("게시글 리스트를 조회하면 PostDto 리스트가 반환된다")
    void getPostList() {
        when(postRepository.findAll()).thenReturn(new ArrayList<>(List.of(post)));
        assertEquals(1, postService.getPostList().size());
    }

    @Test
    @DisplayName("게시글이 존재하면 해당 PostDto를 반환한다")
    void getPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assertNotNull(postService.getPost(1L));
    }

    @Test
    @DisplayName("게시글을 생성하면 PostDto가 반환된다")
    void createPost() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        LocalDateTime createdAt = LocalDateTime.now();
        post.setCreatedAt(createdAt);
        when(postRepository.save(any())).thenReturn(post);
        PostDto createdDto = postService.createPost(postDto);
        postDto.setCreatedAt(createdAt);
        postDto.setViews(0L);
        assertEquals(createdDto, postDto);
    }

    @Test
    @DisplayName("게시글 조회수를 수정하면 PostDto가 반환된다")
    void updatePostView() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postDto.setViews(postDto.getViews() + 1L);
        when(postRepository.save(any())).thenReturn(post);
        assertEquals(postDto.getViews(), 1L);
        PostDto updatedPostViewDto = postService.updatePostView(1L, postDto);
        assertEquals(updatedPostViewDto.getViews(), 1);
    }

    @Test
    @DisplayName("게시글을 수정하면 PostDto가 반환된다")
    void updatePost() {
        // Given (기존 게시글 존재)
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);

        // 게시글 수정 데이터 설정
        postDto.setTitle("Updated Title");
        postDto.setContent("Updated Content");

        // When (postRepository.save()가 수정된 게시글을 반환)
        post.setTitle("Updated Title");
        post.setContent("Updated Content");
        when(postRepository.save(any())).thenReturn(post);

        // Act (서비스 호출)
        PostDto updatedPostDto = postService.updatePost(1L, postDto, "token");

        // Then (수정된 데이터가 정상적으로 반영되었는지 확인)
        assertNotNull(updatedPostDto);
        assertEquals("Updated Title", updatedPostDto.getTitle());
        assertEquals("Updated Content", updatedPostDto.getContent());
    }


    @Test
    @DisplayName("권한이 없는 사용자가 게시글을 수정하면 예외가 발생한다")
    void updatePost_ShouldThrowException_WhenUserNotAuthorized() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(2L);
        assertThrows(IllegalArgumentException.class, () -> postService.updatePost(1L, postDto, "token"));
    }

    @Test
    @DisplayName("게시글을 삭제하면 PostDto가 반환된다")
    void deletePost() {
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        doNothing().when(commentRepository).deleteByPost_Id(1L);
        doNothing().when(likeRepository).deleteByPost_Id(1L);
        doNothing().when(imageRepository).deleteByPost_Id(1L);

        doNothing().when(postRepository).delete(any());

        PostDto deletedPostDto = postService.deletePost(1L, "token");

        assertEquals(postDto.getAuthorId(), deletedPostDto.getAuthorId());

        verify(commentRepository, times(1)).deleteByPost_Id(1L);
        verify(likeRepository, times(1)).deleteByPost_Id(1L);
        verify(imageRepository, times(1)).deleteByPost_Id(1L);
        verify(postRepository, times(1)).delete(any());
    }


    @Test
    @DisplayName("게시글의 이미지를 업데이트하면 PostDto가 반환된다")
    void updatePostImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        // 기존 게시글에 이미지가 존재하도록 설정
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(imageService.saveImage(any())).thenReturn(newImage);

        // 기존 이미지가 존재하는 경우 삭제하도록 설정
        List<Image> images = new ArrayList<>(List.of(image));
        post.setImages(images);

        doNothing().when(imageService).deleteImage(image.getId());
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostDto updatedPostDto = postService.updatePostImage(1L, file);

        assertNotNull(updatedPostDto);
        assertEquals(newImage.getFileName(), post.getImages().get(0).getFileName());
        verify(imageService).deleteImage(image.getId());
        verify(imageService).saveImage(file);
    }
}

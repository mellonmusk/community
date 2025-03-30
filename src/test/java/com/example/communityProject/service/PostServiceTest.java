package com.example.communityProject.service;

import com.example.communityProject.dto.PostDto;
import com.example.communityProject.dto.UserDto;
import com.example.communityProject.entity.*;
import com.example.communityProject.repository.*;
import com.example.communityProject.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    private PostService postService;

    private Post post;
    private User user;
    private UserDto userDto;
    private PostDto postDto;
    private PostDto newDto;
    private Comment comment;
    private Like like;
    private Image image;
    private Image newImage;
    private String token = "mockToken";

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository, commentRepository, likeRepository, imageRepository, imageService, userService, jwtUtil);

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .build();

        image = Image.builder()
                .fileName("test file name")
                .build();

        newImage = Image.builder()
                .fileName("new file name")
                .build();

        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .user(user)
                .likes(0L)
                .views(0L)
                .images(new ArrayList<>(List.of(image)))
                .build();

        postDto = PostDto.builder()
                .authorId(1L)
                .title("Test Title")
                .content("Test Content")
                .views(0L)
                .likes(0L)
                .build();

        newDto = PostDto.builder()
                .id(1L)
                .title("Updated Title")
                .content("Updated Content")
                .likes(20L)
                .views(200L)
                .build();

        comment = new Comment(1L, post, user, "comment body", LocalDateTime.now());

        like = new Like(1L, post, user);
    }

    @Test
    @DisplayName("게시글 리스트를 조회하면 PostDto 리스트가 반환된다")
    void getPostList() {
        when(postRepository.findAll()).thenReturn(new ArrayList<>(List.of(post)));
        assertEquals(1, postService.getPostListWithComments().size());
    }

    @Test
    @DisplayName("게시글이 존재하면 해당 PostDto를 반환한다")
    void getPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assertNotNull(postService.getPost(1L));
    }

    @Test
    @DisplayName("게시글을 생성하면 PostDto가 반환된다")
    void createPost_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        LocalDateTime createdAt = LocalDateTime.now();
        post = post.toBuilder()
                .createdAt(createdAt)
                .user(user)
                .build();
        when(postRepository.save(any())).thenReturn(post);
        PostDto createdDto = postService.createPost(postDto);
        postDto = postDto.toBuilder().author(userDto).createdAt(createdAt).views(0L).build();

        assertEquals(createdDto.getContent(), postDto.getContent());
        assertEquals(createdDto.getTitle(), postDto.getTitle());
        assertEquals(createdDto.getAuthorId(), postDto.getAuthorId());
    }

    @Test
    @DisplayName("게시글 생성 실패: 작성자 ID가 유효하지 않음")
    void createPost_Fail_InvalidAuthorId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.createPost(postDto)
        );

        assertEquals("게시글 생성 실패, 작성자 ID가 유효하지 않습니다.", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 생성 실패: DTO에 ID가 존재함")
    void createPost_Fail_DtoHasId() {
        postDto = postDto.toBuilder().id(1L).build(); // DTO에 ID 설정

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.createPost(postDto)
        );

        assertEquals("게시글 생성 실패, 게시글의 id가 없어야 합니다.", exception.getMessage());

        verify(userRepository, never()).findById(anyLong());
        verify(postRepository, never()).save(any(Post.class));
    }


    @Test
    @DisplayName("patchPost: 게시글이 성공적으로 수정된다")
    @Transactional
    void patchPost_Success() {
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post updatedPost = postService.patchPost(post, newDto);

        assertNotNull(updatedPost);
        assertEquals("Updated Title", updatedPost.getTitle());
        assertEquals("Updated Content", updatedPost.getContent());
        assertEquals(20L, updatedPost.getLikes());
        assertEquals(200L, updatedPost.getViews());
    }

    @Test
    @DisplayName("patchPost: ID가 일치하지 않으면 예외가 발생한다")
    void patchPost_InvalidId_ExceptionThrown() {
        newDto = newDto.toBuilder().id(2L).build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.patchPost(post, newDto)
        );

        assertEquals("게시글 수정 실패, 잘못된 id가 입력됐습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updatePost: 사용자가 본인 게시글을 성공적으로 수정한다")
    @Transactional
    void updatePost_Success() {
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto updatedDto = postService.updatePost(1L, newDto, token);

        assertNotNull(updatedDto);
        assertEquals("Updated Title", updatedDto.getTitle());
        assertEquals("Updated Content", updatedDto.getContent());
    }

    @Test
    @DisplayName("updatePost: 사용자가 본인 게시글이 아닐 경우 예외 발생")
    void updatePost_Forbidden_ExceptionThrown() {
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(2L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.updatePost(1L, newDto, token)
        );

        assertEquals("수정 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("updatePostView: 게시글 조회수를 성공적으로 증가시킨다")
    @Transactional
    void updatePostView_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto updatedDto = postService.updatePostView(1L, newDto);

        assertNotNull(updatedDto);
        assertEquals(200L, updatedDto.getViews());
    }

    @Test
    @DisplayName("updatePostView: 대상 게시글이 존재하지 않으면 예외 발생")
    void updatePostView_NotFound_ExceptionThrown() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.updatePostView(1L, newDto)
        );

        assertEquals("게시글 조회수 증가 실패, 대상 게시글이 없습니다.", exception.getMessage());
    }


    @Test
    @DisplayName("권한이 없는 사용자가 게시글을 수정하면 예외가 발생한다")
    void updatePost_ShouldThrowException_WhenUserNotAuthorized() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(2L);
        assertThrows(IllegalArgumentException.class, () -> postService.updatePost(1L, postDto, token));
    }

    @Test
    @DisplayName("게시글을 삭제하면 PostDto가 반환된다")
    void deletePost() {
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        doNothing().when(commentRepository).deleteByPost_Id(1L);
        doNothing().when(likeRepository).deleteByPostId(1L);
        doNothing().when(imageRepository).deleteByPost_Id(1L);

        doNothing().when(postRepository).delete(any());

        PostDto deletedPostDto = postService.deletePost(1L, token);

        assertEquals(postDto.getAuthorId(), deletedPostDto.getAuthorId());

        verify(commentRepository, times(1)).deleteByPost_Id(1L);
        verify(likeRepository, times(1)).deleteByPostId(1L);
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
        post = post.toBuilder()
                .images(images)
                .build();

        doNothing().when(imageService).deleteImage(image.getId());
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostDto updatedPostDto = postService.updatePostImage(1L, file);

        assertNotNull(updatedPostDto);
        assertEquals(newImage.getFileName(), updatedPostDto.getImage());
        verify(imageService).deleteImage(image.getId());
        verify(imageService).saveImage(file);
    }
}

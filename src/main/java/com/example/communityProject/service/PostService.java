package com.example.communityProject.service;

import com.example.communityProject.dto.CommentDto;
import com.example.communityProject.dto.PostDto;
import com.example.communityProject.dto.UserDto;
import com.example.communityProject.entity.Comment;
import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.*;
import com.example.communityProject.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository, LikeRepository likeRepository, ImageRepository imageRepository, ImageService imageService, UserService userService, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.imageRepository = imageRepository;
        this.imageService = imageService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostListWithComments() {
        // 1. 모든 게시글 조회
        List<Post> posts = postRepository.findAll();

        // 2. 게시글 ID 리스트 생성
        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        // 3. 게시글 ID에 기반하여 댓글 조회
        List<Comment> comments = commentRepository.findAllByPostIds(postIds);

        // 4. 댓글을 게시글 ID 기준으로 그룹화
        Map<Long, List<Comment>> commentMap = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getPost().getId()));

        // 5. PostDto에 댓글 매핑하여 반환
        return posts.stream()
                .map(post -> {
                    PostDto postDto = PostDto.createPostDto(post); // 게시글 DTO 생성

                    // 댓글 정보 설정
                    List<Comment> postComments = commentMap.getOrDefault(post.getId(), List.of());
                    postDto.setComments(postComments.stream()
                            .map(comment -> CommentDto.createCommentDto(comment)) // CommentDto 변환
                            .collect(Collectors.toList())); // 댓글 DTO 리스트로 설정

                    // 사용자 프로필 이미지 URL을 Base64로 변환
                    String imagePath = userService.getProfileImagePath(post.getUser().getId());
                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            byte[] imageData = Files.readAllBytes(Paths.get(imagePath));
                            String base64Image = Base64.getEncoder().encodeToString(imageData);
                            UserDto userDto = UserDto.createUserDto(post.getUser());
                            userDto = userDto.toBuilder().profileImageUrl(base64Image).build(); // Base64 형식으로 변환된 프로필 이미지 URL 설정
                            postDto.setAuthor(userDto);
                        } catch (IOException e) {
                            // 이미지 로드 중 오류 발생 시 처리 (예: 기본 이미지 사용)
                            UserDto userDto = UserDto.createUserDto(post.getUser());
                            userDto = userDto.toBuilder().profileImageUrl(null).build();// 기본 이미지 URL로 설정
                            postDto.setAuthor(userDto);
                        }
                    } else {
                        // 이미지 경로가 비어 있을 경우 기본 프로필 이미지 설정
                        UserDto userDto = UserDto.createUserDto(post.getUser());
                        userDto = userDto.toBuilder().profileImageUrl(null).build(); // 기본 이미지 URL로 설정
                        postDto.setAuthor(userDto);
                    }

                    return postDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 조회 실패, 대상 게시글이 없습니다."));
        return PostDto.createPostDto(post);
    }

    @Transactional
    public PostDto createPost(PostDto dto) {
        validatePostDto(dto);
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 생성 실패, 작성자 ID가 유효하지 않습니다."));
        Post post = createPost(dto, user, LocalDateTime.now());
        Post created = postRepository.save(post);
        return PostDto.createPostDto(created);
    }

    @Transactional
    public Post patchPost(Post post, PostDto dto) {
        // 예외 발생
        if (!post.getId().equals(dto.getId()))
            throw new IllegalArgumentException("게시글 수정 실패, 잘못된 id가 입력됐습니다.");

        // 값이 존재하는 경우에만 업데이트
        if (dto.getTitle() != null) {
            post = post.toBuilder().title(dto.getTitle()).build();
        }
        if (dto.getContent() != null) {
            post = post.toBuilder().content(dto.getContent()).build();
        }
        if (dto.getLikes() != null) {
            post = post.toBuilder().likes(dto.getLikes()).build();
        }
        if (dto.getViews() != null) {
            post = post.toBuilder().views(dto.getViews()).build();
        }
        return postRepository.save(post);
    }


    @Transactional
    public PostDto updatePost(Long id, PostDto dto, String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        // 타깃 조회하기
        Post target = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글 수정 실패, 대상 게시글이 없습니다."));
        // 현재 로그인한 사용자가 작성자인지 확인
        if (!target.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 업데이트하기
        target=patchPost(target, dto); // 기존 데이터에 새 데이터 붙이기
        Post updated = postRepository.save(target);
        return PostDto.createPostDto(updated);
    }


    @Transactional
    public PostDto updatePostView(Long id, PostDto dto) {
        // 타깃 조회하기
        Post target = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글 조회수 증가 실패, 대상 게시글이 없습니다."));

        // 업데이트하기
        target=patchPost(target, dto); // 기존 데이터에 새 데이터 붙이기
        Post updated = postRepository.save(target);
        return PostDto.createPostDto(updated);
    }


    @Transactional
    public PostDto deletePost(Long id, String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        // 대상 찾기
        Post target = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글 삭제 실패, 대상 게시글이 없습니다."));

        // 작성자와 현재 로그인한 사용자 비교
        if (!target.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        // 관련 댓글 삭제
        commentRepository.deleteByPost_Id(id);
        // 관련 좋아요 삭제
        likeRepository.deleteByPostId(id);
        // 관련 이미지 삭제
        imageRepository.deleteByPost_Id(id);
        // 대상 삭제하기
        postRepository.delete(target);
        return PostDto.createPostDto(target);
    }

    @Transactional
    public PostDto updatePostImage(Long postId, MultipartFile file) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 업데이트 실패, 대상 게시글이 없습니다."));

        // 새 이미지 저장
        Image savedImage = imageService.saveImage(file);
        Image newImage = savedImage.toBuilder().post(post).build();
        imageRepository.save(newImage);

        // 기존 이미지 삭제
        if (!post.getImages().isEmpty()) {
            Image oldImage = post.getImages().get(0);
            imageService.deleteImage(oldImage.getId());
            post.getImages().remove(0);
        }

        // 새로운 이미지 설정
        post.getImages().add(newImage);
        Optional<Image> savedImageCheck = imageRepository.findById(newImage.getId());

        Post updatedPost = postRepository.save(post);

        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .image(newImage.getFileName()) // 파일명 반환
                .likes(post.getLikes())
                .authorId(post.getUser().getId())
                .views(post.getViews())
                .createdAt(post.getCreatedAt())
                .build();
    }

    // create post entity from dto
    public Post createPost(PostDto dto, User user, LocalDateTime now) {
        // 엔티티 생성 및 반환
        return new Post(
                null,
                dto.getTitle(),
                dto.getContent(),
                null,
                user,
                dto.getLikes(),
                null,
                dto.getViews(),
                now
        );
    }

    private void validatePostDto(PostDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("게시글 생성 실패, 게시글의 id가 없어야 합니다.");
        }
    }
}
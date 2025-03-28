package com.example.communityProject.service;

import com.example.communityProject.dto.PostDto;
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
import java.time.LocalDateTime;
import java.util.List;
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
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository, LikeRepository likeRepository, ImageRepository imageRepository, ImageService imageService, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.imageRepository = imageRepository;
        this.imageService = imageService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostList() {
        return postRepository.findAll()
                .stream()
                .map(post -> PostDto.createPostDto(post))
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
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 생성 실패, 작성자 ID가 유효하지 않습니다."));
        Post post = createPost(dto, user, LocalDateTime.now());
        Post created = postRepository.save(post);
        return PostDto.createPostDto(created);
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
        target.patch(dto); // 기존 데이터에 새 데이터 붙이기
        Post updated = postRepository.save(target);
        return PostDto.createPostDto(updated);
    }

    @Transactional
    public PostDto updatePostView(Long id, PostDto dto) {
        // 타깃 조회하기
        Post target = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글 조회수 증가 실패, 대상 게시글이 없습니다."));

        // 업데이트하기
        target.patch(dto); // 기존 데이터에 새 데이터 붙이기
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

    public Post createPost(PostDto dto, User user, LocalDateTime now) {
        // 엔티티 생성 및 반환
        return new Post(
                null,
                dto.getTitle(),
                dto.getContent(),
                null,
                user,
                dto.getLikes(),
                dto.getViews(),
                now
        );
    }
    private void validatePostDto(PostDto dto){
        if (dto.getId() != null){
            throw new IllegalArgumentException("게시글 생성 실패, 게시글의 id가 없어야 합니다.");
        }
    }
}
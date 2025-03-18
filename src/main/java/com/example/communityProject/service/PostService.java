package com.example.communityProject.service;

import com.example.communityProject.dto.PostDto;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private ImageRepository imageRepository;

    public List<PostDto> getPostList() {
        return postRepository.findAll()
                .stream()
                .map(post -> PostDto.createPostDto(post))
                .collect(Collectors.toList());
    }

    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 조회 실패, 대상 게시글이 없습니다."));
        return PostDto.createPostDto(post);
    }

    @Transactional
    public PostDto createPost(PostDto dto) {
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패, 작성자 ID가 유효하지 않습니다."));
        Post post = Post.createPost(dto, user, LocalDateTime.now());
        Post created = postRepository.save(post);
        return PostDto.createPostDto(created);
    }

    @Transactional
    public PostDto updatePost(Long id, PostDto dto) {
        // 타깃 조회하기
        Post target = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 수정 실패, 대상 게시글이 없습니다."));
        // 업데이트하기
        target.patch(dto); // 기존 데이터에 새 데이터 붙이기
        Post updated = postRepository.save(target);
        return PostDto.createPostDto(updated);
    }

    @Transactional
    public PostDto deletePost(Long id) {
        // 대상 찾기
        Post target = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 삭제 실패, 대상 게시글이 없습니다."));
        // 관련 댓글 삭제
        commentRepository.deleteByPost_Id(id);
        // 관련 좋아요 삭제
        likeRepository.deleteByPost_Id(id);
        // 관련 이미지 삭제
        imageRepository.deleteByPost_Id(id);
        // 대상 삭제하기
        postRepository.delete(target);
        return PostDto.createPostDto(target);
    }
}
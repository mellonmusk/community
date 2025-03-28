package com.example.communityProject.service;

import com.example.communityProject.dto.LikeDto;
import com.example.communityProject.entity.Like;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.exception.AlreadyLikedException;
import com.example.communityProject.repository.LikeRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {
    private LikeRepository likeRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public LikeDto createLike(Long postId, LikeDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("좋아요 생성 실패, 게시글 ID가 유효하지 않습니다."));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("좋아요 생성 실패, 작성자 ID가 유효하지 않습니다."));
        if (likeRepository.existsByUserIdAndPostId(postId, dto.getUserId())) {
            throw new AlreadyLikedException("이미 좋아요를 누른 게시글입니다.");
        }
        Like like = createLike(dto, user, post);
        Like created = likeRepository.save(like);
        postRepository.incrementLikes(postId);
        return LikeDto.createLikeDto(created);
    }

    @Transactional(readOnly = true)
    public boolean getLike(Long postId, Long userId) {
        boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, postId);
        return isLiked;
    }

    @Transactional
    public LikeDto deleteLike(Long postId, Long userId) {
        Like target = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new RuntimeException("좋아요 삭제 실패, 주어진 사용자와 게시글에 해당하는 좋아요가 없습니다."));
        likeRepository.delete(target);
        postRepository.decrementLikes(postId);
        return LikeDto.createLikeDto(target);
    }

    public Like createLike(LikeDto dto, User user, Post post) {
        // 예외 발생
        validateLikeDto(dto, user, post);

        return new Like(
                dto.getId(),
                post,
                user
        );
    }

    private void validateLikeDto(LikeDto dto, User user, Post post) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("좋아요 증가 실패, 좋아요의 id가 없어야 합니다.");
        }
        if (!dto.getPostId().equals(post.getId())) {
            throw new IllegalArgumentException("좋아요 증가 실패, 게시글의 id가 잘못됐습니다.");
        }
        if (!dto.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("좋아요 증가 실패, 사용자의 id가 잘못됐습니다.");
        }
    }
}

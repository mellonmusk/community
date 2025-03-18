package com.example.communityProject.service;

import com.example.communityProject.dto.LikeDto;
import com.example.communityProject.entity.Like;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.LikeRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    public Long countByPostId(Long id) {
        Long likeCount = likeRepository.countByPostId(id);
        return likeCount;
    }

//    public List<LikeDto> getLikes(Long postId) {
//    }

    @Transactional
    public LikeDto createLike(Long postId, LikeDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("좋아요 생성 실패, 게시글 ID가 유효하지 않습니다."));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("좋아요 생성 실패, 작성자 ID가 유효하지 않습니다."));
        if (likeRepository.existsByUser_IdAndPost_Id(postId, dto.getUserId())) {
            throw new RuntimeException("이미 좋아요를 누른 게시글입니다.");
        }
        Like like = Like.createLike(dto, user, post);
        Like created = likeRepository.save(like);
        return LikeDto.createLikeDto(created);
    }

//    public boolean getLike(Long postId, Long userId) {
//        boolean isLiked= likeRepository.existsByUser_IdAndPost_Id(postId, userId);
//        return isLiked;
//    }

    @Transactional
    public LikeDto deleteLike(Long postId, Long userId) {
        Like target = likeRepository.findByUser_IdAndPost_Id(postId, userId)
                        .orElseThrow(()->new RuntimeException("좋아요 삭제 실패, 주어진 사용자와 게시글에 해당하는 좋아요가 없습니다."));
        likeRepository.delete(target);
        return LikeDto.createLikeDto(target);
    }
}

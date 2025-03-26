package com.example.communityProject.service;

import com.example.communityProject.dto.CommentDto;
import com.example.communityProject.entity.Comment;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.CommentRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import com.example.communityProject.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<CommentDto> getComments(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(comment -> CommentDto.createCommentDto(comment))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto createComment(Long postId, CommentDto dto) {
        // db에서 게시글, 사용자 조회 및 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("댓글 생성 실패, 대상 게시글이 없습니다."));
        log.info(dto.toString());
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패, 작성자 ID가 유효하지 않습니다."));
        // 댓글 entity 생성
        Comment comment = Comment.createComment(dto, post, user, LocalDateTime.now());
        // 댓글 entity를 db에 저장
        Comment created=commentRepository.save(comment);
        // dto로 변환해 반환
        return CommentDto.createCommentDto(created);

    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto dto, String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        // db에서 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("댓글 수정 실패, 대상 댓글이 없습니다."));
        // 현재 로그인한 사용자가 작성자인지 확인
        log.info("댓글 작성자: "+target.getUser().getId()+"로그인한 사용자: "+userId);
        if (!target.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        // 댓글 수정
        target.patch(dto);
        // db 갱신
        Comment updated = commentRepository.save(target);
        // 댓글 entity를 dto로 변환해 반환
        return CommentDto.createCommentDto(updated);
    }

    @Transactional
    public CommentDto deleteComment(Long commentId, String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("댓글 삭제 실패, 대상 댓글이 없습니다."));
        // 작성자와 현재 로그인한 사용자 비교
        if (!target.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        // 댓글 삭제
        commentRepository.delete(target);
        // 삭제 댓글을 dto로 변환해 반환
        return CommentDto.createCommentDto(target);
    }
}

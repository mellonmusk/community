package com.example.communityProject.service;

import com.example.communityProject.dto.CommentDto;
import com.example.communityProject.entity.Comment;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.CommentRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    public List<CommentDto> getComments(Long postId) {
//        // 댓글 조회
//        List<Comment> comments = commentRepository.findByPostId(postId);
//        // entity -> dto
//        List<CommentDto> dtos = new ArrayList<CommentDto>();
//        for (int i = 0; i < comments.size(); i++) {
//            Comment c = comments.get(i);
//            CommentDto dto = CommentDto.createCommentDto(c);
//            dtos.add(dto);
//        }
//        // 결과 반환
//        return dtos;
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
    public CommentDto updateComment(Long commentId, CommentDto dto) {
        // db에서 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("댓글 수정 실패, 대상 댓글이 없습니다."));
        // 댓글 수정
        target.patch(dto);
        // db 갱신
        Comment updated = commentRepository.save(target);
        // 댓글 entity를 dto로 변환해 반환
        return CommentDto.createCommentDto(updated);
    }

    @Transactional
    public CommentDto deleteComment(Long commentId) {
        // 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("댓글 삭제 실패, 대상 댓글이 없습니다."));
        // 댓글 삭제
        commentRepository.delete(target);
        // 삭제 댓글을 dto로 변환해 반환
        return CommentDto.createCommentDto(target);
    }
}

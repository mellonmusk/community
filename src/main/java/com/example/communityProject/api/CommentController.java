package com.example.communityProject.api;

import com.example.communityProject.dto.CommentDto;
import com.example.communityProject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    // 댓글 조회
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId){
        List<CommentDto> dtos = commentService.getComments(postId);
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }
    // 댓글 생성
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId,
                                                          @RequestBody CommentDto dto){
        CommentDto createdDto = commentService.createComment(postId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }
    // 댓글 수정
    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId,
                                                    @RequestBody CommentDto dto){
        CommentDto updatedDto = commentService.updateComment(commentId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }
    // 댓글 삭제
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDto> deleteComment(@PathVariable Long commentId){
        CommentDto deletedDto = commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedDto);
    }
}

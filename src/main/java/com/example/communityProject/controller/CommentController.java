package com.example.communityProject.controller;

import com.example.communityProject.dto.CommentDto;
import com.example.communityProject.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 조회
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        List<CommentDto> dtos = commentService.getComments(postId);
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }


    // 댓글 생성 (Only TA or Professor)
    @PreAuthorize("hasRole('TA') or hasRole('PROFESSOR')")
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId,
                                                    @RequestBody CommentDto dto) {
        CommentDto createdDto = commentService.createComment(postId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }

    // 댓글 수정 (Only TA or Professor)
    @PreAuthorize("hasRole('TA') or hasRole('PROFESSOR')")
    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId,
                                                    @RequestBody CommentDto dto,
                                                    @RequestHeader("Authorization") String token) {
        try {
            CommentDto updatedDto = commentService.updateComment(commentId, dto, token);
            return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);  // 권한 없음 (403)
        } catch (
                EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 게시글 없음 (404)
        }
    }

    // 댓글 삭제 (Only TA or Professor)
    @PreAuthorize("hasRole('TA') or hasRole('PROFESSOR')")
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDto> deleteComment(@PathVariable Long commentId, @RequestHeader("Authorization") String token) {
        try {
            CommentDto deletedDto = commentService.deleteComment(commentId, token);
            return ResponseEntity.status(HttpStatus.OK).body(deletedDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  // 권한 없음 (403)
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 게시글 없음 (404)
        }
    }
}

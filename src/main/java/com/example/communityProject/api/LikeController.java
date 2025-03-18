package com.example.communityProject.api;

import com.example.communityProject.dto.LikeDto;
import com.example.communityProject.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LikeController {
    @Autowired
    private LikeService likeService;

    // 게시글의 좋아요 개수 조회
    @GetMapping("/api/posts/{postId}/likes")
    public ResponseEntity<Long> getLikes(@PathVariable Long postId){
        Long dtos = likeService.countByPostId(postId);
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }
    // 좋아요 생성
    @PostMapping("/api/posts/{postId}/likes")
    public ResponseEntity<LikeDto> createLike(@PathVariable Long postId,
                                                    @RequestBody LikeDto dto){
        LikeDto createdDto = likeService.createLike(postId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }
//    // 좋아요 여부 조회
//    @GetMapping("/api/posts/{postId}/likes/{userId}")
//    public ResponseEntity<Boolean> checkLike(@PathVariable Long postId, @PathVariable Long userId) {
//        boolean isLiked = likeService.getLike(postId, userId);
//        return ResponseEntity.status(HttpStatus.OK).body(isLiked);
//    }
    // 좋아요 취소
    @DeleteMapping("/api/posts/{postId}/likes/{userId}")
    public ResponseEntity<LikeDto> deleteLike(@PathVariable Long postId, @PathVariable Long userId){
        LikeDto deletedDto = likeService.deleteLike(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedDto);
    }
}

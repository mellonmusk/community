package com.example.communityProject.api;

import com.example.communityProject.dto.PostDto;
import com.example.communityProject.service.LikeService;
import com.example.communityProject.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {
    @Autowired
    private PostService postService;

    // 게시글 목록 조회
    @GetMapping("/api/posts")
    public ResponseEntity<List<PostDto>> getPostList(){
        List<PostDto> postList = postService.getPostList();
        return ResponseEntity.status(HttpStatus.OK).body(postList);
    }

    // 게시글 조회
    @GetMapping("/api/posts/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id){
        PostDto post = postService.getPost(id);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    // 게시글 생성
    @PostMapping("/api/posts")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto dto){
        PostDto createdDto = postService.createPost(dto);
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }

    // 게시글 수정
    @PatchMapping("/api/posts/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody PostDto dto, @RequestHeader("Authorization") String token){
        try {
            PostDto updatedPost = postService.updatePost(id, dto, token);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);  // 권한 없음 (403)
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 게시글 없음 (404)
        }
    }

    // 게시글 조회수 증가
    @PatchMapping("/api/posts/{id}/views")
    public ResponseEntity<PostDto> updatePostView(@PathVariable Long id, @RequestBody PostDto dto){
        try {
            PostDto updatedPost = postService.updatePostView(id, dto);
            return ResponseEntity.ok(updatedPost);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 게시글 없음 (404)
        }
    }

    // 게시글 삭제
    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<PostDto> deletePost(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            PostDto deletedDto = postService.deletePost(id, token);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  // 권한 없음 (403)
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 게시글 없음 (404)
        }
    }
}

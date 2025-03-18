package com.example.communityProject.api;

import com.example.communityProject.dto.PostDto;
import com.example.communityProject.service.LikeService;
import com.example.communityProject.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody PostDto dto){
        PostDto updatedDto = postService.updatePost(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    // 게시글 삭제
    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<PostDto> deletePost(@PathVariable Long id){
        PostDto deletedDto = postService.deletePost(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}

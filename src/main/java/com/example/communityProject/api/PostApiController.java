package com.example.communityProject.api;

import com.example.communityProject.dto.PostForm;
import com.example.communityProject.entity.Post;
import com.example.communityProject.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostApiController {
    @Autowired
    private PostService postService;

    // 게시글 목록 조회
    @GetMapping("/api/posts")
    public ResponseEntity<List<PostForm>> getPostList(){
        List<PostForm> postList = postService.getPostList();
        return ResponseEntity.status(HttpStatus.OK).body(postList);
    }

    // 게시글 조회
    @GetMapping("/api/posts/{id}")
    public ResponseEntity<PostForm> getPost(@PathVariable Long id){
        PostForm post = postService.getPost(id);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    // 게시글 생성
    @PostMapping("/api/posts")
    public ResponseEntity<PostForm> createPost(@RequestBody PostForm dto){
        PostForm createdDto = postService.createPost(dto);
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }

    // 게시글 수정
    @PatchMapping("/api/posts/{id}")
    public ResponseEntity<PostForm> updatePost(@PathVariable Long id, @RequestBody PostForm dto){
        PostForm updatedDto = postService.updatePost(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    // 게시글 삭제
    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<PostForm> deletePost(@PathVariable Long id){
        PostForm deletedDto = postService.deletePost(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}

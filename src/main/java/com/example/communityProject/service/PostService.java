package com.example.communityProject.service;

import com.example.communityProject.dto.PostForm;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
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
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    public List<PostForm> getPostList() {
        return postRepository.findAll()
                .stream()
                .map(post -> PostForm.createPostDto(post))
                .collect(Collectors.toList());
    }

    public PostForm getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 조회 실패, 대상 게시글이 없습니다."));
        return PostForm.createPostDto(post);
    }

    @Transactional
    public PostForm createPost(PostForm dto) {
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패, 작성자 ID가 유효하지 않습니다."));
        Post post = Post.createPost(dto, user, LocalDateTime.now());
        Post created = postRepository.save(post);
        return PostForm.createPostDto(created);
    }

    @Transactional
    public PostForm updatePost(Long id, PostForm dto) {
        // 타깃 조회하기
        Post target = postRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("게시글 수정 실패, 대상 게시글이 없습니다."));
        // 업데이트하기
        target.patch(dto); // 기존 데이터에 새 데이터 붙이기
        Post updated = postRepository.save(target);
        return PostForm.createPostDto(updated);
    }

    @Transactional
    public PostForm deletePost(Long id) {
        // 대상 찾기
        Post target = postRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("게시글 삭제 실패, 대상 게시글이 없습니다."));
        // 대상 삭제하기
        postRepository.delete(target);
        return PostForm.createPostDto(target);
    }

//    @Transactional
//    public List<Post> createPosts(List<PostForm> dtos) {
//        // dto 묶음을 엔티티 묶음으로 변환하기
//        List<Post> postList = dtos.stream()
//                .map(dto -> dto.toEntity())
//                .collect(Collectors.toList());
//        // 엔티티 묶음을 db에 저장하기
//        postList.stream()
//                .forEach(post -> postRepository.save(post));
//        // 강제 예외 발생시키기
//        postRepository.findById(-1L)
//                .orElseThrow(()->new IllegalArgumentException("실패"));
//        // 결과 값 반환하기
//        return postList;
//    }
}

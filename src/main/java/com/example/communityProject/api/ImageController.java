package com.example.communityProject.api;

import com.example.communityProject.dto.PostDto;
import com.example.communityProject.dto.UserDto;
import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.User;
import com.example.communityProject.service.ImageService;
import com.example.communityProject.service.PostService;
import com.example.communityProject.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;
    private final PostService postService;
    public ImageController(ImageService imageService, UserService userService, PostService postService) {
        this.imageService = imageService;
        this.userService = userService;
        this.postService = postService;
    }


    // 게시글 이미지 업로드
    @PostMapping("/post/{postId}")
    public ResponseEntity<PostDto> uploadPostImage(@PathVariable Long postId, @RequestParam("file") MultipartFile file) {
        try {
            PostDto updatedPost = postService.updatePostImage(postId, file);
            return ResponseEntity.ok(updatedPost);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // 게시글 이미지 조회
    @GetMapping("/post/{id}")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long id) {
        Optional<Image> imageOptional = imageService.getImageByPostId(id);

        if (imageOptional.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Image image = imageOptional.get();
        File file = new File("src/main/resources/static" + image.getFilePath());

        try {
            byte[] imageData = Files.readAllBytes(file.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


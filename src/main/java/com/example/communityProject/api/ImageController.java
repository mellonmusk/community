package com.example.communityProject.api;

import com.example.communityProject.dto.UserDto;
import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.User;
import com.example.communityProject.service.ImageService;
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
    public ImageController(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    // 프로필 이미지 업로드
    @PostMapping("/user/{userId}")
    public ResponseEntity<UserDto> uploadProfileImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            UserDto updatedUser = userService.updateUserProfileImage(userId, file);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 이미지 업로드
    @PostMapping("/post/{postId}")
    public ResponseEntity<Image> uploadPostImage(@PathVariable Long postId, @RequestParam("file") MultipartFile file) {
        try {
            Image savedImage = imageService.savePostImage(file);
            return ResponseEntity.ok(savedImage);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // 프로필 이미지 조회
    @GetMapping("/user/{id}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long id) {
        Optional<Image> imageOptional = imageService.getImageByUserId(id);

        if (imageOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
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
    // 게시글 이미지 조회
    @GetMapping("/post/{id}")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long id) {
        Optional<Image> imageOptional = imageService.getImageByPostId(id);

        if (imageOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
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


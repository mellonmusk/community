package com.example.communityProject.controller;

import com.example.communityProject.dto.UserDto;
import com.example.communityProject.security.JwtUtil;
import com.example.communityProject.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    // 사용자 프로필 목록 조회
    @GetMapping("/api/users")
    public ResponseEntity<List<UserDto>> getUserList() {
        List<UserDto> userList = userService.getUserList();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    // 사용자 프로필 조회
    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto dto = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserDto userDto) {
        UserDto authenticatedUser = userService.authenticateUser(userDto.getEmail(), userDto.getPassword());
        // JWT 토큰 생성
        String accessToken = jwtUtil.generateToken(authenticatedUser.getId());
        // 응답 객체 생성 (토큰 + 유저 정보 포함)
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("user", authenticatedUser);
        responseBody.put("accessToken", accessToken);

        return ResponseEntity.ok(responseBody);
    }


    // 사용자 프로필 등록(회원가입)
    @PostMapping("/api/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
        UserDto createdDto = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }

    // 사용자 프로필 수정
    @PatchMapping("/api/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto, @RequestHeader("Authorization") String token) {
        try {
            UserDto updatedDto = userService.updateUser(id, dto, token);
            return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);  // 권한 없음 (403)
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 게시글 없음 (404)
        }
    }

    // 사용자 삭제
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            UserDto deletedDto = userService.deleteUser(id, token);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);  // 권한 없음 (403)
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 게시글 없음 (404)
        }
    }


    // 프로필 이미지 업로드
    @PostMapping("/api/images/user/{userId}")
    public ResponseEntity<UserDto> uploadProfileImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) throws IOException {
        try {
            String imageUrl = userService.saveImageToLocalFile(file, userId);
            UserDto updatedUser = userService. updateProfileImage(userId, imageUrl);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 프로필 이미지 조회
    @GetMapping("/api/images/user/{id}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long id) {
        String imagePath = userService.getProfileImagePath(id);
        if (imagePath.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(imagePath);

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

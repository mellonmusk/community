package com.example.communityProject.service;

import com.example.communityProject.dto.UserDto;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.*;
import com.example.communityProject.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(PostRepository postRepository, CommentRepository commentRepository, LikeRepository likeRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Value("${file.upload-dir}") // application.properties에서 설정한 경로
    String uploadDir;

    @Transactional(readOnly = true)
    public List<UserDto> getUserList() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserDto.createUserDto(user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 실패, 대상 사용자가 없습니다."));
        return UserDto.createUserDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        User user = createUser(dto, passwordEncoder);
        User created = userRepository.save(user);
        return UserDto.createUserDto(created);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto dto, String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User target = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자 수정 실패, 대상 사용자가 없습니다."));
        if (!target.getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        target.patch(dto);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) { // 비밀번호 변경이 있을 때만 암호화 적용
            target = target.toBuilder()
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .build();
        }
        User updated = userRepository.save(target);
        return UserDto.createUserDto(updated);
    }

    @Transactional
    public UserDto deleteUser(Long id, String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User target = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자 삭제 실패, 대상 사용자가 없습니다."));
        if (!target.getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 사용자가 좋아요를 누른 모든 게시글 ID를 가져옴
        List<Long> likedPostIds = likeRepository.findPostIdsByUserId(userId);

        // 사용자가 눌렀던 좋아요 삭제
        likeRepository.deleteByUserId(userId);

        // 각 게시글의 좋아요 수 감소
        for (Long postId : likedPostIds) {
            postRepository.decrementLikes(postId);
        }

        commentRepository.deleteByUserId(id);

        // 사용자가 작성한 게시글 ID 조회
        List<Long> postIds = postRepository.findIdsByUserId(id);

        // 해당 게시글과 관련된 모든 좋아요, 댓글 삭제
        if (!postIds.isEmpty()) {
            likeRepository.deleteByPost_IdIn(postIds);
            commentRepository.deleteByPost_IdIn(postIds);
        }
        // 게시글 삭제
        postRepository.deleteByUserId(id);

        userRepository.delete(target);
        return UserDto.createUserDto(target);
    }

    @Transactional(readOnly = true)
    public UserDto authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return UserDto.createUserDto(user);
        }
        return null; // 인증 실패 시 null 반환
    }

    @Transactional
    public UserDto updateProfileImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 프로필 업데이트 실패, 대상 사용자가 없습니다."));

        // 기존 이미지 삭제
        if (user.getProfileImageUrl() != null) {
            String oldImagePath = user.getProfileImageUrl();
            File oldFile = new File(oldImagePath);
            if (oldFile.exists()) {
                oldFile.delete(); // 기존 이미지 삭제
            }
        }
        // 새로운 이미지 설정
        user = user.toBuilder()
                .profileImageUrl(imageUrl)
                .build();

        User updatedUser = userRepository.save(user);

        return UserDto.createUserDto(updatedUser);
    }


    @Transactional
    public String saveImageToLocalFile(MultipartFile file, Long userId) throws IOException {

        File uploadFolder = new File(uploadDir);

        // Create folder if it doesn't exist
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        // Generate unique filename
        String fileName = "user_" + userId + "_" + file.getOriginalFilename();
        if (fileName.length() > 50) {
            fileName = fileName.substring(0, 50); // 최대 50자로 잘라냄
        }
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());

        return filePath.toString(); // Return local file path
    }

    @Transactional(readOnly = true)
    public String getProfileImagePath(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 이미지 불러오기 실패, 대상 사용자가 없습니다."));

        return user.getProfileImageUrl();
    }

    public User createUser(UserDto dto, PasswordEncoder passwordEncoder) {
        validateUserDto(dto);
        return new User(
                null,
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getNickname(),
                null, // 프로필 이미지는 별도로 저장
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    private void validateUserDto(UserDto dto){
        if (dto.getId() != null){ // dto에 id가 존재하면 안됨. 엔티티의 id는 db가 자동 생성함.
            throw new IllegalArgumentException("사용자 생성 실패, 사용자의 id가 없어야 합니다.");
        }
    }
}
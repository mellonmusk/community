package com.example.communityProject.service;

import com.example.communityProject.dto.UserDto;
import com.example.communityProject.entity.Like;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.*;
import com.example.communityProject.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${file.upload-dir}") // application.properties에서 설정한 경로
    private String uploadDir;

    public List<UserDto> getUserList() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserDto.createUserDto(user))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 조회 실패, 대상 사용자가 없습니다."));
        return UserDto.createUserDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        User user = User.createUser(dto, passwordEncoder);
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
            target.setPassword(passwordEncoder.encode(dto.getPassword()));
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

        likeRepository.deleteByUser_Id(id);
        commentRepository.deleteByUser_Id(id);

        // 사용자가 작성한 게시글 ID 조회
        List<Long> postIds = postRepository.findIdsByUser_Id(id);

        // 해당 게시글과 관련된 모든 좋아요, 댓글 삭제
        if (!postIds.isEmpty()) {
            likeRepository.deleteByPost_IdIn(postIds);
            commentRepository.deleteByPost_IdIn(postIds);
        }
        // 게시글 삭제
        postRepository.deleteByUser_Id(id);

        userRepository.delete(target);
        return UserDto.createUserDto(target);
    }

    public UserDto authenticateUser(String email, String password) {
        log.info("AUthentication started!");
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
        user.setProfileImageUrl(imageUrl);
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

    public String getProfileImagePath(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 이미지 불러오기 실패, 대상 사용자가 없습니다."));

        return user.getProfileImageUrl();
    }
}
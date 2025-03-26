package com.example.communityProject.service;

import com.example.communityProject.dto.UserDto;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.*;
import com.example.communityProject.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userService = new UserService(postRepository, commentRepository, likeRepository, userRepository, passwordEncoder, jwtUtil);

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setId(1L);

        userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("rawPassword");
    }

    @Test
    @DisplayName("사용자 리스트를 조회하면 UserDto 리스트가 반환된다")
    void getUserList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>(List.of(user)));
        List<UserDto> userList = userService.getUserList();
        assertEquals(1, userList.size());
    }

    @Test
    @DisplayName("사용자가 존재하면 해당 UserDto를 반환한다")
    void getUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto result = userService.getUser(1L);
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 조회하면 예외가 발생한다")
    void getUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.getUser(1L));
    }

    @Test
    @DisplayName("새 사용자를 생성하면 UserDto가 반환된다")
    void createUser() {
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);
        UserDto createdUser = userService.createUser(userDto);
        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
    }

    @Test
    @DisplayName("삭제 권한이 없는 사용자가 삭제를 시도하면 예외가 발생한다")
    void deleteUser_ShouldThrowException_WhenUserNotAuthorized() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(2L);
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(1L, "token"));
    }

    @Test
    @DisplayName("사용자 정보를 업데이트하면 UserDto가 반환된다")
    void updateUser() {
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto modifiedDto = new UserDto();
        modifiedDto.setId(1L);
        modifiedDto.setEmail("test@example.com");
        modifiedDto.setPassword("modifiedrawpassword");
        when(passwordEncoder.encode(any())).thenReturn("newlyencodedPassword");

        User modifiedUser = new User();
        modifiedUser.setId(1L);
        modifiedUser.setEmail(modifiedDto.getEmail());
        modifiedUser.setPassword("newlyencodedPassword");

        when(userRepository.save(any())).thenReturn(modifiedUser);
        UserDto updatedUser = userService.updateUser(1L, modifiedDto, "token");
        assertNotNull(updatedUser);
    }

    @Test
    @DisplayName("수정 권한이 없는 사용자가 수정을 시도하면 예외가 발생한다")
    void updateUser_ShouldThrowException_WhenUserNotAuthorized() {
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto modifiedDto = new UserDto();
        modifiedDto.setId(1L);
        modifiedDto.setEmail("test@example.com");
        modifiedDto.setPassword("modifiedrawpassword");

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, modifiedDto,"token"));
    }


    @Test
    @DisplayName("이메일로 사용자를 찾으면 UserDto를 반환한다")
    void authenticateUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        UserDto result = userService.authenticateUser("test@example.com", "rawPassword");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("이메일로 사용자를 찾을 수 없으면 UsernameNotFoundException을 반환한다")
    void authenticateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.authenticateUser("test@example.com", "rawPassword"));
    }

    @Test
    @DisplayName("프로필 이미지를 업데이트하면 변경된 URL을 반환한다")
    void updateProfileImage() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User updatedUser = new User();
        updatedUser.setProfileImageUrl("newImageUrl");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto updatedDto = userService.updateProfileImage(1L, "newImageUrl");
        assertNotNull(updatedDto);
        assertEquals("newImageUrl", updatedDto.getProfileImage());
    }

    @Test
    @DisplayName("이미지를 로컬 파일로 저장하면 파일 경로를 반환한다")
    void saveImageToLocalFile() throws IOException {
        // Mock MultipartFile 생성
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // 테스트용 업로드 폴더 설정
        String testUploadDir = "test-uploads/";
        Path testPath = Paths.get(testUploadDir);

        // 디렉토리 생성 (없으면)
        if (!Files.exists(testPath)) {
            Files.createDirectories(testPath);
        }

        // UserService의 uploadDir 값을 테스트 경로로 설정
        userService.uploadDir = testUploadDir;

        // saveImageToLocalFile 메서드 실행
        String filePath = userService.saveImageToLocalFile(file, 1L);

        // 파일이 올바르게 저장되었는지 확인
        assertNotNull(filePath);
        assertTrue(filePath.startsWith(testUploadDir));

        // 실제로 파일이 생성되었는지 확인
        File savedFile = new File(filePath);
        assertTrue(savedFile.exists());

        // 테스트가 끝난 후 파일 삭제
        savedFile.delete();
    }


    @Test
    @DisplayName("사용자 ID로 프로필 이미지 경로를 가져올 수 있다")
    void getProfileImagePath() {
        user.setProfileImageUrl("profileImagePath");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String imagePath = userService.getProfileImagePath(1L);

        assertEquals("profileImagePath", imagePath);
    }

}


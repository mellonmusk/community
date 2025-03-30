package com.example.communityProject.service;

import com.example.communityProject.dto.UserDto;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.*;
import com.example.communityProject.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private String token = "mock token";

    @BeforeEach
    void setUp() {
        userService = new UserService(postRepository, commentRepository, likeRepository, userRepository, passwordEncoder, jwtUtil);

        user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .id(1L)
                .build();

        userDto = UserDto.builder()
                .email("test@example.com")
                .password("rawPassword")
                .build();
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
    @DisplayName("사용자가 정상적으로 삭제된다")
    @Transactional
    void deleteUser_Success() {
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(likeRepository.findPostIdsByUserId(1L)).thenReturn(List.of(100L, 101L));
        when(postRepository.findIdsByUserId(1L)).thenReturn(List.of(200L, 201L));

        UserDto deletedUser = userService.deleteUser(1L, token);

        assertNotNull(deletedUser);
        assertEquals(1L, deletedUser.getId());
        verify(likeRepository).deleteByUserId(1L);
        verify(postRepository, times(2)).decrementLikes(anyLong());
        verify(commentRepository).deleteByUserId(1L);
        verify(likeRepository).deleteByPost_IdIn(anyList());
        verify(commentRepository).deleteByPost_IdIn(anyList());
        verify(postRepository).deleteByUserId(1L);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("삭제 시 대상 사용자가 존재하지 않으면 예외 발생")
    void deleteUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.deleteUser(1L, token)
        );

        assertEquals("사용자 삭제 실패, 대상 사용자가 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("삭제 권한이 없는 경우 예외 발생")
    void deleteUser_Forbidden() {
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUser(1L, token)
        );

        assertEquals("수정 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("사용자 정보를 업데이트하면 UserDto가 반환된다")
    void updateUser() {
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto modifiedDto = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .password("modifiedrawpassword")
                .build();

        when(passwordEncoder.encode(any())).thenReturn("newlyencodedPassword");

        User modifiedUser = User.builder()
                .email(modifiedDto.getEmail())
                .password("newlyencodedPassword")
                .id(1L)
                .build();

        when(userRepository.save(any())).thenReturn(modifiedUser);
        UserDto updatedUser = userService.updateUser(1L, modifiedDto, token);
        assertNotNull(updatedUser);
    }

    @Test
    @DisplayName("수정 권한이 없는 사용자가 수정을 시도하면 예외가 발생한다")
    void updateUser_ShouldThrowException_WhenUserNotAuthorized() {
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto modifiedDto = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .password("modifiedrawpassword")
                .build();

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
        User updatedUser = User.builder()
                .profileImageUrl("newImageUrl")
                .build();
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto updatedDto = userService.updateProfileImage(1L, "newImageUrl");
        assertNotNull(updatedDto);
        assertEquals("newImageUrl", updatedDto.getProfileImageUrl());
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
        String testUploadDir = "test-uploads/users/";
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
        user = User.builder()
                .profileImageUrl("profileImagePath")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String imagePath = userService.getProfileImagePath(1L);

        assertEquals("profileImagePath", imagePath);
    }

}


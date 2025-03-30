package com.example.communityProject.service;

import com.example.communityProject.entity.Image;
import com.example.communityProject.repository.ImageRepository;
import com.example.communityProject.repository.PostRepository;
import com.example.communityProject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    private ImageService imageService;

    private String uploadDir = "test-uploads/posts/";

    @BeforeEach
    void setUp() {
        imageService = new ImageService(imageRepository);

        // 테스트용 업로드 디렉토리 생성
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        imageService.uploadDir = uploadDir;
    }

    @Test
    @DisplayName("이미지를 로컬 파일로 저장하면 파일 경로를 반환한다")
    void saveImage() throws IOException {
        // Mock MultipartFile 생성
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        Image mockImage = new Image("test-image.jpg", uploadDir+"test-image.jpg");
        when(imageRepository.save(any(Image.class))).thenReturn(mockImage); // mockImage를 반환하도록 설정

        Image savedImage = imageService.saveImage(file);

        // 파일이 올바르게 저장되었는지 확인
        assertNotNull(savedImage);
    }


    @Test
    void deleteImage() {
        Long imageId = 1L;
        imageService.deleteImage(imageId);
        verify(imageRepository).deleteById(imageId);
    }

    @Test
    void getImageByPostId() {
        Long postId = 1L;
        Image image = Image.builder()
                .fileName("testImage.jpg")
                .build();
        when(imageRepository.findByPost_Id(postId)).thenReturn(List.of(image));

        List<Image> images = imageService.getImageByPostId(postId);

        assertNotNull(images);
        assertEquals(1, images.size());
        assertEquals("testImage.jpg", images.get(0).getFileName());
    }
}

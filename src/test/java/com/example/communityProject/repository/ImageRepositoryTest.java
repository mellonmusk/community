package com.example.communityProject.repository;

import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.Post;
import com.example.communityProject.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ImageRepositoryTest {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private Image testImage1;
    private Image testImage2;
    private Post testPost1;
    private Post testPost2;
    private User testUser1;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setEmail("test1@example.com");
        testUser1.setPassword("User@123");
        testUser1.setNickname("tester1");
        userRepository.save(testUser1);

        testPost1 = new Post();
        testPost1.setUser(testUser1);
        postRepository.save(testPost1);  // ⭐ Post 먼저 저장

        testPost2 = new Post();
        testPost2.setUser(testUser1);
        postRepository.save(testPost2);

        testImage1 = new Image();
        testImage1.setPost(testPost1); // ⭐ Post에 연결
        imageRepository.save(testImage1);

        testImage2 = new Image();
        testImage2.setPost(testPost2);
        imageRepository.save(testImage2);
    }

    @Test
    @DisplayName("게시글 ID로 이미지 삭제하기")
    void deleteByPost_Id() {
        imageRepository.deleteByPost_Id(testPost1.getId());
        List<Image> images = imageRepository.findByPost_Id(testPost1.getId());
        assertThat(images).isEmpty();
    }

    @Test
    @DisplayName("게시글 ID로 이미지 찾기")
    void findByPost_Id() {
        List<Image> images = imageRepository.findByPost_Id(testPost1.getId());
        assertThat(images).isNotEmpty();
        assertThat(images).contains(testImage1);
    }
}
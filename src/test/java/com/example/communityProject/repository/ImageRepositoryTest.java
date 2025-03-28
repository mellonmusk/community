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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
        testUser1 = userRepository.save(User.builder()
                .email("test1@example.com")
                .password("User@123")
                .nickname("tester1")
                .build());

        testPost1 = postRepository.save(Post.builder()
                .title("Post 1")
                .content("Content of Post 1")
                .user(testUser1)
                .build());

        testPost2 = postRepository.save(Post.builder()
                .title("Post 2")
                .content("Content of Post 2")
                .user(testUser1)
                .build());

        testImage1 = imageRepository.save(Image.builder()
                .post(testPost1)
                .build());

        testImage2 = imageRepository.save(Image.builder()
                .post(testPost2)
                .build());
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
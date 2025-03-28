package com.example.communityProject.repository;

import com.example.communityProject.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = userRepository.save(User.builder()
                        .email("test1@example.com")
                        .password("User@123")
                        .nickname("tester1")
                        .build());

        testUser2 = userRepository.save(User.builder()
                .email("test2@example.com")
                .password("User@234")
                .nickname("tester2")
                .build());
    }

    @Test
    @DisplayName("모든 사용자 조회하기")
    void findAll() {
        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
        List<User> expectedUsers = List.of(testUser1, testUser2);
        assertIterableEquals(expectedUsers, users, "The list of users should match the expected list.");
    }

    @Test
    @DisplayName("사용자 ID로 찾기")
    void findByIdTest() {
        Optional<User> expected = userRepository.findById(testUser1.getId());
        assertThat(expected).isPresent();
        assertThat(expected.get().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    void findByEmail() {
        Optional<User> expected = userRepository.findByEmail(testUser1.getEmail());
        assertNotNull(expected);
        assertEquals(expected.get().getNickname(), testUser1.getNickname());
    }
}
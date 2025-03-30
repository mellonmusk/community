package com.example.communityProject.service;

import com.example.communityProject.entity.User;
import com.example.communityProject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("사용자가 존재할 경우 UserDetails를 반환한다")
    void loadUserByUsername_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("1");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("사용자가 존재하지 않을 경우 예외를 발생시킨다")
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("1")
        );

        assertEquals("사용자를 찾을 수 없습니다: 1", exception.getMessage());
    }
}

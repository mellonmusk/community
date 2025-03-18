package com.example.communityProject.service;

import com.example.communityProject.dto.UserDto;
import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.ImageRepository;
import com.example.communityProject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageService imageService;

    public List<UserDto> getUserList() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserDto.createUserDto(user))
                .collect(Collectors.toList());
    }
    @Transactional
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("사용자 조회 실패, 대상 사용자가 없습니다."));
        return UserDto.createUserDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        User user = User.createUser(dto);
        User created= userRepository.save(user);
        return UserDto.createUserDto(created);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto dto) {
        User target = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("사용자 수정 실패, 대상 사용자가 없습니다."));
        target.patch(dto);
        User updated = userRepository.save(target);
        return UserDto.createUserDto(updated);
    }

    @Transactional
    public UserDto deleteUser(Long id) {
        User target = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("사용자 삭제 실패, 대상 사용자가 없습니다."));
        imageRepository.deleteByUser_Id(id);
        userRepository.delete(target);
        return UserDto.createUserDto(target);
    }

    @Transactional
    public UserDto updateUserProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 프로필 업데이트 실패, 대상 사용자가 없습니다."));

        // 새 이미지 저장
        Image newImage = imageService.saveProfileImage(file);

        // 기존 이미지 삭제
        if (user.getProfileImage() != null) {
            imageService.deleteImage(user.getProfileImage().getId());
        }

        // 새로운 이미지 설정
        user.setProfileImage(newImage);
        User updatedUser = userRepository.save(user);

        return UserDto.createUserDto(updatedUser);
    }
}
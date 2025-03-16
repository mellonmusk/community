package com.example.communityProject.service;

import com.example.communityProject.dto.PostForm;
import com.example.communityProject.dto.UserForm;
import com.example.communityProject.entity.User;
import com.example.communityProject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserForm> getUserList() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserForm.createUserDto(user))
                .collect(Collectors.toList());
    }

    public UserForm getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("사용자 조회 실패, 대상 사용자가 없습니다."));
        return UserForm.createUserDto(user);
    }

    @Transactional
    public UserForm createUser(UserForm dto) {
        User user = User.createUser(dto);
        User created= userRepository.save(user);
        return UserForm.createUserDto(created);
    }

    @Transactional
    public UserForm updateUser(Long id, UserForm dto) {
        User target = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("사용자 수정 실패, 대상 사용자가 없습니다."));
        target.patch(dto);
        User updated = userRepository.save(target);
        return UserForm.createUserDto(updated);
    }

    @Transactional
    public UserForm deleteUser(Long id) {
        User target = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("사용자 삭제 실패, 대상 사용자가 없습니다."));
        userRepository.delete(target);
        return UserForm.createUserDto(target);
    }
}

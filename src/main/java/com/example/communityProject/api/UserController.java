package com.example.communityProject.api;

import com.example.communityProject.dto.UserDto;
import com.example.communityProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    // 사용자 프로필 목록 조회
    @GetMapping("/api/users")
    public ResponseEntity<List<UserDto>> getUserList() {
        List<UserDto> userList = userService.getUserList();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    // 사용자 프로필 조회
    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto dto = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // 사용자 프로필 등록
    @PostMapping("/api/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
        UserDto createdDto = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }

    // 사용자 프로필 수정
    @PatchMapping("/api/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
        UserDto updatedDto = userService.updateUser(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    // 사용자 삭제
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id){
        UserDto deletedDto = userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody UserDto userDto) {
        UserDto authenticatedUser = userService.authenticateUser(userDto.getEmail(), userDto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(authenticatedUser);
    }
}

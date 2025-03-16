package com.example.communityProject.api;

import com.example.communityProject.dto.UserForm;
import com.example.communityProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserApiController {
    @Autowired
    private UserService userService;

    // 사용자 프로필 목록 조회
    @GetMapping("/api/users")
    public ResponseEntity<List<UserForm>> getUserList() {
        List<UserForm> userList = userService.getUserList();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    // 사용자 프로필 조회
    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserForm> getUser(@PathVariable Long id) {
        UserForm dto = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // 사용자 프로필 등록
    @PostMapping("/api/users")
    public ResponseEntity<UserForm> createUser(@RequestBody UserForm dto) {
        UserForm createdDto = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }

    // 사용자 프로필 수정
    @PatchMapping("/api/users/{id}")
    public ResponseEntity<UserForm> updateUser(@PathVariable Long id, @RequestBody UserForm dto) {
        UserForm updatedDto = userService.updateUser(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    // 사용자 삭제
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<UserForm> deleteUser(@PathVariable Long id){
        UserForm deletedDto = userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

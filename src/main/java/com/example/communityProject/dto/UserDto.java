package com.example.communityProject.dto;

import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    public static UserDto createUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                null,
                user.getNickname(),
                null
        );
    }
}

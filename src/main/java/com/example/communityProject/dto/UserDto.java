package com.example.communityProject.dto;

import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
@ToString
public class UserDto {
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String profileImageUrl;


    public static UserDto createUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                null,
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }
}

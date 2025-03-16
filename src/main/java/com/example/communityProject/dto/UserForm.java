package com.example.communityProject.dto;

import com.example.communityProject.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class UserForm {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    public static UserForm createUserDto(User user) {
        return new UserForm(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getProfileImage()
        );
    }
}

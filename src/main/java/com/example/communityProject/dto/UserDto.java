package com.example.communityProject.dto;

import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
@ToString
public class UserDto {
    private Long id;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "비밀번호는 8~20자이며, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    private String nickname;

    @NotBlank(message = "프로필 이미지 저장위치는 필수 입력 값입니다.")
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

package com.example.communityProject.entity;

import com.example.communityProject.dto.UserForm;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "MEMBER")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 id 자동 생성
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column(unique = true, length=10)
    private String nickname;

    private String profileImage =""; // 프로필 이미지 URL 저장

    public static User createUser(UserForm dto) {
        // 예외 발생
        if (dto.getId() != null){ // dto에 id가 존재하면 안됨. 엔티티의 id는 db가 자동 생성함.
            throw new IllegalArgumentException("사용자 생성 실패, 사용자의 id가 없어야 합니다.");
        }
        // 엔티티 생성 및 반환
        return new User(
                dto.getId(),
                dto.getEmail(),
                dto.getPassword(),
                dto.getNickname(),
                dto.getProfileImage()
        );
    }

    public void patch(UserForm dto) {
        // 예외 발생
        if(this.id != dto.getId())
            throw new IllegalArgumentException("사용자 수정 실패, 잘못된 id가 입력됐습니다.");
        // 객체 갱신
        if (dto.getEmail() != null) {
            this.email = dto.getEmail();
        }
        if (dto.getPassword() != null) {
            this.password = dto.getPassword();
        }
        if (dto.getNickname() != null) {
            this.nickname = dto.getNickname();
        }
        if (dto.getProfileImage() != null) {
            this.profileImage = dto.getProfileImage();
        }
    }
}

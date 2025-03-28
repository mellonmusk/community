package com.example.communityProject.entity;

import com.example.communityProject.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MEMBER")
@Getter
@Builder(toBuilder = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 id 자동 생성
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(length = 72)
    private String password;

    @Column(unique = true, length=10)
    private String nickname;

    private String profileImageUrl; // 프로필 이미지 URL 저장

    // Bidirectional one-to-many mapping for comments.
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 5)
    private List<Comment> comments = new ArrayList<>();

    // Bidirectional one-to-many mapping for likes.
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    // Bidirectional one-to-many mapping for posts.
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    public void patch(UserDto dto) {
        // 예외 발생
        if(this.id != dto.getId())
            throw new IllegalArgumentException("사용자 수정 실패, 잘못된 id가 입력됐습니다.");
        // 객체 갱신
        if (dto.getEmail() != null) {
            this.email = dto.getEmail();
        }
        if (dto.getNickname() != null) {
            this.nickname = dto.getNickname();
        }
        if(dto.getProfileImage() != null) {
            this.profileImageUrl = dto.getProfileImage();
        }
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}

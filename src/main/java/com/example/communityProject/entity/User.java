package com.example.communityProject.entity;

import com.example.communityProject.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "MEMBER")
@Getter
@Setter
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image profileImage; // 프로필 이미지 URL 저장

    // Bidirectional one-to-many mapping for comments.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // Bidirectional one-to-many mapping for likes.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    // Bidirectional one-to-many mapping for posts.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    public static User createUser(UserDto dto, PasswordEncoder passwordEncoder) {
        // 예외 발생
        if (dto.getId() != null){ // dto에 id가 존재하면 안됨. 엔티티의 id는 db가 자동 생성함.
            throw new IllegalArgumentException("사용자 생성 실패, 사용자의 id가 없어야 합니다.");
        }
        // 엔티티 생성 및 반환
        return new User(
                null,
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getNickname(),
                null, // 프로필 이미지는 별도로 저장
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

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
    }
}

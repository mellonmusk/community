package com.example.communityProject.entity;

import com.example.communityProject.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
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

    private String profileImageUrl;

    // Bidirectional one-to-many mapping for comments
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 5)
    private List<Comment> comments = new ArrayList<>();

    // Bidirectional one-to-many mapping for likes
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    // Bidirectional one-to-many mapping for posts
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
}
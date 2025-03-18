package com.example.communityProject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "profileImage")
    private User user;

    @OneToOne(mappedBy = "postImage")
    private Post post;

    private String fileName;  // 저장된 파일 이름
    private String filePath;  // 파일 경로

    public Image(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}

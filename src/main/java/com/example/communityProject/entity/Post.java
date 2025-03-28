package com.example.communityProject.entity;

import com.example.communityProject.dto.PostDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter
@Builder(toBuilder = true)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 26)
    private String title;

    @Lob
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User user;

    private Long likes;

    @Column
    @Builder.Default
    private Long views=0L;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void patch(PostDto dto) {
        // 예외 발생
        if(this.id != dto.getId())
            throw new IllegalArgumentException("게시글 수정 실패, 잘못된 id가 입력됐습니다.");
        // 객체 갱신
        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }
        if (dto.getContent() != null) {
            this.content = dto.getContent();
        }
        if (dto.getLikes() != null) {
            this.likes = dto.getLikes();
        }
        if (dto.getViews() != null) {
            this.views = dto.getViews();
        }
    }
}

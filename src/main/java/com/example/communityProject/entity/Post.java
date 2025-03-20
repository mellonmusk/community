package com.example.communityProject.entity;

import com.example.communityProject.dto.PostDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 26)
    private String title;

    @Lob
    private String content;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "image_id")
    private Image postImage; // 파일 디렉토리

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "author_id", nullable = false)
    private User user;

//    @Column
//    private Like likes;

    @Column
    private Long views=0L;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static Post createPost(PostDto dto, User user, LocalDateTime now) {
        if (dto.getId() != null){
            throw new IllegalArgumentException("게시글 생성 실패, 게시글의 id가 없어야 합니다.");
        }
        // 엔티티 생성 및 반환
        return new Post(
                null,
                dto.getTitle(),
                dto.getContent(),
                null,
                user,
//                dto.getLikes(),
                dto.getViews(),
                now
        );
    }

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
//        if (dto.getLikes() != null) {
//            this.likes = dto.getLikes();
//        }
        if (dto.getViews() != null) {
            this.views = dto.getViews();
        }
    }
}

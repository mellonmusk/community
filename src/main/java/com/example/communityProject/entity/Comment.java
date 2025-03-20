package com.example.communityProject.entity;

import com.example.communityProject.dto.CommentDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Comment entity와 Post entity를 다대일 관계로 설정
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "post_id", nullable = false) // Post entity의 기본 키(id)와 매핑
    private Post post; // 해당 댓글의 부모 게시글

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "author_id", nullable = false)
    private User user; // 댓글 작성자

    @Lob
    private String body; // 댓글 본문

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static Comment createComment(CommentDto dto, Post post, User user, LocalDateTime createdAt) {
        // 예외 발생
        if (dto.getId() != null){ // dto에 id가 존재하면 안됨. 엔티티의 id는 db가 자동 생성함.
            throw new IllegalArgumentException("댓글 생성 실패, 댓글의 id가 없어야 합니다.");
        }
        if (dto.getPostId() != post.getId()) { // json 데이터와 url요청 정보가 다르면 안됨.(dto에서 가져온 부모 게시글과 entity에서 가져온 부모 게시글의 id가 다르면 안됨.)
            throw new IllegalArgumentException("댓글 생성 실패, 게시글의 id가 잘못됐습니다.");
        }
        if (dto.getAuthorId() != user.getId()) {
            throw new IllegalArgumentException("댓글 생성 실패, 작성자의 id가 잘못됐습니다.");
        }
        // 엔티티 생성 및 반환
        return new Comment(
                dto.getId(),
                post,
                user,
                dto.getBody(),
                createdAt
        );
    }

    public void patch(CommentDto dto) {
        // 예외 발생
        if(this.id != dto.getId())
            throw new IllegalArgumentException("댓글 수정 실패, 잘못된 id가 입력됐습니다.");
        // 객체 갱신
        if(dto.getBody() != null)
            this.body=dto.getBody();
    }
}

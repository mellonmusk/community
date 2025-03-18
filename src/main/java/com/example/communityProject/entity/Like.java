package com.example.communityProject.entity;

import com.example.communityProject.dto.LikeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public static Like createLike(LikeDto dto, User user, Post post) {
        // 예외 발생
        if (dto.getId() != null){ // dto에 id가 존재하면 안됨. 엔티티의 id는 db가 자동 생성함.
            throw new IllegalArgumentException("좋아요 증가 실패, 좋아요의 id가 없어야 합니다.");
        }
        if (dto.getPostId() != post.getId()) { // json 데이터와 url요청 정보가 다르면 안됨.(dto에서 가져온 부모 게시글과 entity에서 가져온 부모 게시글의 id가 다르면 안됨.)
            throw new IllegalArgumentException("좋아요 증가 실패, 게시글의 id가 잘못됐습니다.");
        }
        if (dto.getUserId() != user.getId()) {
            throw new IllegalArgumentException("좋아요 증가 실패, 사용자의 id가 잘못됐습니다.");
        }
        return new Like(
                dto.getId(),
                post,
                user
        );
    }
}

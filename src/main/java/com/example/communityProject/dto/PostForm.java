package com.example.communityProject.dto;

import com.example.communityProject.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class PostForm {
    private Long id;
    private String title;
    private String content;
    private String image;     // 파일 디렉토리 (null 허용)
    private Long likes;    // 좋아요 수
    private Long authorId=1L;
    private Long views;
    private LocalDateTime createdAt;

    public static PostForm createPostDto(Post post) {
        return new PostForm(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getImage(),
                post.getLikes(),
                post.getUser().getId(),
                post.getViews(),
                post.getCreatedAt()
        );
    }

//
//    public Post toEntity() {
//        return new Post(id, title, content, authorId, views, createdAt);
//    }
}

package com.example.communityProject.dto;

import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.Post;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder(toBuilder = true)
@Data
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String image;
    private Long likes;    // 좋아요 수
    private Long authorId;
    private Long views;
    private LocalDateTime createdAt;

    public static PostDto createPostDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                null,
                post.getLikes(),
                post.getUser().getId(),
                post.getViews(),
                post.getCreatedAt()
        );
    }
}

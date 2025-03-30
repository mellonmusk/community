package com.example.communityProject.dto;

import com.example.communityProject.entity.Comment;
import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder(toBuilder = true)
public class PostDto {
    private Long id;

    private String title;

    private String content;

    private String image;

    private Long likes;    // 좋아요 수

    private Long authorId;


    @Setter
    private UserDto author;

    private Long views;

    private LocalDateTime createdAt;

    @Setter
    @Builder.Default
    private List<CommentDto> comments = null;


    public static PostDto createPostDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                null,
                post.getLikes(),
                post.getUser().getId(),
                UserDto.createUserDto(post.getUser()),
                post.getViews(),
                post.getCreatedAt(),
                null
        );
    }

}

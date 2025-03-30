package com.example.communityProject.dto;

import com.example.communityProject.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@ToString
public class CommentDto {
    private Long id;

    private Long postId;

    private Long authorId;

    private String body;

    private LocalDateTime createdAt;


    public static CommentDto createCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getBody(),
                comment.getCreatedAt()
        );
    }
}

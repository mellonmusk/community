package com.example.communityProject.dto;

import com.example.communityProject.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@ToString
public class CommentDto {
    private Long id;

    @NotNull(message = "게시글 ID는 필수 입력 값입니다.")
    private Long postId;

    @NotNull(message = "작성자 ID는 필수 입력 값입니다.")
    private Long authorId;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
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

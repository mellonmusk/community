package com.example.communityProject.dto;

import com.example.communityProject.entity.Comment;
import com.example.communityProject.entity.Image;
import com.example.communityProject.entity.Post;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 26, message = "제목은 최대 26자까지 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    private String image;

    @NotNull(message = "좋아요 수는 필수 입력 값입니다.")
    @Min(value = 0, message = "좋아요 수는 0 이상이어야 합니다.")
    private Long likes;

    @NotNull(message = "작성자 ID는 필수 입력 값입니다.")
    private Long authorId;

    @Setter
    private UserDto author;

    @NotNull(message = "조회수는 필수 입력 값입니다.")
    @Min(value = 0, message = "조회수는 0 이상이어야 합니다.")
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

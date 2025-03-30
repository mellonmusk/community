package com.example.communityProject.dto;

import com.example.communityProject.entity.Like;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class LikeDto {
    private Long id;

    @NotNull(message = "게시글 ID는 필수 입력 값입니다.")
    private Long postId;

    @NotNull(message = "작성자 ID는 필수 입력 값입니다.")
    private Long userId;


    public static LikeDto createLikeDto(Like like) {
        return new LikeDto(
                like.getId(),
                like.getUser().getId(),
                like.getPost().getId()
        );
    }
}

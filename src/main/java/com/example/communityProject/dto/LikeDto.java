package com.example.communityProject.dto;

import com.example.communityProject.entity.Like;
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

    private Long postId;

    private Long userId;


    public static LikeDto createLikeDto(Like like) {
        return new LikeDto(
                like.getId(),
                like.getUser().getId(),
                like.getPost().getId()
        );
    }
}

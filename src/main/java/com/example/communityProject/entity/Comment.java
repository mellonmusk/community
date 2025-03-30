package com.example.communityProject.entity;

import com.example.communityProject.dto.CommentDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)// Comment entity와 Post entity를 다대일 관계로 설정
    @JoinColumn(name = "post_id", nullable = false) // Post entity의 기본 키(id)와 매핑
    @JsonBackReference
    private Post post; // 해당 댓글의 부모 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User user; // 댓글 작성자

    @Lob
    private String body; // 댓글 본문

    @Column(updatable = false)
    private LocalDateTime createdAt;
}

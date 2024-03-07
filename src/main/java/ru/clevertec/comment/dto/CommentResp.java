package ru.clevertec.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResp implements Serializable {
    private Long id;
    private LocalDateTime time;
    private String text;
    private String username;
    private Long newsId;
}
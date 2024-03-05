package ru.clevertec.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.comment.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentResp toResponse(Comment comment);
    Comment toRequest(CommentReq commentReq);
}

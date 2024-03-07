package ru.clevertec.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.comment.dto.CommentReq;
import ru.clevertec.comment.dto.CommentResp;


import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentResp save(CommentReq commentReq,String userName, Long newsId);

    CommentResp findById(Long id);

    void delete(Long id);

    Page<CommentResp> findAll(int page, int pageSize);

    Page<CommentResp> search(String keyword, Pageable pageable);

    List<CommentResp> findByUsername(String username);

    List<CommentResp> findByNews_Id(Long id);

}

package ru.clevertec.comment.service.imp;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.comment.dao.CommentDao;
import ru.clevertec.comment.dto.CommentMapper;
import ru.clevertec.comment.dto.CommentReq;
import ru.clevertec.comment.dto.CommentResp;
import ru.clevertec.comment.entity.Comment;
import ru.clevertec.comment.exeption.EntityNotFoundException;
import ru.clevertec.comment.proxy.Cache;
import ru.clevertec.comment.service.CommentService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImp implements CommentService {
    private CommentMapper commentMapper;
    private CommentDao commentDao;

    @Override
    @Cache
    public CommentResp save(CommentReq commentReq, String userName, Long newsId) {
        try {
            Comment comment = commentMapper.toRequest(commentReq);
            comment.setUsername(userName);
            comment.setNewsId(newsId);
            comment.setTime(LocalDateTime.now());
            commentDao.save(comment);
            return commentMapper.toResponse(comment);
        } catch (RuntimeException e) {
            throw new EntityNotFoundException("an error occurred");
        }
    }

    @Override
    @Cache
    public CommentResp findById(Long id) {
        Optional<Comment> comment = commentDao.findById(id);
        if (comment.isPresent()) {
            return commentMapper.toResponse(comment.get());
        } else {
            throw new EntityNotFoundException("Comment not found with id " + id);
        }
    }

    @Override
    @Cache
    public void delete(Long id) {
        Optional<Comment> comment = commentDao.findById(id);
        if (comment.isPresent()) {
            commentDao.delete(comment.get());
        } else {
            throw new EntityNotFoundException("Comment not found with id " + id);
        }
    }

    @Override
    @Cache
    public Page<CommentResp> findAll(int page, int pageSize) {
        Page<CommentResp> respPage = commentDao.findAll(PageRequest.of(page, pageSize)).map(commentMapper::toResponse);
        if (respPage.isEmpty()) {
            throw new EntityNotFoundException("Comment not found");
        }
        return respPage;
    }

    @Override
    public Page<CommentResp> search(String keyword, Pageable pageable) {
        return commentDao.search(keyword, pageable).map(commentMapper::toResponse);
    }

    @Override
    public List<CommentResp> findByUsername(String username) {
        Optional<List<Comment>> optionalComments = commentDao.findByUsername(username);
        if (optionalComments.isPresent()) {
            List<Comment> commentList = optionalComments.get();
            return commentList
                    .stream()
                    .map(commentMapper::toResponse).collect(Collectors.toList());
        } else {
            throw new EntityNotFoundException("Comment not found");
        }
    }

    @Override
    public List<CommentResp> findByNews_Id(Long id) {
        List<Comment> commentList = commentDao.findByNewsId(id);
        return commentList.stream().map(commentMapper::toResponse).collect(Collectors.toList());
    }


}

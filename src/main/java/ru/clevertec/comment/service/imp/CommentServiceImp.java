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
    /**
     * Saves a new comment.
     *
     * @param commentReq the comment request object
     * @param userName the username of the user who created the comment
     * @param newsId the ID of the news article the comment is associated with
     * @return the saved comment response object
     * @throws EntityNotFoundException if an error occurred while saving the comment
     */
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
    /**
     * Finds a comment by its ID.
     *
     * @param id the ID of the comment
     * @return the found comment response object
     * @throws EntityNotFoundException if no comment was found with the given ID
     */
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
    /**
     * Deletes a comment by its ID.
     *
     * @param id the ID of the comment
     * @throws EntityNotFoundException if no comment was found with the given ID
     */
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
    /**
     * Finds all comments.
     *
     * @param page the page number
     * @param pageSize the size of the page
     * @return a page of found comment response objects
     * @throws EntityNotFoundException if no comments were found
     */
    @Override
    @Cache
    public Page<CommentResp> findAll(int page, int pageSize) {
        Page<CommentResp> respPage = commentDao.findAll(PageRequest.of(page, pageSize)).map(commentMapper::toResponse);
        if (respPage.isEmpty()) {
            throw new EntityNotFoundException("Comment not found");
        }
        return respPage;
    }
    /**
     * Searches for comments by a keyword.
     *
     * @param keyword the keyword to search for
     * @param pageable the pagination information
     * @return a page of found comment response objects
     */
    @Override
    public Page<CommentResp> search(String keyword, Pageable pageable) {
        return commentDao.search(keyword, pageable).map(commentMapper::toResponse);
    }
    /**
     * Finds comments by a username.
     *
     * @param username the username to search for
     * @return a list of found comment response objects
     * @throws EntityNotFoundException if no comments were found for the given username
     */
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

    /**
     * Finds comments by a news ID.
     *
     * @param id the ID of the news article
     * @return a list of found comment response objects
     */
    @Override
    public List<CommentResp> findByNews_Id(Long id) {
        List<Comment> commentList = commentDao.findByNewsId(id);
        return commentList.stream().map(commentMapper::toResponse).collect(Collectors.toList());
    }


}

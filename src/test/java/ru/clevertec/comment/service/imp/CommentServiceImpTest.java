package ru.clevertec.comment.service.imp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import ru.clevertec.comment.dao.CommentDao;
import ru.clevertec.comment.dto.CommentMapper;
import ru.clevertec.comment.dto.CommentReq;
import ru.clevertec.comment.dto.CommentResp;
import ru.clevertec.comment.entity.Comment;
import ru.clevertec.comment.service.CommentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentServiceImpTest {
    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentMapper commentMapper;

    @MockBean
    private CommentDao commentDao;

    @Test
    public void testSave() {
        CommentReq commentReq = new CommentReq();
        Comment comment = new Comment();
        CommentResp commentResp = new CommentResp();

        when(commentMapper.toRequest(commentReq)).thenReturn(comment);
        when(commentDao.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(commentResp);

        CommentResp result = commentService.save(commentReq, "username", 1L);

        assertEquals(commentResp, result);
    }

    @Test
    public void testFindById() {
        Comment comment = new Comment();
        CommentResp commentResp = new CommentResp();

        when(commentDao.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentMapper.toResponse(comment)).thenReturn(commentResp);

        CommentResp result = commentService.findById(1L);

        assertEquals(commentResp, result);
    }


    @Test
    public void testFindAll() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment());
        Page<Comment> page = new PageImpl<>(comments);

        when(commentDao.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CommentResp> result = commentService.findAll(0, 1);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testSearch() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment());
        Page<Comment> page = new PageImpl<>(comments);

        when(commentDao.search(anyString(), any(Pageable.class))).thenReturn(page);

        Page<CommentResp> result = commentService.search("keyword", PageRequest.of(0, 1));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testFindByUsername() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment());

        when(commentDao.findByUsername(anyString())).thenReturn(Optional.of(comments));

        List<CommentResp> result = commentService.findByUsername("username");

        assertEquals(1, result.size());
    }

    @Test
    public void testFindByNews_Id() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment());

        when(commentDao.findByNewsId(anyLong())).thenReturn(comments);

        List<CommentResp> result = commentService.findByNews_Id(1L);

        assertEquals(1, result.size());
    }
}
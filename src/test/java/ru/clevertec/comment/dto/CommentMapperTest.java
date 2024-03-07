package ru.clevertec.comment.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.clevertec.comment.entity.Comment;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {
    @InjectMocks
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testToResponse() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");

        CommentResp commentResp = commentMapper.toResponse(comment);

        assertThat(commentResp).isNotNull();
        assertThat(commentResp.getId()).isEqualTo(comment.getId());
        assertThat(commentResp.getText()).isEqualTo(comment.getText());
    }

    @Test
    public void testToRequest() {
        CommentReq commentReq = new CommentReq();
        commentReq.setText("Test comment");

        Comment comment = commentMapper.toRequest(commentReq);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo(commentReq.getText());
    }
}
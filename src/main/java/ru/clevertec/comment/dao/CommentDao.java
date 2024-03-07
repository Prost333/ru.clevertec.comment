package ru.clevertec.comment.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.clevertec.comment.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao extends JpaRepository<Comment, Long> {

    Page<Comment> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM comments c WHERE " +
            "to_tsvector('simple', c.text || ' ' || c.username) @@ plainto_tsquery('simple', :keyword)",
            countQuery = "SELECT count(*) FROM comments c WHERE " +
                    "to_tsvector('simple', c.text || ' ' || c.username) @@ plainto_tsquery('simple', :keyword)",
            nativeQuery = true)
    Page<Comment> search(@Param("keyword") String keyword, Pageable pageable);

    Optional<List<Comment>> findByUsername(String username);

    List<Comment> findByNewsId(Long newsId);

}

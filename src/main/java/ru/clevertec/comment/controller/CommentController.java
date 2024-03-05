package ru.clevertec.comment.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.comment.dto.CommentReq;
import ru.clevertec.comment.dto.CommentResp;
import ru.clevertec.comment.dto.ResponsePage;
import ru.clevertec.comment.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResp> save(@RequestBody CommentReq commentReq,@RequestParam Long newsId) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(commentService.save(commentReq,user,newsId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResp> findById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponsePage<CommentResp> findAll(@RequestParam(defaultValue = "0", name = "page") int page,
                                             @RequestParam (defaultValue = "0", name = "pageSize")int pageSize) {
        Page<CommentResp>commentResps=commentService.findAll(page, pageSize);
        return new ResponsePage<>(commentResps);
    }

    @GetMapping("/search")
    public ResponsePage<CommentResp> search(@RequestParam String keyword, Pageable pageable) {
        Page<CommentResp>commentResps=commentService.search(keyword, pageable);
        return new ResponsePage<>(commentResps);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<CommentResp>> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(commentService.findByUsername(username));
    }

    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<CommentResp>> findByNewsId(@PathVariable Long newsId) {
        return ResponseEntity.ok(commentService.findByNews_Id(newsId));
    }

}

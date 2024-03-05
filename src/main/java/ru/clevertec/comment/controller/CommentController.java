package ru.clevertec.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            method = "POST",
            summary = "Save comment",
            description = "Save comment",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentReq.class)
                    )
            ),
            responses = @ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentResp.class)
                    )
            )
    )
    @PostMapping
    public ResponseEntity<CommentResp> save(@RequestBody CommentReq commentReq, @RequestParam Long newsId) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(commentService.save(commentReq, user, newsId));
    }

    @Operation(
            method = "GET",
            summary = "Find comment by ID",
            description = "Find comment by ID",
            responses = @ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentResp.class)
                    )
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<CommentResp> findById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.findById(id));
    }

    @Operation(
            method = "DELETE",
            summary = "Delete comment",
            description = "Delete comment by ID",
            responses = @ApiResponse(
                    description = "Success",
                    responseCode = "204"
            )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(
            method = "GET",
            summary = "Find all comments",
            description = "Find all comments with pagination",
            responses = @ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponsePage.class)
                    )
            )
    )
    @GetMapping
    public ResponsePage<CommentResp> findAll(@RequestParam(defaultValue = "0", name = "page") int page,
                                             @RequestParam(defaultValue = "0", name = "pageSize") int pageSize) {
        Page<CommentResp> commentResps = commentService.findAll(page, pageSize);
        return new ResponsePage<>(commentResps);
    }

    @Operation(
            method = "GET",
            summary = "Search comments",
            description = "Search comments by keyword",
            responses = @ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponsePage.class)
                    )
            )
    )
    @GetMapping("/search")
    public ResponsePage<CommentResp> search(@RequestParam String keyword, Pageable pageable) {
        Page<CommentResp> commentResps = commentService.search(keyword, pageable);
        return new ResponsePage<>(commentResps);
    }

    @Operation(
            method = "GET",
            summary = "Find comments by username",
            description = "Find comments by username",
            responses = @ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = List.class)
                    )
            )
    )
    @GetMapping("/username/{username}")
    public ResponseEntity<List<CommentResp>> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(commentService.findByUsername(username));
    }

    @Operation(
            method = "GET",
            summary = "Find comments by news ID",
            description = "Find comments by news ID",
            responses = @ApiResponse(
                    description = "Success",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = List.class)
                    )
            )
    )
    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<CommentResp>> findByNewsId(@PathVariable Long newsId) {
        return ResponseEntity.ok(commentService.findByNews_Id(newsId));
    }

}

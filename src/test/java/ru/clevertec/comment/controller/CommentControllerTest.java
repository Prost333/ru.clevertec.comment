package ru.clevertec.comment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.clevertec.comment.dto.CommentReq;
import ru.clevertec.comment.dto.CommentResp;
import ru.clevertec.comment.dto.ResponsePage;
import ru.clevertec.comment.enums.Role;
import ru.clevertec.comment.multiFeign.User;
import ru.clevertec.comment.multiFeign.UserClient;
import ru.clevertec.comment.security.JwtService;
import ru.clevertec.comment.service.CommentService;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @MockBean
    private UserClient userClient;
    @Autowired
    private JwtService jwtService;

    private void authenticateAsAdmin() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.ADMIN.name()));
        User user = User.builder().role(Role.ADMIN).password("admin123").username("admin").build();
        UserDetails userDetails = user;
        String token = jwtService.generateToken(userDetails);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userClient.getDto()).thenReturn(token);
    }

    private void authenticateAsJournalist() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.JOURNALIST.name()));
        User user = User.builder().role(Role.JOURNALIST).password("journalist1").username("journalist1").build();
        UserDetails userDetails = user;
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testSave() throws Exception {
        authenticateAsAdmin();

        CommentReq commentReq = new CommentReq();
        commentReq.setText("Test comment");
        commentReq.setNewsId(1L);

        CommentResp commentResp = new CommentResp();
        commentResp.setId(1L);
        commentResp.setText(commentReq.getText());
        commentResp.setUsername("admin");
        commentResp.setNewsId(commentReq.getNewsId());

        when(commentService.save(any(CommentReq.class), anyString(), anyLong())).thenReturn(commentResp);

        MvcResult result = mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentReq))
                        .param("newsId", "1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        CommentResp responseComment = new ObjectMapper().readValue(responseContent, CommentResp.class);

        assertEquals(commentResp.getId(), responseComment.getId());
        assertEquals(commentResp.getText(), responseComment.getText());
        assertEquals(commentResp.getUsername(), responseComment.getUsername());
        assertEquals(commentResp.getNewsId(), responseComment.getNewsId());

        verify(commentService, times(1)).save(any(CommentReq.class), anyString(), anyLong());
    }
    @Test
    public void testFindById() throws Exception {
        authenticateAsAdmin();

        CommentResp commentResp = new CommentResp();
        when(commentService.findById(anyLong())).thenReturn(commentResp);

        mockMvc.perform(get("/comments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(commentResp)));

        verify(commentService, times(1)).findById(anyLong());
    }

    @Test
    public void testDelete() throws Exception {
        authenticateAsAdmin();

        mockMvc.perform(delete("/comments/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).delete(anyLong());
    }

    @Test
    public void testFindAll() throws Exception {
        authenticateAsAdmin();

        Page<CommentResp> commentResps = new PageImpl<>(new ArrayList<>());
        when(commentService.findAll(anyInt(), anyInt())).thenReturn(commentResps);

        mockMvc.perform(get("/comments")
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new ResponsePage<>(commentResps))));

        verify(commentService, times(1)).findAll(anyInt(), anyInt());
    }

    @Test
    public void testSearch() throws Exception {
        authenticateAsAdmin();

        Page<CommentResp> commentResps = new PageImpl<>(new ArrayList<>());
        when(commentService.search(anyString(), any(Pageable.class))).thenReturn(commentResps);

        mockMvc.perform(get("/comments/search")
                        .param("keyword", "test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new ResponsePage<>(commentResps))));

        verify(commentService, times(1)).search(anyString(), any(Pageable.class));
    }

    @Test
    public void testFindByUsername() throws Exception {
        authenticateAsAdmin();

        List<CommentResp> commentResps = new ArrayList<>();
        when(commentService.findByUsername(anyString())).thenReturn(commentResps);

        mockMvc.perform(get("/comments/username/{username}", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(commentResps)));

        verify(commentService, times(1)).findByUsername(anyString());
    }

    @Test
    public void testFindByNewsId() throws Exception {
        authenticateAsAdmin();

        List<CommentResp> commentResps = new ArrayList<>();
        when(commentService.findByNews_Id(anyLong())).thenReturn(commentResps);

        mockMvc.perform(get("/comments/news/{newsId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(commentResps)));

        verify(commentService, times(1)).findByNews_Id(anyLong());
    }
}
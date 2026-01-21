package com.cosmorum.controller;

import com.cosmorum.dto.AuthorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
class AuthorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private AuthorDTO createTestAuthor(String firstName, String lastName, String nationality) throws Exception {
        AuthorDTO author = new AuthorDTO(null, firstName, lastName, nationality);
        String response = mockMvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, AuthorDTO.class);
    }

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM astronomical_observations");
        jdbcTemplate.execute("DELETE FROM authors");
    }

    @Test
    void testCreateAuthor() throws Exception {
        AuthorDTO author = new AuthorDTO(null, "Carl", "Sagan", "American");

        mockMvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Carl"))
                .andExpect(jsonPath("$.lastName").value("Sagan"))
                .andExpect(jsonPath("$.nationality").value("American"));
    }

    @Test
    void testCreateAuthorWithInvalidData() throws Exception {
        AuthorDTO author = new AuthorDTO(null, "", "", "");
        mockMvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllAuthors() throws Exception {
        createTestAuthor("Test", "User", "TestNation");

        mockMvc.perform(get("/api/author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testUpdateAuthor() throws Exception {
        AuthorDTO created = createTestAuthor("Neil", "Armstrong", "USNation");
        created.setFirstName("Neil Alden");

        mockMvc.perform(put("/api/author/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Neil Alden"));
    }

    @Test
    void testDeleteAuthor() throws Exception {
        AuthorDTO created = createTestAuthor("Test", "Delete", "DeleteNation");

        mockMvc.perform(delete("/api/author/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/author/" + created.getId()))
                .andExpect(status().isNotFound());
    }
}

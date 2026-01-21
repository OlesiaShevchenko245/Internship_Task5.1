package com.cosmorum.controller;

import com.cosmorum.dto.AuthorDTO;
import com.cosmorum.dto.ObservationDTO;
import com.cosmorum.dto.ObservationFilterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
class ObservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testAuthorId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        jdbcTemplate.execute("DELETE FROM celestial_objects");
        jdbcTemplate.execute("DELETE FROM astronomical_observations");
        jdbcTemplate.execute("DELETE FROM authors");

        AuthorDTO author = createTestAuthor("John", "Doe", "USA");
        testAuthorId = author.getId();

        jdbcTemplate.execute("""
                    ALTER TABLE astronomical_observations
                    ALTER COLUMN name TYPE TEXT USING name::text
                """);
    }

    private AuthorDTO createTestAuthor(String firstName, String lastName, String nationality) throws Exception {
        AuthorDTO author = new AuthorDTO(null, firstName, lastName, nationality);
        String response = mockMvc.perform(post("/api/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, AuthorDTO.class);
    }

    private ObservationDTO createTestObservation(String name, String description) throws Exception {
        ObservationDTO observation = new ObservationDTO();
        observation.setName(name);
        observation.setDescription(description);
        observation.setObservationTime(LocalDateTime.of(2024, 1, 15, 20, 30));
        observation.setAuthorId(testAuthorId);
        observation.setCelestialObjects(Arrays.asList("Mars", "Jupiter"));

        String response = mockMvc.perform(post("/api/observation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(observation)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, ObservationDTO.class);
    }

    @Test
    void testCreateObservation() throws Exception {
        ObservationDTO created = createTestObservation("Test Observation", "Test description");

        mockMvc.perform(get("/api/observation/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("Test Observation"))
                .andExpect(jsonPath("$.author.id").value(testAuthorId.intValue()));
    }

    @Test
    void testCreateObservationWithInvalidAuthor() throws Exception {
        ObservationDTO observation = new ObservationDTO();
        observation.setName("Invalid Author Test");
        observation.setObservationTime(LocalDateTime.now());
        observation.setAuthorId(99999L);

        mockMvc.perform(post("/api/observation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(observation)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateObservation() throws Exception {
        ObservationDTO created = createTestObservation("Original Name", "Original description");
        created.setName("Updated Name");
        created.setDescription("Updated description");

        mockMvc.perform(put("/api/observation/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void testDeleteObservation() throws Exception {
        ObservationDTO created = createTestObservation("Delete Test", "Delete description");

        mockMvc.perform(delete("/api/observation/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/observation/" + created.getId()))
                .andExpect(status().isNotFound());
    }
}

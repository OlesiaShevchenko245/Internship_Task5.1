package com.cosmorum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class ObservationDTO {
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @NotNull(message = "Observation time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime observationTime;
    @NotNull(message = "Author ID is required")
    private Long authorId;
    private AuthorDTO author;
    private List<String> celestialObjects;

    public ObservationDTO() {}

    public ObservationDTO(Long id, String name, String description, LocalDateTime observationTime,
                          Long authorId, AuthorDTO author, List<String> celestialObjects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.observationTime = observationTime;
        this.authorId = authorId;
        this.author = author;
        this.celestialObjects = celestialObjects;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getObservationTime() { return observationTime; }
    public void setObservationTime(LocalDateTime observationTime) { this.observationTime = observationTime; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public AuthorDTO getAuthor() { return author; }
    public void setAuthor(AuthorDTO author) { this.author = author; }

    public List<String> getCelestialObjects() { return celestialObjects; }
    public void setCelestialObjects(List<String> celestialObjects) { this.celestialObjects = celestialObjects; }
}

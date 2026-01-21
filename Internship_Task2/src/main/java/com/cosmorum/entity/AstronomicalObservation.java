package com.cosmorum.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "astronomical_observations")
public class AstronomicalObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime observationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ElementCollection
    @CollectionTable(name = "celestial_objects", joinColumns = @JoinColumn(name = "observation_id"))
    @Column(name = "object_name")
    private List<String> celestialObjects;

    // Constructors
    public AstronomicalObservation() {}

    public AstronomicalObservation(Long id, String name, String description,
                                   LocalDateTime observationTime, Author author,
                                   List<String> celestialObjects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.observationTime = observationTime;
        this.author = author;
        this.celestialObjects = celestialObjects;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getObservationTime() {
        return observationTime;
    }

    public void setObservationTime(LocalDateTime observationTime) {
        this.observationTime = observationTime;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<String> getCelestialObjects() {
        return celestialObjects;
    }

    public void setCelestialObjects(List<String> celestialObjects) {
        this.celestialObjects = celestialObjects;
    }
}

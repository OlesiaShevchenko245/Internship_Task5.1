package com.cosmorum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ObservationFilterRequest {
    private Long authorId;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    private Integer page = 1;
    private Integer size = 20;

    public ObservationFilterRequest() {}

    public ObservationFilterRequest(Long authorId, String name, LocalDateTime startTime, Integer page, Integer size) {
        this.authorId = authorId;
        this.name = name;
        this.startTime = startTime;
        this.page = page;
        this.size = size;
    }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}

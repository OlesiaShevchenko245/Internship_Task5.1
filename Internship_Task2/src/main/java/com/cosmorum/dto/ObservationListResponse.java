package com.cosmorum.dto;

import java.util.List;

public class ObservationListResponse {
    private List<ObservationListDTO> list;
    private Integer totalPages;

    public ObservationListResponse() {}

    public ObservationListResponse(List<ObservationListDTO> list, Integer totalPages) {
        this.list = list;
        this.totalPages = totalPages;
    }

    public List<ObservationListDTO> getList() { return list; }
    public void setList(List<ObservationListDTO> list) { this.list = list; }

    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
}

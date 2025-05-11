package com.example.financeservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagedProjectResponse {
    private List<ProjectDTO> content;
    private int totalPages;
    private long totalElements;
    private boolean last;
    private boolean first;
    private int size;
    private int number;
    private int numberOfElements;
    private boolean empty;
}

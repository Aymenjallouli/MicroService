package com.example.financeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectDTO {
    private Long id;
    private String name;
    private String nomProjet;
    private String location;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double latitude;
    private Double longitude;
    private String images;
    private String description;
}

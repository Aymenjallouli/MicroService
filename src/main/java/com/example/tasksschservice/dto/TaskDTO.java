package com.example.tasksschservice.dto;

import com.example.tasksschservice.entities.Priority;
import com.example.tasksschservice.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Long assigneeId;
    private Long projectId;
    private List<Long> missionIds;
    private String imageUrl;
    
    // Informations du projet
    private ProjectDTO project;
}

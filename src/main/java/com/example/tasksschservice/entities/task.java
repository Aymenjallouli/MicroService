package com.example.tasksschservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Long assigneeId;
    private Long projectId; // Ajout de la référence au projet

    @OneToOne
    private Image image;

    @ElementCollection
    private List<Long> missionIds = new ArrayList<>();

    // Attributs pour stocker les informations du projet
    @Transient
    private String projectName;
    @Transient
    private String projectStatus;
}

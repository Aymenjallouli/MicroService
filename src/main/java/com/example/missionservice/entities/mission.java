package com.example.missionservice.entities;

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
public class mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long assigneeId;
    private String status;
    
    // Ajouter une liste pour stocker les IDs de tâches liées
    @ElementCollection
    private List<Long> taskIds = new ArrayList<>();
}

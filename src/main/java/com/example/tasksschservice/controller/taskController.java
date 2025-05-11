package com.example.tasksschservice.controller;

import com.example.tasksschservice.client.ProjectClient;
import com.example.tasksschservice.dto.ProjectDTO;
import com.example.tasksschservice.dto.TaskDTO;
import com.example.tasksschservice.entities.Image;
import com.example.tasksschservice.entities.Priority;
import com.example.tasksschservice.entities.Status;
import com.example.tasksschservice.entities.task;
import com.example.tasksschservice.model.mission;
import com.example.tasksschservice.repo.missionClient;
import com.example.tasksschservice.service.CloudinaryService;
import com.example.tasksschservice.service.ImageService;
import com.example.tasksschservice.service.taskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class taskController {
    private final taskService taskservice;
    private final CloudinaryService cloudinaryService;
    private final ImageService imageService;
    private final missionClient missionclient;
    private final ProjectClient projectClient;

    public taskController(taskService taskService, CloudinaryService cloudinaryService,
                          ImageService imageService, missionClient missionclient, ProjectClient projectClient) {
        this.taskservice = taskService;
        this.cloudinaryService = cloudinaryService;
        this.imageService = imageService;
        this.missionclient = missionclient;
        this.projectClient = projectClient;
    }

    @GetMapping
    public List<task> getAllTasks() {
        return taskservice.getAllTasks();
    }
    
    @GetMapping("/with-project-info")
    public List<TaskDTO> getAllTasksWithProjectInfo() {
        return taskservice.getAllTasksWithProjectInfo();
    }

    @GetMapping("/{id}")
    public ResponseEntity<task> getTaskById(@PathVariable Long id) {
        Optional<task> task = taskservice.getTaskById(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/with-project-info")
    public ResponseEntity<TaskDTO> getTaskWithProjectInfoById(@PathVariable Long id) {
        Optional<TaskDTO> taskDTO = taskservice.getTaskDTOById(id);
        return taskDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody task task) {
        try {
            // Vérifier si le projet existe si un projectId est fourni
            if (task.getProjectId() != null) {
                try {
                    ProjectDTO project = projectClient.getProjectById(task.getProjectId());
                    if (project == null) {
                        return ResponseEntity.badRequest().body("Le projet avec l'ID " + task.getProjectId() + " n'existe pas.");
                    }
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Erreur lors de la vérification du projet: " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok(taskservice.saveTask(task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de la tâche: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskservice.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(
            @PathVariable Long id,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        // Récupérer la tâche existante
        Optional<task> optionalTask = taskservice.getTaskById(id);
        // Vérifier si la tâche existe
        if (optionalTask.isPresent()) {
            task existingTask = optionalTask.get();
            // Si une nouvelle photo est fournie, upload sur Cloudinary
            if (photo != null && !photo.isEmpty()) {
                Map result = cloudinaryService.upload(photo);
                String photoUrl = (String) result.get("url");
                Image image = new Image();
                image.setName(photo.getOriginalFilename());
                image.setImageUrl(photoUrl);
                image.setImageId((String) result.get("public_id"));
                imageService.save(image);
                existingTask.setImage(image);
            }
            // Sauvegarde de la tâche mise à jour
            taskservice.updateTask(id, existingTask);
            return new ResponseEntity<>("Tâche mise à jour avec succès !", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Tâche non trouvée.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add-mission-to-task/{taskId}")
    public ResponseEntity<String> createMissionAndAssignToTask(
            @PathVariable Long taskId,
            @RequestBody mission newMission
    ) {
        try {
            // 1. Créer la mission via Feign client
            mission createdMission = missionclient.createMission(newMission);

            // 2. Récupérer la tâche existante
            Optional<task> optionalTask = taskservice.getTaskById(taskId);
            if (optionalTask.isEmpty()) {
                return new ResponseEntity<>("Tâche non trouvée", HttpStatus.NOT_FOUND);
            }

            task existingTask = optionalTask.get();

            // 3. Initialiser si null (sécurité)
            if (existingTask.getMissionIds() == null) {
                existingTask.setMissionIds(new ArrayList<>());
            }

            // 4. Ajouter la nouvelle mission à la liste
            existingTask.getMissionIds().add(createdMission.getId());

            // 5. Sauvegarder la tâche mise à jour
            taskservice.saveTask(existingTask);

            return new ResponseEntity<>("Nouvelle mission ajoutée à la tâche existante avec succès", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Erreur : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/missions-by-task/{taskId}")
    public ResponseEntity<?> getMissionsByTask(@PathVariable Long taskId) {
        try {
            Optional<task> optionalTask = taskservice.getTaskById(taskId);
            if (optionalTask.isEmpty()) {
                return new ResponseEntity<>("Tâche non trouvée", HttpStatus.NOT_FOUND);
            }
            task existingTask = optionalTask.get();
            List<Long> missionIds = existingTask.getMissionIds();
            if (missionIds == null || missionIds.isEmpty()) {
                return new ResponseEntity<>("Aucune mission liée à cette tâche", HttpStatus.OK);
            }
            List<mission> missions = new ArrayList<>();
            for (Long missionId : missionIds) {
                mission m = missionclient.getMissionById(missionId);
                if (m != null) {
                    missions.add(m);
                }
            }
            return new ResponseEntity<>(missions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/by-project/{projectId}")
    public ResponseEntity<?> getTasksByProjectId(@PathVariable Long projectId) {
        try {
            // Vérifier si le projet existe
            try {
                ProjectDTO project = projectClient.getProjectById(projectId);
                if (project == null) {
                    return ResponseEntity.badRequest().body("Le projet avec l'ID " + projectId + " n'existe pas.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erreur lors de la vérification du projet: " + e.getMessage());
            }
            
            List<task> tasks = taskservice.getTasksByProjectId(projectId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des tâches: " + e.getMessage());
        }
    }
}

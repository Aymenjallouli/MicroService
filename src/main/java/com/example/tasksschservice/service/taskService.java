package com.example.tasksschservice.service;

import com.example.tasksschservice.client.ProjectClient;
import com.example.tasksschservice.dto.ProjectDTO;
import com.example.tasksschservice.dto.TaskDTO;
import com.example.tasksschservice.entities.Image;
import com.example.tasksschservice.entities.Status;
import com.example.tasksschservice.entities.task;
import com.example.tasksschservice.repo.taskRepo;
import feign.FeignException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class taskService {
    private static final Logger log = LoggerFactory.getLogger(taskService.class);
    
    private final taskRepo taskRepository;
    private final ProjectClient projectClient;

    public taskService(taskRepo taskRepository, ProjectClient projectClient) {
        this.taskRepository = taskRepository;
        this.projectClient = projectClient;
    }

    public List<task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public List<TaskDTO> getAllTasksWithProjectInfo() {
        List<task> tasks = taskRepository.findAll();
        return tasks.stream()
            .map(this::convertToTaskDTO)
            .collect(Collectors.toList());
    }

    public Optional<task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public Optional<TaskDTO> getTaskDTOById(Long id) {
        return taskRepository.findById(id)
            .map(this::convertToTaskDTO);
    }

    public task saveTask(task task) {
        // Vérifier si le projet existe avant de sauvegarder la tâche
        if (task.getProjectId() != null) {
            try {
                ProjectDTO project = projectClient.getProjectById(task.getProjectId());
                if (project == null) {
                    throw new RuntimeException("Le projet avec l'ID " + task.getProjectId() + " n'existe pas");
                }
            } catch (FeignException e) {
                log.error("Erreur lors de la récupération du projet {}: {}", task.getProjectId(), e.getMessage());
                throw new RuntimeException("Erreur lors de la vérification du projet: " + e.getMessage());
            }
        }
        
        task.setImage(null);
        return taskRepository.save(task);
    }

    public task updateTaskImage(Long taskId, Image image) {
        return taskRepository.findById(taskId).map(task -> {
            task.setImage(image);
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public task updateTask(long id, task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setPriority(updatedTask.getPriority());
            task.setDueDate(updatedTask.getDueDate());
            task.setStartDate(updatedTask.getStartDate());
            task.setStatus(updatedTask.getStatus());
            task.setProjectId(updatedTask.getProjectId());
            
            // Vérifier si le projet existe avant de mettre à jour
            if (task.getProjectId() != null) {
                try {
                    ProjectDTO project = projectClient.getProjectById(task.getProjectId());
                    if (project == null) {
                        throw new RuntimeException("Le projet avec l'ID " + task.getProjectId() + " n'existe pas");
                    }
                } catch (FeignException e) {
                    log.error("Erreur lors de la récupération du projet {}: {}", task.getProjectId(), e.getMessage());
                    throw new RuntimeException("Erreur lors de la vérification du projet: " + e.getMessage());
                }
            }
            
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Scheduled(cron = "* * * * * *")
    public void updateTaskStatus() {
        try {
            taskRepository.updateTaskToInProgress(Status.INPROGRESS);
            taskRepository.updateTaskToDone(Status.DONE);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour des statuts des tâches : " + e.getMessage());
        }
    }
    
    public List<task> getTasksByProjectId(Long projectId) {
        // Vérifier si le projet existe
        try {
            ProjectDTO project = projectClient.getProjectById(projectId);
            if (project == null) {
                throw new RuntimeException("Le projet avec l'ID " + projectId + " n'existe pas");
            }
        } catch (FeignException e) {
            log.error("Erreur lors de la récupération du projet {}: {}", projectId, e.getMessage());
            throw new RuntimeException("Erreur lors de la vérification du projet: " + e.getMessage());
        }
        
        // Utiliser le repository pour trouver les tâches par projectId
        return taskRepository.findByProjectId(projectId);
    }
    
    private TaskDTO convertToTaskDTO(task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setStartDate(task.getStartDate());
        dto.setDueDate(task.getDueDate());
        dto.setAssigneeId(task.getAssigneeId());
        dto.setProjectId(task.getProjectId());
        dto.setMissionIds(task.getMissionIds());
        
        if (task.getImage() != null) {
            dto.setImageUrl(task.getImage().getImageUrl());
        }
        
        // Récupérer les informations du projet si un projectId est défini
        if (task.getProjectId() != null) {
            try {
                ProjectDTO project = projectClient.getProjectById(task.getProjectId());
                dto.setProject(project);
            } catch (Exception e) {
                log.warn("Impossible de récupérer les informations du projet {}: {}", task.getProjectId(), e.getMessage());
                // Continuer sans les informations du projet
            }
        }
        
        return dto;
    }
}

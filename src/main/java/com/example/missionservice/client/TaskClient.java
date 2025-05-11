package com.example.missionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "taskssch-service", url = "${task-service.url:}")
public interface TaskClient {

    @GetMapping("/tasks")
    List<Object> getAllTasks();
    
    @GetMapping("/tasks/{id}")
    Object getTaskById(@PathVariable("id") Long id);
    
    @GetMapping("/tasks/missions-by-task/{taskId}")
    List<Object> getMissionsByTask(@PathVariable("taskId") Long taskId);
    
    @PostMapping("/tasks/add-mission-to-task/{taskId}")
    String addMissionToTask(@PathVariable("taskId") Long taskId, @RequestBody Object mission);
}

package com.example.tasksschservice.client;

import com.example.tasksschservice.config.FeignClientConfig;
import com.example.tasksschservice.dto.ProjectDTO;
import com.example.tasksschservice.dto.PagedProjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PROJECT-SERVICE", url = "${project-service.url:}", configuration = FeignClientConfig.class)
public interface ProjectClient {
    
    @GetMapping("/api/projects")
    PagedProjectResponse getAllProjects();
    
    @GetMapping("/api/projects/{id}")
    ProjectDTO getProjectById(@PathVariable("id") Long id);
    
    @PostMapping("/api/projects")
    ProjectDTO createProject(@RequestBody ProjectDTO projectDTO);
    
    @PutMapping("/api/projects/{id}")
    ProjectDTO updateProject(@PathVariable("id") Long id, @RequestBody ProjectDTO projectDTO);
    
    @DeleteMapping("/api/projects/{id}")
    void deleteProject(@PathVariable("id") Long id);
}

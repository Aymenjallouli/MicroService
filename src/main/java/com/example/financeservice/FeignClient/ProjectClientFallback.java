package com.example.financeservice.FeignClient;

import com.example.financeservice.dto.PagedProjectResponse;
import com.example.financeservice.dto.ProjectDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProjectClientFallback implements ProjectClient {
    
    @Override
    public PagedProjectResponse getAllProjects() {
        System.out.println("Using fallback for getAllProjects");
        PagedProjectResponse response = new PagedProjectResponse();
        response.setContent(new ArrayList<>());
        response.setEmpty(true);
        response.setTotalElements(0);
        response.setTotalPages(0);
        return response;
    }

    @Override
    public ProjectDTO getProjectById(Long id) {
        System.out.println("Using fallback for getProjectById: " + id);
        ProjectDTO dto = new ProjectDTO();
        dto.setId(id);
        dto.setName("Project #" + id);
        dto.setDescription("Project details not available");
        return dto;
    }

    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        System.out.println("Using fallback for createProject");
        return projectDTO;
    }

    @Override
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        System.out.println("Using fallback for updateProject: " + id);
        return projectDTO;
    }

    @Override
    public void deleteProject(Long id) {
        System.out.println("Using fallback for deleteProject: " + id);
    }
}

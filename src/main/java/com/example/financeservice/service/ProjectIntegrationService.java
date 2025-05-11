package com.example.financeservice.service;

import com.example.financeservice.FeignClient.ProjectClient;
import com.example.financeservice.dto.ProjectDTO;
import com.example.financeservice.dto.PagedProjectResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(ProjectIntegrationService.class);

    @Autowired(required = false)
    private ProjectClient projectClient;    /**
     * Get project from remote service
     */
    public ProjectDTO getProjectById(Long id) {
        if (projectClient == null) {
            throw new RuntimeException("ProjectClient is not available - service discovery issue");
        }
        
        log.info("Fetching project with ID: {} from Project Service", id);
        try {
            ProjectDTO project = projectClient.getProjectById(id);
            
            if (project == null) {
                throw new RuntimeException("Project with ID " + id + " not found");
            }
            
            log.info("Successfully retrieved project: {} ({})", 
                project.getName() != null ? project.getName() : project.getNomProjet(),
                project.getId());
            return project;
        } catch (feign.FeignException.Unauthorized e) {
            log.error("Authentication failed when retrieving project: {} - {}", id, e.getMessage());
            throw new RuntimeException("Unauthorized access to Project Service. Please check your authentication token.", e);
        } catch (feign.FeignException.NotFound e) {
            log.error("Project not found: {}", id);
            throw new RuntimeException("Project with ID " + id + " not found in Project Service", e);
        } catch (feign.FeignException e) {
            log.error("Error from Project Service: {} - Status: {}", e.getMessage(), e.status());
            throw new RuntimeException("Error communicating with Project Service: HTTP Status " + e.status(), e);
        }
    }    /**
     * Get all projects from project microservice
     */
    public List<ProjectDTO> getAllProjects() {
        if (projectClient == null) {
            throw new RuntimeException("ProjectClient is not available - service discovery issue");
        }
        
        log.info("Fetching all projects from Project Service");
        try {
            PagedProjectResponse response = projectClient.getAllProjects();
            
            if (response == null || response.getContent() == null) {
                throw new RuntimeException("Failed to retrieve projects from project service");
            }
            
            log.info("Successfully retrieved {} projects", response.getContent().size());
            return response.getContent();
        } catch (feign.FeignException.Unauthorized e) {
            log.error("Authentication failed when retrieving projects: {}", e.getMessage());
            throw new RuntimeException("Unauthorized access to Project Service. Please check your authentication token.", e);
        } catch (feign.FeignException e) {
            log.error("Error from Project Service: {} - Status: {}", e.getMessage(), e.status());
            throw new RuntimeException("Error communicating with Project Service: HTTP Status " + e.status(), e);
        }
    }
}

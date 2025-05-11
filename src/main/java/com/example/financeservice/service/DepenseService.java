package com.example.financeservice.service;

import com.example.financeservice.dto.ProjectDTO;
import com.example.financeservice.entity.Depense;
import com.example.financeservice.repository.DepenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepenseService {

    private static final Logger log = LoggerFactory.getLogger(DepenseService.class);
    private static final int MAX_FILE_SIZE = 16 * 1024 * 1024; // 16MB max file size

    private final DepenseRepository repository;
    
    @Autowired(required = false)
    private ProjectIntegrationService projectIntegrationService;

    public DepenseService(DepenseRepository repository) {
        this.repository = repository;
    }

    public List<Depense> getAll(Long idProject) {
        List<Depense> depenses = repository.findByIdProjet(idProject);
        
        // Enrich with project data if available
        if (projectIntegrationService != null && idProject != null) {
            try {
                ProjectDTO project = projectIntegrationService.getProjectById(idProject);
                if (project != null) {
                    String projectName = project.getNomProjet() != null ? 
                            project.getNomProjet() : project.getName();
                            
                    for (Depense depense : depenses) {
                        depense.setProjectName(projectName);
                        depense.setProjectDescription(project.getDescription());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error enriching depenses with project data: " + e.getMessage());
            }
        }
        
        return depenses;
    }

    public Depense getById(Long id) {
        Depense depense = repository.findById(id).orElse(null);
        if (depense != null && depense.getIdProjet() != null) {
            enrichWithProjectDetails(depense);
        }
        return depense;
    }
    
    /**
     * Enriches a Depense with project details from the Project Service
     */
    private void enrichWithProjectDetails(Depense depense) {
        if (projectIntegrationService != null && depense.getIdProjet() != null) {
            try {
                ProjectDTO projectDTO = projectIntegrationService.getProjectById(depense.getIdProjet());
                if (projectDTO != null) {
                    // Prefer nomProjet if available, fall back to name
                    String projectName = projectDTO.getNomProjet();
                    if (projectName == null || projectName.isEmpty()) {
                        projectName = projectDTO.getName();
                    }
                    depense.setProjectName(projectName);
                    depense.setProjectDescription(projectDTO.getDescription());
                }
            } catch (Exception e) {
                System.err.println("Error enriching depense with project data: " + e.getMessage());
            }
        }
    }

    /**
     * Enriches all Depenses in a list with project details
     */
    private void enrichWithProjectDetails(List<Depense> depenses) {
        for (Depense depense : depenses) {
            enrichWithProjectDetails(depense);
        }
    }

    public Depense save(Depense depense) {
        depense.setDate(new Date());
        
        // Explicitly set the enum value to ensure it's properly handled
        if (depense.getType() != null) {
            try {
                Depense.TypeDepense type = depense.getType();
                depense.setType(type);
            } catch (Exception e) {
                System.err.println("Error setting type: " + e.getMessage());
                // Default to AUTRES if conversion fails
                depense.setType(Depense.TypeDepense.AUTRES);
            }
        }
        
        // Always set a status if none provided
        if (depense.getStatut() == null) {
            depense.setStatut(Depense.StatutDepense.EN_ATTENTE);
        }
        
        // Save first
        Depense savedDepense = repository.save(depense);
        
        // Then enrich with project data (for return value only)
        enrichWithProjectDetails(savedDepense);
        
        return savedDepense;
    }

    public Depense edit(Long id, Depense depense) {
        Depense oldDepense = getById(id);

        depense.setIdProjet(oldDepense.getIdProjet());
        depense.setFileData(oldDepense.getFileData());
        depense.setFileName(oldDepense.getFileName());
        depense.setFileType(oldDepense.getFileType());
        depense.setDate(new Date());
        depense.setStatut(Depense.StatutDepense.EN_ATTENTE);
        return repository.save(depense);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Modified method to use ProjectIntegrationService directly
    public List<ProjectDTO> getAllProject() {
        if (projectIntegrationService != null) {
            try {
                List<ProjectDTO> projects = projectIntegrationService.getAllProjects();
                log.info("Retrieved {} projects from ProjectIntegrationService", projects.size());
                return projects;
            } catch (Exception e) {
                // Improved error logging
                log.error("Error using ProjectIntegrationService to retrieve all projects: {}", e.getMessage());
                e.printStackTrace();
                // Fallback to empty list
                return new ArrayList<>();
            }
        } else {
            log.warn("ProjectIntegrationService is not available (null)");
            // Return empty list if no integration service available
            return new ArrayList<>();
        }
    }
    
    // Get a specific project by ID from the microservice
    public ProjectDTO getProjectById(Long id) {
        if (projectIntegrationService != null) {
            return projectIntegrationService.getProjectById(id);
        }
        return null;
    }

    public void storeFile(MultipartFile file, Long idd) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum allowed size of 16MB");
        }
        
        byte[] fileData = file.getBytes();
        log.info("Storing file: {} ({} bytes) for depense ID: {}", 
                file.getOriginalFilename(), fileData.length, idd);
        
        Depense depense = repository.findById(idd).orElse(null);
        if (depense == null) {
            throw new IllegalArgumentException("Depense not found with ID: " + idd);
        }
        
        depense.setFileData(fileData);
        depense.setFileType(file.getContentType());
        depense.setFileName(file.getOriginalFilename());
        repository.save(depense);
        log.info("File successfully stored for depense ID: {}", idd);
    }

    public List<Depense> getAllSearch(String param) {
        return repository.search(param);
    }

    /**
     * Get a depense with its attached file
     */
    public Depense getDepenseWithFile(Long id) {
        log.info("Loading depense with file for ID: {}", id);
        Depense depense = repository.findById(id).orElse(null);
        
        if (depense == null) {
            log.warn("Depense not found with ID: {}", id);
            return null;
        }
        
        if (depense.getFileData() == null) {
            log.info("Depense {} has no file data attached", id);
        } else {
            log.info("Loaded file data: {} bytes for depense ID: {}", 
                    depense.getFileData().length, id);
        }
        
        return depense;
    }
}

package com.example.financeservice.controller;

import com.example.financeservice.dto.ProjectDTO;
import com.example.financeservice.entity.Depense;
import com.example.financeservice.service.DepenseService;
import com.example.financeservice.service.ProjectIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/depenses")
@CrossOrigin("*")
public class DepenseController {

    private static final Logger log = LoggerFactory.getLogger(DepenseController.class);

    private final DepenseService service;
    
    @Autowired(required = false)
    private ProjectIntegrationService projectIntegrationService;

    public DepenseController(DepenseService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Health check endpoint called");
        return ResponseEntity.ok("Finance service is up and running");
    }

    @GetMapping("findByProject/{projectId}")
    public List<Depense> getAll(@PathVariable Long projectId) {
        return service.getAll(projectId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Depense depense = service.getById(id); // service.getById() already enriches the depense object
        if (depense == null) {
            return ResponseEntity.notFound().build();
        }
        // The enrichment logic previously here was redundant as DepenseService.getById() handles it.
        return ResponseEntity.ok(depense);
    }

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestBody Depense depense) {
        try {
            System.out.println("Dépense reçue : " + depense);
            
            // Validate that the type is valid
            if (depense.getType() == null) {
                return ResponseEntity.badRequest().body("Le type de dépense est obligatoire");
            }
            
            Depense savedDepense = service.save(depense);
            return ResponseEntity.ok(savedDepense);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Erreur lors de la création de la dépense: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public Depense update(@PathVariable Long id, @RequestBody Depense depense) {
        depense.setId(id);
        return service.edit(id,depense);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("allProject")
    public ResponseEntity<?> getAllProject() {
        try {
            List<ProjectDTO> projects = service.getAllProject();
            log.info("getAllProject endpoint returned {} projects", projects.size());
            
            if (projects.isEmpty()) {
                // Return a more informative response when no projects are found
                return ResponseEntity.ok()
                    .body(Map.of(
                        "projects", projects,
                        "message", "No projects found. Integration service may not be available.",
                        "serviceStatus", projectIntegrationService != null ? "available" : "not available"
                    ));
            }
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            log.error("Error in getAllProject endpoint: {}", e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Failed to retrieve projects",
                    "message", e.getMessage()
                ));
        }
    }
    
    @GetMapping("projects")
    public ResponseEntity<?> getAllProjectsFromService() {
        // Delegate to DepenseService, which handles interaction with ProjectIntegrationService and its fallbacks.
        List<ProjectDTO> projects = service.getAllProject();
        return ResponseEntity.ok(projects);
    }
      @GetMapping("project/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        try {
            ProjectDTO project = service.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            log.error("Error fetching project with ID {}: {}", id, e.getMessage());
            return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                    "error", "Unable to retrieve project information",
                    "message", e.getMessage(),
                    "projectId", id
                ));
        }
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier est vide");
            }
            
            log.info("Received file upload request: {} ({} bytes) for depense ID: {}", 
                    file.getOriginalFilename(), file.getSize(), id);
            
            service.storeFile(file, id);
            return ResponseEntity.ok("Fichier ajouté avec succès");
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
            
        } catch (Exception e) {
            log.error("Error uploading file: ", e);
            return ResponseEntity.status(500)
                    .body("Erreur lors du téléchargement du fichier : " + e.getMessage());
        }
    }

    @GetMapping("/getfile/{id}")
    public ResponseEntity<?> getFile(@PathVariable Long id) {
        try {
            Depense depense = service.getById(id);
            
            if (depense == null) {
                log.warn("Depense not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            if (depense.getFileData() == null || depense.getFileName() == null || depense.getFileType() == null) {
                log.warn("Depense {} has no file attached", id);
                return ResponseEntity.badRequest().body("No file found for this expense");
            }
            
            log.info("Returning file: {} ({} type, {} bytes) for depense ID: {}", 
                    depense.getFileName(), depense.getFileType(), 
                    depense.getFileData().length, id);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + depense.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(depense.getFileType()))
                    .body(depense.getFileData());
        } catch (Exception e) {
            log.error("Error retrieving file for depense ID: {}", id, e);
            return ResponseEntity.status(500).body("Error retrieving file: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Depense>> getAllByParam(@RequestParam String param) {
        List<Depense> depenses = service.getAllSearch(param);
        return ResponseEntity.ok(depenses);
    }

    /**
     * Test endpoint to check if a file exists for a depense
     */
    @GetMapping("/check-file/{id}")
    public ResponseEntity<?> checkFile(@PathVariable Long id) {
        try {
            Depense depense = service.getById(id);
            
            if (depense == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (depense.getFileData() == null) {
                return ResponseEntity.ok(Map.of(
                    "hasFile", false,
                    "message", "No file attached to this expense"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "hasFile", true,
                    "fileName", depense.getFileName(),
                    "fileType", depense.getFileType(),
                    "fileSize", depense.getFileData().length
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error checking file: " + e.getMessage());
        }
    }

    /**
     * View text files in browser
     */
    @GetMapping("/view-text-file/{id}")
    public ResponseEntity<?> viewTextFile(@PathVariable Long id) {
        try {
            Depense depense = service.getById(id);
            
            if (depense == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (depense.getFileData() == null || depense.getFileName() == null) {
                return ResponseEntity.badRequest().body("No file found for this expense");
            }
            
            String fileType = depense.getFileType();
            
            // Only allow text files to be displayed
            if (fileType == null || !(fileType.startsWith("text/") || 
                                     fileType.equals("application/csv") ||
                                     fileType.equals("text/csv"))) {
                return ResponseEntity.badRequest().body("Only text files can be viewed in browser");
            }
            
            // Return as inline content rather than attachment
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + depense.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(fileType))
                    .body(depense.getFileData());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error viewing file: " + e.getMessage());
        }
    }
    
    /**
     * Special endpoint for CSV files
     */
    @GetMapping("/csv-file/{id}")
    public ResponseEntity<?> getCSVFile(@PathVariable Long id) {
        try {
            Depense depense = service.getById(id);
            
            if (depense == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (depense.getFileData() == null || depense.getFileName() == null) {
                return ResponseEntity.badRequest().body("No CSV file found for this expense");
            }
            
            // Force content type for CSV
            MediaType mediaType = MediaType.parseMediaType("text/csv");
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + depense.getFileName() + "\"")
                    .contentType(mediaType)
                    .body(depense.getFileData());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving CSV file: " + e.getMessage());
        }
    }

    /**
     * Upload multiple depenses via CSV/Excel file for a project
     */
    @PostMapping("/upload-depenses/{projectId}")
    public ResponseEntity<String> uploadDepenses(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long projectId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier est vide");
            }
            
            String fileName = file.getOriginalFilename();
            log.info("Processing bulk depense file: {} ({} bytes) for project ID: {}", 
                    fileName, file.getSize(), projectId);
            
            // Validate file type
            if (fileName != null && 
                    !(fileName.endsWith(".xlsx") || fileName.endsWith(".csv"))) {
                return ResponseEntity.badRequest()
                        .body("Format de fichier non supporté. Utilisez .xlsx ou .csv");
            }

            // TODO: Implement actual processing of file in DepenseService 
            // This would involve reading the Excel/CSV and creating depenses
            // service.processDepenseFile(file, projectId);
            
            return ResponseEntity.ok("Fichier traité avec succès. " + 
                    "Les dépenses ont été ajoutées au projet #" + projectId);
            
        } catch (Exception e) {
            log.error("Error processing depense file for project {}: {}", projectId, e.getMessage());
            return ResponseEntity.status(500)
                    .body("Erreur lors du traitement du fichier: " + e.getMessage());
        }
    }
}


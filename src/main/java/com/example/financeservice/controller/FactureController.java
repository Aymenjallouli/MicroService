package com.example.financeservice.controller;

import com.example.financeservice.entity.Facture;
import com.example.financeservice.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin("*")
public class FactureController {

    private static final Logger log = LoggerFactory.getLogger(FactureController.class);

    private final FactureService factureService;

    @Autowired
    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    /**
     * Get all invoices (factures) with project data
     */
    @GetMapping("/with-projects")
    public ResponseEntity<List<Facture>> getAllFacturesWithProjects() {
        List<Facture> factures = factureService.getFacturesWithProjects();
        return ResponseEntity.ok(factures);
    }

    /**
     * Create a new invoice (facture) associated with a project
     */
    @PostMapping("/project/{projectId}")
    public ResponseEntity<?> createWithProject(
            @PathVariable Long projectId,
            @RequestBody Facture facture) {
        try {
            Facture savedFacture = factureService.createFactureForProject(facture, projectId);
            return ResponseEntity.ok(savedFacture);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating facture: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Facture> getAll() {
        return factureService.getAll();
    }

    @GetMapping("/{id}")
    public Facture getById(@PathVariable Long id) {
        return factureService.getById(id);
    }

    @PostMapping
    public Facture create(@RequestBody Facture facture) {
        return factureService.save(facture);
    }

    @PutMapping("/{id}")
    public Facture update(@PathVariable Long id, @RequestBody Facture facture) {
        facture.setId(id);
        return factureService.save(facture);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        factureService.delete(id);
    }

    /**
     * Upload and process a file to generate multiple invoices
     */
    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadInvoiceFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier est vide");
            }

            log.info("Processing invoice file: {} ({})", file.getOriginalFilename(), file.getSize());

            List<Facture> createdInvoices = factureService.processInvoiceFile(file);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Fichier traité avec succès");
            response.put("invoiceCount", createdInvoices.size());
            response.put("invoices", createdInvoices);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing invoice file", e);
            return ResponseEntity.status(500)
                    .body("Erreur lors du traitement du fichier: " + e.getMessage());
        }
    }

    /**
     * Download a CSV template for invoice import
     */
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            byte[] templateBytes = factureService.generateInvoiceTemplate();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"factures-template.csv\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(templateBytes);
        } catch (Exception e) {
            log.error("Error generating template", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Generate invoice from an uploaded expense document
     */
    @PostMapping("/generate-from-depense/{depenseId}")
    public ResponseEntity<?> generateFromDepense(@PathVariable Long depenseId) {
        try {
            Facture generatedFacture = factureService.generateFactureFromDepense(depenseId);
            return ResponseEntity.ok(generatedFacture);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating invoice from expense", e);
            return ResponseEntity.status(500)
                    .body("Erreur lors de la génération de la facture: " + e.getMessage());
        }
    }
}

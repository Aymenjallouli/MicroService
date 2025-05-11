package com.example.financeservice.service;

import com.example.financeservice.dto.ProjectDTO;
import com.example.financeservice.entity.Depense;  // Add this import
import com.example.financeservice.entity.Facture;
import com.example.financeservice.repository.FactureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FactureService {

    private static final Logger log = LoggerFactory.getLogger(FactureService.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final FactureRepository factureRepository;
    private final ProjectIntegrationService projectIntegrationService;

    @Autowired
    public FactureService(
            FactureRepository factureRepository,
            ProjectIntegrationService projectIntegrationService) {
        this.factureRepository = factureRepository;
        this.projectIntegrationService = projectIntegrationService;
    }

    public List<Facture> getFacturesWithProjects() {
        List<Facture> factures = factureRepository.findAll();
        
        if (projectIntegrationService != null) {
            // Enrich with project data where possible
            for (Facture facture : factures) {
                if (facture.getProjectId() != null) {
                    enrichFactureWithProject(facture, facture.getProjectId());
                }
            }
        }
        
        return factures;
    }

    @Transactional
    public Facture createFactureForProject(Facture facture, Long projectId) {
        // Set the projectId directly - no foreign key relationship
        facture.setProjectId(projectId);
        
        // Enrich with project data for display purposes
        if (projectIntegrationService != null) {
            enrichFactureWithProject(facture, projectId);
        }
        
        return factureRepository.save(facture);
    }
    
    private void enrichFactureWithProject(Facture facture, Long projectId) {
        try {
            ProjectDTO projectDTO = projectIntegrationService.getProjectById(projectId);
            if (projectDTO != null) {
                // Prefer nomProjet if available, fall back to name
                String projectName = projectDTO.getNomProjet();
                if (projectName == null || projectName.isEmpty()) {
                    projectName = projectDTO.getName();
                }
                facture.setProjectName(projectName);
                facture.setProjectDescription(projectDTO.getDescription());
            }
        } catch (Exception e) {
            System.err.println("Error enriching facture with project data: " + e.getMessage());
        }
    }

    public List<Facture> getAll() {
        return factureRepository.findAll();
    }

    public Facture getById(Long id) {
        return factureRepository.findById(id).orElse(null);
    }

    public Facture save(Facture facture) {
        return factureRepository.save(facture);
    }

    public void delete(Long id) {
        factureRepository.deleteById(id);
    }

    /**
     * Process an invoice CSV file to generate invoices
     * CSV format: numero,montant,statut,description,projectId,dateFacture
     */
    @Transactional
    public List<Facture> processInvoiceFile(MultipartFile file) throws IOException {
        List<Facture> createdInvoices = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Skip header row
            String line = reader.readLine();
            
            // Process each line
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length < 6) {
                    log.warn("Skipping invalid line: {}", line);
                    continue;
                }
                
                try {
                    Facture facture = new Facture();
                    facture.setNumero(values[0].trim());
                    facture.setMontant(Double.parseDouble(values[1].trim()));
                    facture.setStatut(values[2].trim());
                    facture.setDescription(values[3].trim());
                    facture.setProjectId(Long.parseLong(values[4].trim()));
                    
                    // Parse date
                    try {
                        facture.setDateFacture(dateFormat.parse(values[5].trim()));
                    } catch (ParseException e) {
                        facture.setDateFacture(new Date());
                        log.warn("Invalid date format for invoice {}, using current date", facture.getNumero());
                    }
                    
                    // Enrich with project data
                    enrichFactureWithProject(facture, facture.getProjectId());
                    
                    // Save the invoice
                    Facture savedFacture = factureRepository.save(facture);
                    createdInvoices.add(savedFacture);
                    
                    log.info("Created invoice: {} for project {}", savedFacture.getNumero(), savedFacture.getProjectId());
                } catch (Exception e) {
                    log.error("Error processing invoice line: {}", line, e);
                }
            }
        }
        
        return createdInvoices;
    }
    
    /**
     * Generate a sample CSV template for invoice import
     */
    public byte[] generateInvoiceTemplate() {
        StringBuilder template = new StringBuilder();
        
        // Header
        template.append("numero,montant,statut,description,projectId,dateFacture\n");
        
        // Sample data
        template.append("FACT-2025-001,15000.00,EMISE,\"Facturation phase initiale\",1,2025-05-15\n");
        template.append("FACT-2025-002,8500.00,EMISE,\"Services de consultation\",1,2025-05-16\n");
        
        // Help info
        template.append("\n# Statut disponibles: EMISE, PAYEE, ANNULEE\n");
        template.append("# Format de date: YYYY-MM-DD\n");
        
        return template.toString().getBytes();
    }

    @Autowired
    private DepenseService depenseService;

    /**
     * Generate an invoice based on an expense
     */
    @Transactional
    public Facture generateFactureFromDepense(Long depenseId) {
        Depense depense = depenseService.getById(depenseId);
        if (depense == null) {
            throw new IllegalArgumentException("Dépense non trouvée avec ID: " + depenseId);
        }
        
        Facture facture = new Facture();
        facture.setNumero("FACT-" + System.currentTimeMillis());
        facture.setMontant(depense.getMontant());
        facture.setStatut("EMISE");
        facture.setDescription("Facture générée depuis la dépense: " + depense.getDescription());
        facture.setDateFacture(new Date());
        facture.setProjectId(depense.getIdProjet());
        
        // Enrich with project data
        enrichFactureWithProject(facture, depense.getIdProjet());
        
        return factureRepository.save(facture);
    }
}


package com.example.financeservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Depense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String description;
    private Double montant;
    private Date date;
    
    @Lob
    @Column(columnDefinition="LONGBLOB") // Use LONGBLOB to store large files
    private byte[] fileData;
    
    @Column(length = 255)
    private String fileName;
    
    @Column(length = 100)
    private String fileType;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutDepense statut;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeDepense type;
    
    // Store project ID directly
    private Long idProjet;
    
    // Transient fields for data transfer only
    @Transient
    private String projectName;
    
    @Transient
    private String projectDescription;
    
    public enum StatutDepense {
        EN_ATTENTE,
        VALIDEE,
        REJETEE
    }
    
    public enum TypeDepense {
        MATERIEL,
        MAIN_DOEUVRE,
        TRANSPORT,
        AUTRES
    }
}
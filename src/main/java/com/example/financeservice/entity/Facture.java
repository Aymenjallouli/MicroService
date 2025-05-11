package com.example.financeservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private Date dateFacture;
    private Double montant;
    private String statut;
    private String description;

    // Project reference as simple ID (not a foreign key)
    private Long projectId;
    
    // Transient fields for data transfer only
    @Transient
    private String projectName;
    
    @Transient
    private String projectDescription;
}


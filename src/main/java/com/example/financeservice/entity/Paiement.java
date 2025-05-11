package com.example.financeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Date datePaiement;
    private Double montant;
    
    @Enumerated(EnumType.STRING)
    private MethodePaiement methodePaiement;
    
    private String reference;
    
    @ManyToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;
    
    private String status; // COMPLETE, EN_ATTENTE, ANNULE
    
    // Explicitly define setId method to resolve compilation error
    public void setId(Long id) {
        this.id = id;
    }
}


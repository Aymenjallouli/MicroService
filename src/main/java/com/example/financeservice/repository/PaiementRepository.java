package com.example.financeservice.repository;

import com.example.financeservice.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    // Custom queries can be added here if needed
}


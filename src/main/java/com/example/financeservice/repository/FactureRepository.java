package com.example.financeservice.repository;

import com.example.financeservice.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    // Custom queries can be added here if needed
}


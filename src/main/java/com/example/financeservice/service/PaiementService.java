package com.example.financeservice.service;



import com.example.financeservice.entity.Paiement;
import com.example.financeservice.repository.PaiementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaiementService {

    private final PaiementRepository repository;

    public PaiementService(PaiementRepository repository) {
        this.repository = repository;
    }

    public List<Paiement> getAll() {
        return repository.findAll();
    }

    public Paiement getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Paiement save(Paiement paiement) {
        return repository.save(paiement);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}


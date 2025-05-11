package com.example.financeservice.controller;

import com.example.financeservice.entity.Paiement;
import com.example.financeservice.service.PaiementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService service;

    public PaiementController(PaiementService service) {
        this.service = service;
    }

    @GetMapping
    public List<Paiement> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Paiement getById(@PathVariable Long id) {
        return service.getById(id);
    }


    @PostMapping
    public Paiement create(@RequestBody Paiement paiement) {
        System.out.println("Paiement reçue : " + paiement);
        return service.save(paiement);
    }


    @PutMapping("/{id}")
    public Paiement update(@PathVariable Long id, @RequestBody Paiement paiement) {
        paiement.setId(id);
        return service.save(paiement);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}


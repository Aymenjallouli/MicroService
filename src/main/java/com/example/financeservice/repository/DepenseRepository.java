package com.example.financeservice.repository;

import com.example.financeservice.entity.Depense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepenseRepository extends JpaRepository<Depense, Long> {

    List<Depense> findByIdProjet(Long idProject);

    @Query(value = "SELECT d.* FROM depense d WHERE d.description LIKE CONCAT('%', :param, '%') OR d.montant LIKE CONCAT('%', :param, '%') ORDER BY d.description ASC", nativeQuery = true)
    List<Depense> search(@Param("param") String param);
}

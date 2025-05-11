package com.example.tasksschservice.repo;

import com.example.tasksschservice.entities.Status;
import com.example.tasksschservice.entities.task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface taskRepo extends JpaRepository<task, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE task s SET s.status = :statut WHERE s.startDate <= CURRENT_DATE AND s.dueDate > CURRENT_DATE AND s.status = 'UNREACHED' ")
    void updateTaskToInProgress(@Param("statut") Status statut);

    @Transactional
    @Modifying
    @Query("UPDATE task s SET s.status = :statut WHERE s.dueDate <= CURRENT_DATE AND s.status = 'INPROGRESS'")
    void updateTaskToDone(@Param("statut") Status statut);
    
    // Ajouter la méthode pour trouver les tâches par projectId
    List<task> findByProjectId(Long projectId);
}

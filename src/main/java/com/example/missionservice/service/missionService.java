package com.example.missionservice.service;

import com.example.missionservice.dto.TaskDTO;
import com.example.missionservice.entities.mission;
import com.example.missionservice.repo.missionRepo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class missionService {
    private final missionRepo missionrepo;

    public missionService(missionRepo missionrepo) {
        this.missionrepo = missionrepo;
    }

    public List<mission> getAllMissions() {
        return missionrepo.findAll();
    }

    public Optional<mission> getMissionById(Long id) {
        return missionrepo.findById(id);
    }

    public mission saveMission(mission mission) {
        return missionrepo.save(mission);
    }

    public void deleteMission(Long id) {
        missionrepo.deleteById(id);
    }
    
    // Implémentation temporaire pour résoudre les erreurs de compilation
    public mission createMissionAndAssignToTask(Long taskId, mission mission) {
        // Pour l'instant, nous enregistrons simplement la mission sans la lier à une tâche
        return missionrepo.save(mission);
    }
    
    // Implémentation temporaire pour résoudre les erreurs de compilation
    public List<TaskDTO> getTasksByMissionId(Long missionId) {
        // Retourne une liste vide pour l'instant
        return Collections.emptyList();
    }
}

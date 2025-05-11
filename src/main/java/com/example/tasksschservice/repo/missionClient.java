package com.example.tasksschservice.repo;
import com.example.tasksschservice.model.mission;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Remplacer le nom du service par le nom exact utilisé dans application.properties et ajouter l'URL directe
@FeignClient(name = "mission-service", url = "${mission-service.url:}")
public interface missionClient {
    @GetMapping("/mission/{id}")
    mission findMissionById(@PathVariable Long id);
    
    @GetMapping("/mission")
    List<mission> allMissionss();
    
    @PostMapping("/mission")
    mission createMission(@RequestBody mission mission);
    
    @GetMapping("/mission/{id}")
    mission getMissionById(@PathVariable Long id);
}

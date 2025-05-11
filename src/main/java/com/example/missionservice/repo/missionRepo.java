package com.example.missionservice.repo;

import com.example.missionservice.entities.mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface missionRepo extends JpaRepository<mission, Long> {
}

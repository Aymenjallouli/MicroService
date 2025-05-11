package com.example.tasksschservice.repo;

import com.example.tasksschservice.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository  extends JpaRepository<Image,Integer> {
    List<Image> findByOrderById();
}
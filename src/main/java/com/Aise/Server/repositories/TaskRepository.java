package com.Aise.Server.repositories;

import java.util.List;

import com.Aise.Server.models.Task;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository<Task, Long> {  
  List<Task> getAllByCourse_Id(Long id);
}

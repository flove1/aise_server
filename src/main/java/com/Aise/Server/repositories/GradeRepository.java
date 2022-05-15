package com.Aise.Server.repositories;

import com.Aise.Server.models.Grade;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {  
  Grade getByUser_IdAndTask_Id(Long userId, Long taskId);
  Boolean existsByUser_IdAndTask_Id(Long userId, Long taskId);
  void deleteAllByTask_Id(Long taskId);
}

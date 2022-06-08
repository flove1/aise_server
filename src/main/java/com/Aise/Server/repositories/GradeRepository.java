package com.Aise.Server.repositories;

import java.util.List;

import com.Aise.Server.models.Grade;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {  
  List<Grade> getAllByUser_Id(Long id);
  List<Grade> getAllByFinished(Boolean finished);
  List<Grade> getAllByFinishedAndUser_Id(Boolean finished, Long userId);
  List<Grade> getAllByTask_Id(Long taskId);
  Grade getByUser_IdAndTask_Id(Long userId, Long taskId);
  Boolean existsByUser_IdAndTask_Id(Long userId, Long taskId);
  void deleteAllByTask_Id(Long taskId);
}

package com.Aise.Server.repositories;

import java.util.List;

import javax.transaction.Transactional;

import com.Aise.Server.models.Grade;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {  
  List<Grade> getAllByUser_Id(Long id);
  List<Grade> getAllByFinished(Boolean finished);
  List<Grade> getAllByFinishedOrGraded(Boolean finished, Boolean graded);
  List<Grade> getAllByFinishedAndUser_Id(Boolean finished, Long userId);
  List<Grade> getAllByFinishedAndUser_IdOrGradedAndUser_Id(Boolean finished, Long id1, Boolean graded, Long id2);
  List<Grade> getAllByTask_Id(Long taskId);
  Grade getByUser_IdAndTask_Id(Long userId, Long taskId);
  Boolean existsByUser_IdAndTask_Id(Long userId, Long taskId);
  @Transactional void deleteAllByTask_Id(Long taskId);
}

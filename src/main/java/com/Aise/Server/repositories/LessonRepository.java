package com.Aise.Server.repositories;

import java.time.DayOfWeek;
import java.util.List;

import com.Aise.Server.models.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LessonRepository extends JpaRepository<Lesson, Long> { 
  List<Lesson> getAllByCourse_IdAndWeekDay(long courseId, DayOfWeek weekDay);
  List<Lesson> getAllByWeekDay(DayOfWeek weekDay);
}

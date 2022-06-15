package com.Aise.Server.repositories;

import java.util.List;

import com.Aise.Server.models.Course;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseRepository extends JpaRepository<Course, Long> {  
  List<Course> getAllByLecturer_IdOrPracticant_Id(Long lecturerId, Long practicantId);
  List<Course> getAllByPracticant_id(Long practicantId);
  List<Course> getAllByLecturer_id(Long lecturerId);
  List<Course> getAllByGroup_Id(Long groupId);
}

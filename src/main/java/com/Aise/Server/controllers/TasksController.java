package com.Aise.Server.controllers;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.Aise.Server.models.Grade;
import com.Aise.Server.models.Task;
import com.Aise.Server.models.User;
import com.Aise.Server.models.enums.Roles;
import com.Aise.Server.repositories.CourseRepository;
import com.Aise.Server.repositories.GradeRepository;
import com.Aise.Server.repositories.GroupParticipantRepository;
import com.Aise.Server.repositories.TaskRepository;
import com.Aise.Server.repositories.TokenRepository;
import com.Aise.Server.repositories.UserRepository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TasksController {

	@Autowired CourseRepository courseRepository;
	@Autowired GradeRepository gradeRepository;
	@Autowired GroupParticipantRepository groupParticipantRepository;
	@Autowired TaskRepository taskRepository;
	@Autowired TokenRepository tokenRepository;
	@Autowired UserRepository userRepository;
	
	JSONObject taskCreateJsonObject(Task task) {
    JSONObject object = new JSONObject();
    object.put("title", task.getTitle());
    object.put("taskId", task.getId());
    object.put("deadline", task.getDeadline());
    object.put("description", task.getDescription());
    object.put("course", task.getCourse().getCourseName());
    object.put("courseId", task.getCourse().getId());
    return object;
	}

	@GetMapping("/tasks")
  @ResponseBody
  public ResponseEntity<String> tasksGet(
		@RequestParam(required = false) Boolean showFinished,
		@RequestParam(required = false) Long userId,
		@RequestParam(required = false) Long courseId,
		@RequestParam String token) {
		try {
			JSONArray responseObject = new JSONArray();
			User user = tokenRepository.getByToken(token).getUser();
			try {
				if (courseId != null) {
					if (user.getRole() == Roles.USER) {
						if (groupParticipantRepository.existsByGroup_IdAndUser_id(courseRepository.getById(courseId).getGroup().getId(), user.getId())) {
							courseRepository.getById(courseId).getTasks().forEach(task -> {
								responseObject.put(taskCreateJsonObject(task));
							});
						}
						else 
            	return new ResponseEntity<String>("You are not enrolled in this course", HttpStatus.BAD_REQUEST);{
						}
					}
					else {
						courseRepository.getById(courseId).getTasks().forEach(task -> {
							responseObject.put(taskCreateJsonObject(task));
						});
					}
				}
				else if (userId != null) {
					if (user.getRole() != Roles.USER) {
						groupParticipantRepository.getAllByUser_Id(userId).forEach(group -> {
							group.getGroup().getCourses().forEach(course -> {
								course.getTasks().forEach(task -> {
									responseObject.put(taskCreateJsonObject(task));
								});
							});
						});
					}
					else {
						return new ResponseEntity<String>("Unsufficient priviliedges", HttpStatus.BAD_REQUEST);
					}
				}
				else {
					if (user.getRole() == Roles.USER) {
						if (showFinished != null) {
							gradeRepository.getAllByUser_Id(user.getId()).forEach(grade -> {
								JSONObject object = taskCreateJsonObject(grade.getTask());
								object.put("finished", grade.getFinished());
								responseObject.put(object);
							});
						}
						else {
							gradeRepository.getAllByFinishedAndUser_Id(false, user.getId()).forEach(grade -> {
								JSONObject object = taskCreateJsonObject(grade.getTask());
								object.put("finished", grade.getFinished());
								responseObject.put(object);
							});
						}
					}
					else {
						courseRepository.getAllByLecturer_IdOrPracticant_Id(user.getId(), user.getId()).forEach(course -> {
							course.getTasks().forEach(task -> {
								responseObject.put(taskCreateJsonObject(task));
							});
						});
					}
				}
				return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.OK);
			} catch (NullPointerException|DataIntegrityViolationException e) {
				return new ResponseEntity<String>("Wrong parameters", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}
  }

	@PostMapping("/tasks")
	@ResponseBody
	public ResponseEntity<String> tasksPost(
		@RequestParam String title,
		@RequestParam String deadline,
		@RequestParam(required = false) String desc,
		@RequestParam Long courseId,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() != Roles.USER) {
				try {
					Task task = new Task();
					task.setTitle(title);
					task.setDeadline(Date.valueOf(deadline));
					task.setDescription(desc==null?"No description":desc);
					task.setCourse(courseRepository.getById(courseId));
					taskRepository.save(task);
					groupParticipantRepository.getAllByGroup_Id(courseRepository.getById(courseId).getGroup().getId()).forEach(part -> {
						Grade grade = new Grade();
						grade.setComment("");
						grade.setFinished(false);
						grade.setUser(part.getUser());
						grade.setTask(task);
						gradeRepository.save(grade);
					});
					return new ResponseEntity<String>("New task was succesfuly created", HttpStatus.CREATED);
				} catch (NullPointerException|DataIntegrityViolationException e) {
					e.printStackTrace();
					return new ResponseEntity<String>("Wrong parameters", HttpStatus.BAD_REQUEST);
				}
			}
			else {
				return new ResponseEntity<String>("Unsufficient privileges", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}  
	}

  @DeleteMapping("/tasks")
  @ResponseBody
  public ResponseEntity<String> deleteTask(
		@RequestParam Long taskId,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() != Roles.USER) {
				try {
					gradeRepository.deleteAllByTask_Id(taskId);
					return new ResponseEntity<String>("Task and its grades was deleted", HttpStatus.OK);
				} catch (NullPointerException|DataIntegrityViolationException e) {
					return new ResponseEntity<String>("Wrong id", HttpStatus.BAD_REQUEST);
				}
			}
			else {
				return new ResponseEntity<String>("Unsufficient privileges", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}  
  }

	@GetMapping("/tasks/grade")
	@ResponseBody
	public ResponseEntity<String> tasksGradeGet(
		@RequestParam(required = false) Long taskId,
		@RequestParam String token){
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() == Roles.USER) {
				try {
					if (taskId != null) {
						JSONObject responseObject = new JSONObject();
						Grade grade = gradeRepository.getByUser_IdAndTask_Id(user.getId(), taskId);
						responseObject.put("title", grade.getTask().getTitle());
						responseObject.put("taskId", grade.getTask().getId());
						responseObject.put("grade", grade.getGrade()==null? "-/-":String.valueOf(grade.getGrade()));
						responseObject.put("name", grade.getTask().getCourse().getPracticant().getName());
						responseObject.put("description", grade.getTask().getDescription());
						responseObject.put("submission", grade.getSubmission()==null? "No submission":grade.getSubmission());
						responseObject.put("comment", grade.getComment());
						return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.OK);
					}
					else {
						JSONArray responseObject = new JSONArray();
						gradeRepository.getAllByUser_Id(user.getId()).forEach(grade -> {
							JSONObject object = new JSONObject();
							object.put("title", grade.getTask().getTitle());
							object.put("taskId", grade.getTask().getId());
							object.put("course", grade.getTask().getCourse().getCourseName());
							object.put("grade", grade.getGrade()==null? "-/-":grade.getGrade());
							object.put("comment", grade.getComment());
							object.put("finished", grade.getFinished());
							responseObject.put(object);
						});
						return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.OK);
					}
				} catch (NullPointerException e) {
					return new ResponseEntity<String>("Wrong parameters", HttpStatus.BAD_REQUEST);
				}
			}
			return new ResponseEntity<String>("No tasks", HttpStatus.BAD_REQUEST);
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}  
	}

  @PutMapping("/tasks/grade")
  @ResponseBody
  public ResponseEntity<String> tasksGradePut(
		@RequestParam Long taskId,
		@RequestParam(required = false) Long userId,
		@RequestParam(required = false) int gradeValue,
		@RequestParam(required = false) String comment,
		@RequestParam(required = false) String submission,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() == Roles.USER) {
				if (gradeRepository.existsByUser_IdAndTask_Id(user.getId(), taskId)) {
					Grade grade = gradeRepository.getByUser_IdAndTask_Id(user.getId(), taskId);
					grade.setTask(taskRepository.getById(taskId));
					grade.setUser(userRepository.getById(user.getId()));
					grade.setFinished(true);
					grade.setSubmission(submission);
					gradeRepository.save(grade);
					return new ResponseEntity<String>("Task was submitted", HttpStatus.OK);
				}
				else{
					return new ResponseEntity<String>("Task does not exist", HttpStatus.BAD_REQUEST);
				}
			}
			else {
				try {
					if (gradeRepository.existsByUser_IdAndTask_Id(userId, taskId)) {
						Grade grade = gradeRepository.getByUser_IdAndTask_Id(userId, taskId);
						grade.setGrade(gradeValue);
						if (comment != null) {
							grade.setComment(comment);
						}
						gradeRepository.save(grade);
						return new ResponseEntity<String>("Grade of task was succesfuly changed", HttpStatus.OK);
					}
					else {
						Grade grade = new Grade();
						grade.setTask(taskRepository.getById(taskId));
						grade.setUser(userRepository.getById(userId));
						grade.setGrade(gradeValue);
						grade.setComment(comment);
						gradeRepository.save(grade);
					}
					return new ResponseEntity<String>("Grade of task was succesfuly created", HttpStatus.CREATED);
				} catch (NullPointerException|DataIntegrityViolationException e) {
					return new ResponseEntity<String>("Wrong parameters", HttpStatus.BAD_REQUEST);
				}
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}  
  }
}
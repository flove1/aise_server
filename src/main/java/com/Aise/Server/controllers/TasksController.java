package com.Aise.Server.controllers;

import java.sql.Timestamp;
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
	
	@GetMapping("/tasks")
  @ResponseBody
  public ResponseEntity<String> tasksGet(
		@RequestParam(required = false) Long userId,
		@RequestParam(required = false) Long courseId,
		@RequestParam String token) {
		try {
			JSONArray responseObject = new JSONArray();
			User user = tokenRepository.getByToken(token).getUser();
			try {
				ArrayList<Task> taskList = new ArrayList<>();
				if (user.getRole() == Roles.USER || userId != null) {
					Long searchId = (user.getRole() != Roles.USER && userId != null)? userId: user.getId();
					if (courseId != null) {
						courseRepository.getById(searchId).getTasks().forEach(task -> {
							taskList.add(task);
						});
					}
					else {
						groupParticipantRepository.getAllByUser_Id(searchId).forEach(group -> {
							group.getGroup().getCourses().forEach(course -> {
								course.getTasks().forEach(task -> {
									taskList.add(task);
								});
							});
						});
					}
				}
				else {
					if (courseId != null) {
						courseRepository.getById(courseId).getTasks().forEach(task -> {
							taskList.add(task);
						});
					}
					else {
						courseRepository.getAllByLecturer_IdOrPracticant_Id(user.getId(), user.getId()).forEach(course -> {
							course.getTasks().forEach(task -> {
								taskList.add(task);
							});
						});
					}
				}
				taskList.forEach(task -> {
					JSONObject object = new JSONObject();
					object.put("title", task.getTitle());
					object.put("deadline", task.getDeadline());
					object.put("description", task.getDescription());
					object.put("course", task.getCourse().getCourseName());
					object.put("courseId", task.getCourse().getId());
					responseObject.put(object);
				});
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
					task.setDeadline(Timestamp.valueOf(deadline));
					task.setDescription(desc);
					task.setCourse(courseRepository.getById(courseId));
					taskRepository.save(task);
					return new ResponseEntity<String>("New task was succesfuly created", HttpStatus.CREATED);
				} catch (NullPointerException|DataIntegrityViolationException e) {
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

  @PutMapping("/tasks/grade")
  @ResponseBody
  public ResponseEntity<String> tasksGradePut(
		@RequestParam Long taskId,
		@RequestParam Long userId,
		@RequestParam int gradeValue,
		@RequestParam(required = false) String comment,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() != Roles.USER) {
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
			else {
				return new ResponseEntity<String>("Unsufficient privileges", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}  
  }
}
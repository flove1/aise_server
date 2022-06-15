package com.Aise.Server.controllers;

import java.sql.Blob;
import java.sql.Date;

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
    object.put("group", task.getCourse().getGroup().getGroupName());
    return object;
	}

	@GetMapping("/tasks")
  @ResponseBody
  public ResponseEntity<String> tasksGet(
		@RequestParam(required = false) Boolean showFinished,
		@RequestParam(required = false) Boolean showGrades,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			try {
				JSONArray responseObject = new JSONArray();
				if (user.getRole() == Roles.STUDENT) {
					showFinished = showFinished!=null? true:false;
					gradeRepository.getAllByFinishedAndUser_Id(showFinished, user.getId()).forEach(grade -> {
						JSONObject object = taskCreateJsonObject(grade.getTask());
						if (showGrades != null) {
							object.put("grade", grade.getGrade()==null? "-/-":String.valueOf(grade.getGrade()));
						}
						object.put("finished", grade.getFinished());
						responseObject.put(object);
					});
				}
				else {
					if (showGrades != null) {
						courseRepository.getAllByPracticant_id(user.getId()).forEach(course -> {
							course.getTasks().forEach(task -> {
								JSONObject taskObject = taskCreateJsonObject(task);
								JSONArray gradesArray = new JSONArray();
								task.getGrades().forEach(grade -> {
									JSONObject gradeObject = new JSONObject();
									gradeObject.put("studentId", grade.getUser().getId());
									gradeObject.put("finished", grade.getFinished());
									gradesArray.put(gradeObject);
								});
								taskObject.put("grades", gradesArray);
								responseObject.put(taskObject);
							});
						});
					}
					else {
						courseRepository.getAllByPracticant_id(user.getId()).forEach(course -> {
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
			if (user.getRole() != Roles.STUDENT) {
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

	@PutMapping("/tasks")
	@ResponseBody
	public ResponseEntity<String> tasksPut(
		@RequestParam Long taskId,
		@RequestParam String title,
		@RequestParam String deadline,
		@RequestParam(required = false) String desc,
		@RequestParam Long courseId,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() != Roles.STUDENT) {
				try {
					Task task = taskRepository.getById(taskId);
					task.setTitle(title);
					task.setDeadline(Date.valueOf(deadline));
					task.setDescription(desc==null?"No description":desc);
					task.setCourse(courseRepository.getById(courseId));
					taskRepository.save(task);
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
			if (user.getRole() != Roles.STUDENT) {
				try {
					taskRepository.deleteById(taskId);
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
		@RequestParam Long taskId,
		@RequestParam(required = false) Long userId,
		@RequestParam String token){
		try {
			User user = tokenRepository.getByToken(token).getUser();
			try {
				if (user.getRole() == Roles.STUDENT) {
					JSONObject responseObject = new JSONObject();
					Grade grade = gradeRepository.getByUser_IdAndTask_Id(user.getId(), taskId);
					responseObject.put("title", grade.getTask().getTitle());
					responseObject.put("grade", grade.getGrade()==null? "":grade.getGrade());
					responseObject.put("description", grade.getTask().getDescription());
					responseObject.put("submission", grade.getSubmission()==null? "No submission":grade.getSubmission());
					responseObject.put("comment", grade.getComment());
					responseObject.put("teacher", grade.getTask().getCourse().getPracticant().getName());
					return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.OK);
				}
				else {
					if (userId != null ) {
						JSONObject responseObject = new JSONObject();
						Grade grade = gradeRepository.getByUser_IdAndTask_Id(userId, taskId);
						responseObject.put("title", grade.getTask().getTitle());
						responseObject.put("grade", grade.getGrade()==null? "":grade.getGrade());
						responseObject.put("description", grade.getTask().getDescription());
						responseObject.put("submission", grade.getSubmission());
						responseObject.put("comment", grade.getComment());
						return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.OK);
					}
					else {
						JSONArray responseObject = new JSONArray();
						taskRepository.getById(taskId).getGrades().forEach(grade -> {
							JSONObject object = new JSONObject();
							object.put("taskId", grade.getTask().getId());
							object.put("submission", grade.getSubmission()==null? "No submission":grade.getSubmission());
							object.put("student", grade.getUser().getName());
							object.put("studentId", grade.getUser().getId());
							object.put("graded", grade.getGraded());
							responseObject.put(object);
						});
						return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.OK);
					}
				}
			} catch (NullPointerException e) {
				return new ResponseEntity<String>("Wrong parameters", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}  
	}

  @PutMapping("/tasks/grade")
  @ResponseBody
  public ResponseEntity<String> tasksGradePut(
		@RequestParam Long taskId,
		@RequestParam(required = false) Long userId,
		@RequestParam(required = false) Integer gradeValue,
		@RequestParam(required = false) String comment,
		@RequestParam(required = false) Blob submission,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() == Roles.STUDENT) {
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
						if (gradeValue != null) {
							grade.setGraded(true);
							grade.setFinished(true);
						}
						else {
							grade.setGraded(false);
						}
						grade.setGrade(gradeValue);
						grade.setComment(comment);
						gradeRepository.save(grade);
						return new ResponseEntity<String>("Grade of task was succesfuly changed", HttpStatus.OK);
					}
					else {
						Grade grade = new Grade();
						grade.setTask(taskRepository.getById(taskId));
						grade.setUser(userRepository.getById(userId));
						grade.setFinished(true);
						grade.setGraded(true);
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
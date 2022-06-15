package com.Aise.Server.controllers;

import com.Aise.Server.models.Course;
import com.Aise.Server.models.User;
import com.Aise.Server.models.enums.Roles;
import com.Aise.Server.repositories.CourseRepository;
import com.Aise.Server.repositories.GroupParticipantRepository;
import com.Aise.Server.repositories.GroupRepository;
import com.Aise.Server.repositories.LessonRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoursesController {

	// try {
	// 	User user = tokenRepository.getByToken(token).getUser();
	// 	if (user.getRole() != Roles.User) {
	// 		try {
	// 			return new ResponseEntity<String>("Task was succesfuly created", HttpStatus.CREATED);
	// 		} catch (NullPointerException|DataIntegrityViolationException e) {
	// 			return new ResponseEntity<String>("Wrong parameters", HttpStatus.BAD_REQUEST);
	// 		}
	// 	}
	// 	else {
	// 		return new ResponseEntity<String>("Unsufficient privileges", HttpStatus.BAD_REQUEST);
	// 	}
	// } catch (NullPointerException|DataIntegrityViolationException e) {
	// 	return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
	// }  

	@Autowired CourseRepository courseRepository;
	@Autowired GroupRepository groupRepository;
	@Autowired LessonRepository lessonRepository;
	@Autowired GroupParticipantRepository groupParticipantRepository;
	@Autowired TaskRepository taskRepository;
	@Autowired TokenRepository tokenRepository;
	@Autowired UserRepository userRepository;

	@GetMapping("/courses")
	@ResponseBody
	public ResponseEntity<String> coursesGet( 
		@RequestParam(required = false) Long groupId,
		@RequestParam(required = false) Long userId,
		@RequestParam(required = false) Boolean getAll,
		@RequestParam(required = false) Boolean getFullList,
		@RequestParam String token) {
		try {
			JSONArray responseObject = new JSONArray();
			User user = tokenRepository.getByToken(token).getUser();
			try {
				switch (user.getRole()) {
					case STUDENT:
						groupParticipantRepository.getAllByUser_Id(user.getId()).forEach(group -> {
							group.getGroup().getCourses().forEach(course -> {
								JSONObject object = new JSONObject();
								object.put("courseId", course.getId());
								object.put("courseName", course.getCourseName());
								object.put("groupId", course.getGroup().getId());
								object.put("groupName", course.getGroup().getGroupName());
								responseObject.put(object);
							});
						});
						break;
					case TEACHER:
						if (userId != null) {
							groupParticipantRepository.getAllByUser_Id(userId).forEach(group -> {
								group.getGroup().getCourses().forEach(course -> {
									JSONObject object = new JSONObject();
									object.put("courseId", course.getId());
									object.put("courseName", course.getCourseName());
									object.put("groupId", course.getGroup().getId());
									object.put("groupName", course.getGroup().getGroupName());
									responseObject.put(object);
								});
							});
						}
						else if (getFullList != null) {
							JSONObject rObject = new JSONObject();
							JSONArray lecturerCourses = new JSONArray();						
							courseRepository.getAllByLecturer_id(user.getId()).forEach(course -> {
								JSONObject object = new JSONObject();
								object.put("courseId", course.getId());
								object.put("courseName", course.getCourseName());
								object.put("groupId", course.getGroup().getId());
								object.put("groupName", course.getGroup().getGroupName());
								lecturerCourses.put(object);
							});
							rObject.put("Lecturer", lecturerCourses);
							JSONArray practicantCourses = new JSONArray();						
							courseRepository.getAllByPracticant_id(user.getId()).forEach(course -> {
								JSONObject object = new JSONObject();
								object.put("courseId", course.getId());
								object.put("courseName", course.getCourseName());
								object.put("groupId", course.getGroup().getId());
								object.put("groupName", course.getGroup().getGroupName());
								practicantCourses.put(object);
							});
							rObject.put("Practicant", practicantCourses);
							return new ResponseEntity<String>(rObject.toString(4), HttpStatus.OK);
						}
						else { 
							courseRepository.getAllByPracticant_id(user.getId()).forEach(course -> {
								JSONObject object = new JSONObject();
								object.put("courseId", course.getId());
								object.put("courseName", course.getCourseName());
								object.put("groupId", course.getGroup().getId());
								object.put("groupName", course.getGroup().getGroupName());
								responseObject.put(object);
							});
						}
						break;
					case ADMIN:
						if (groupId != null) {
							courseRepository.getAllByGroup_Id(groupId).forEach(course -> {
								JSONObject object = new JSONObject();
								object.put("courseId", course.getId());
								object.put("courseName", course.getCourseName());
								object.put("groupId", course.getGroup().getId());
								object.put("groupName", course.getGroup().getGroupName());
								object.put("practicant", course.getPracticant().getName());
								object.put("practicantId", course.getPracticant().getId());
								object.put("lecturer", course.getLecturer().getName());
								object.put("lecturerId", course.getLecturer().getId());
								responseObject.put(object);
							});
						}
						else if (getAll != null) {
							courseRepository.findAll().forEach(course -> {
								JSONObject object = new JSONObject();
								object.put("courseId", course.getId());
								object.put("courseName", course.getCourseName());
								object.put("groupId", course.getGroup().getId());
								object.put("groupName", course.getGroup().getGroupName());
								object.put("practicant", course.getPracticant().getName());
								object.put("practicantId", course.getPracticant().getId());
								object.put("lecturer", course.getLecturer().getName());
								object.put("lecturerId", course.getLecturer().getId());
								responseObject.put(object);
							});	
						}
						else if (getFullList != null) {
							JSONObject rObject = new JSONObject();
							JSONArray lecturerCourses = new JSONArray();						
							courseRepository.getAllByLecturer_id(user.getId()).forEach(course -> {
								JSONObject object = new JSONObject();
								object.put("courseId", course.getId());
								object.put("courseName", course.getCourseName());
								object.put("groupId", course.getGroup().getId());
								object.put("groupName", course.getGroup().getGroupName());
								lecturerCourses.put(object);
							});
							rObject.put("Lecturer", lecturerCourses);
							JSONArray practicantCourses = new JSONArray();						
							courseRepository.getAllByPracticant_id(user.getId()).forEach(course -> {
								JSONObject object = new JSONObject();
								object.put("courseId", course.getId());
								object.put("courseName", course.getCourseName());
								object.put("groupId", course.getGroup().getId());
								object.put("groupName", course.getGroup().getGroupName());
								practicantCourses.put(object);
							});
							rObject.put("Practicant", practicantCourses);
							return new ResponseEntity<String>(rObject.toString(4), HttpStatus.OK);
						}
						else {
							courseRepository.getAllByPracticant_id(user.getId()).forEach(course -> {
								JSONObject object = new JSONObject();
								object.put("courseId", course.getId());
								object.put("courseName", course.getCourseName());
								object.put("groupId", course.getGroup().getId());
								object.put("groupName", course.getGroup().getGroupName());
								responseObject.put(object);
							});
						}
						break;
				}
				return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.OK);
			} catch (NullPointerException|DataIntegrityViolationException e) {
				e.printStackTrace();
				return new ResponseEntity<String>("Wrong parameters", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}  
	}

	@PostMapping("/courses")
	@ResponseBody
	public ResponseEntity<String> coursesPost(
		@RequestParam(required = false) Long courseId,
		@RequestParam String courseName,
		@RequestParam Long lecturerId,
		@RequestParam Long practicantId,
		@RequestParam(required = false) Long groupId,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() == Roles.ADMIN) {
				try {
					if (courseId != null) {
						Course course = courseRepository.getById(courseId);
						course.setCourseName(courseName);
						course.setLecturer(userRepository.getById(lecturerId));
						course.setPracticant(userRepository.getById(practicantId));		
						courseRepository.save(course);
						return new ResponseEntity<String>("Course was succesfuly updated", HttpStatus.OK);
					}
					else {
						Course course = new Course();
						course.setCourseName(courseName);
						course.setLecturer(userRepository.getById(lecturerId));
						course.setPracticant(userRepository.getById(practicantId));
						course.setGroup(groupRepository.getById(groupId));
						courseRepository.save(course);
						return new ResponseEntity<String>("New course was succesfuly created", HttpStatus.CREATED);
					}
				} catch (NullPointerException|DataIntegrityViolationException e) {
					return new ResponseEntity<String>("Invalid parameters", HttpStatus.BAD_REQUEST);
				}  
			}
			else {
				return new ResponseEntity<String>("Unsufficient privileges", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}  
	}

	@DeleteMapping("/courses")
	@ResponseBody
	public ResponseEntity<String> courseDelete(
		@RequestParam Long courseId,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() == Roles.ADMIN) {
				courseRepository.deleteById(courseId);
				return new ResponseEntity<String>("Course was succesfully deleted", HttpStatus.OK);
			}
			else {
				return new ResponseEntity<String>("Unsufficient privileges", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}  
	}
}
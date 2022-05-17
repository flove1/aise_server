package com.Aise.Server.controllers;

import com.Aise.Server.models.Course;
import com.Aise.Server.models.User;
import com.Aise.Server.models.enums.Roles;
import com.Aise.Server.repositories.CourseRepository;
import com.Aise.Server.repositories.GroupParticipantRepository;
import com.Aise.Server.repositories.GroupRepository;
import com.Aise.Server.repositories.TokenRepository;
import com.Aise.Server.repositories.UserRepository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	@Autowired GroupParticipantRepository groupParticipantRepository;
	@Autowired TokenRepository tokenRepository;
	@Autowired UserRepository userRepository;

	@GetMapping("/courses")
	@ResponseBody
	public ResponseEntity<String> coursesGet( 
		@RequestParam(required = false) Long userId,
		@RequestParam String token) {
		try {
			JSONArray responseObject = new JSONArray();
			User user = tokenRepository.getByToken(token).getUser();
			try {
				//if request was sent by user
				if (user.getRole() == Roles.USER) {
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
				}
				//if request was sent by teacher or admin
				else {
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
					else { 
						courseRepository.getAllByLecturer_IdOrPracticant_Id(user.getId(), user.getId()).forEach(course -> {
							JSONObject object = new JSONObject();
							object.put("courseId", course.getId());
							object.put("courseName", course.getCourseName());
							object.put("groupId", course.getGroup().getId());
							object.put("groupName", course.getGroup().getGroupName());
							responseObject.put(object);
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

	@PostMapping("/courses")
	@ResponseBody
	public ResponseEntity<String> coursesPost(
		@RequestParam String courseName,
		@RequestParam Long lecturerId,
		@RequestParam Long practicantId,
		@RequestParam Long groupId,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() == Roles.ADMIN) {
				try {
					Course course = new Course();
					course.setCourseName(courseName);
					course.setLecturer(userRepository.getById(lecturerId));
					course.setPracticant(userRepository.getById(practicantId));
					course.setGroup(groupRepository.getById(groupId));
					courseRepository.save(course);
					return new ResponseEntity<String>("New course was succesfuly created", HttpStatus.CREATED);
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
}
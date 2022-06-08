package com.Aise.Server.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.Aise.Server.models.GroupParticipant;
import com.Aise.Server.models.Token;
import com.Aise.Server.models.User;
import com.Aise.Server.models.enums.Roles;
import com.Aise.Server.repositories.CourseRepository;
import com.Aise.Server.repositories.GroupParticipantRepository;
import com.Aise.Server.repositories.GroupRepository;
import com.Aise.Server.repositories.LessonRepository;
import com.Aise.Server.repositories.RoomRepository;
import com.Aise.Server.repositories.TaskRepository;
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
public class UserController {

	@Autowired CourseRepository courseRepository;
	@Autowired GroupRepository groupRepository;
	@Autowired GroupParticipantRepository groupParticipantRepository;
	@Autowired LessonRepository lessonRepository;
	@Autowired RoomRepository roomRepository;
	@Autowired TaskRepository taskRepository;
	@Autowired TokenRepository tokenRepository;
	@Autowired UserRepository userRepository;
	
	@GetMapping("/login") 
	@ResponseBody
	public ResponseEntity<String> login(
		@RequestParam String email, 
		@RequestParam String password) {	
		try {
			User user = userRepository.getByEmail(email);
			if (password.equals(user.getPassword())) {
				Token token = new Token(user);		
				tokenRepository.save(token);
				tokenRepository.deleteByExpiryDateLessThan(new Date(System.currentTimeMillis()));
				JSONObject responseObject = new JSONObject();
				responseObject.put("token", token.getToken());
				responseObject.put("role", user.getRole().toString());
				responseObject.put("name", user.getName());
				responseObject.put("id", user.getId());
				return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<String>("Wrong credentials", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Wrong credentials", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/users")
	@ResponseBody
	public ResponseEntity<String> userGet(
		@RequestParam(required = false) Long groupId,
		@RequestParam(required = false) Boolean showStudents,
		@RequestParam(required = false) Boolean showTeachers,
		@RequestParam String token) {
		if (tokenRepository.existsByToken(token)) {
			JSONArray responseObject = new JSONArray();
			if (groupId != null) {
				List<GroupParticipant> list = groupParticipantRepository.getAllByGroup_Id(groupId);
				list.forEach(user -> {
					JSONObject object = new JSONObject();
					object.put("id", user.getUser().getId());
					object.put("name", user.getUser().getName());
					object.put("email", user.getUser().getEmail());
					responseObject.put(object);
				});
				return new ResponseEntity<String>(responseObject.toString(), HttpStatus.CREATED);
			}
			else {
				List<User> list = new ArrayList<>();
				if (Boolean.TRUE.equals(showTeachers) || Boolean.TRUE.equals(showStudents)) {
					if (Boolean.TRUE.equals(showTeachers)) {
						list.addAll(userRepository.getAllByRole(Roles.TEACHER));
						list.addAll(userRepository.getAllByRole(Roles.ADMIN));
					}
					if (Boolean.TRUE.equals(showStudents)) {
						list.addAll(userRepository.getAllByRole(Roles.STUDENT));
					}
					list.forEach(user -> {
						JSONObject object = new JSONObject();
						object.put("id", user.getId());
						object.put("name", user.getName());
						object.put("email", user.getEmail());
						responseObject.put(object);
					});
					return new ResponseEntity<String>(responseObject.toString(), HttpStatus.CREATED);	
				}
				else {
					return new ResponseEntity<String>("Bad request", HttpStatus.BAD_REQUEST);	
				}
			}
		}
		else {
      return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}
	}

  @PostMapping("/users") 
	@ResponseBody
	public ResponseEntity<String> userPost(
		@RequestParam String name, 
		@RequestParam String email, 
		@RequestParam(required = false) String password,
		@RequestParam(required = false) String role,
		@RequestParam String token) {
			try {
				if (tokenRepository.getByToken(token).getUser().getRole() == Roles.ADMIN) {
					if (!userRepository.existsByEmail(email)) {
						User user = new User();
						user.setEmail(email);
						user.setName(name);
						if (password != null) {
							user.setPassword(password);
						}
						try {
							user.setRole(Roles.valueOf(role));
						} catch (IllegalArgumentException e) {
							user.setRole(Roles.STUDENT);
						}
						userRepository.save(user);
						return new ResponseEntity<String>("New user was created", HttpStatus.CREATED);
					}
					else {
						return new ResponseEntity<String>("User already exists", HttpStatus.BAD_REQUEST);
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
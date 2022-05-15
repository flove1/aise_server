package com.Aise.Server.controllers;

import java.sql.Date;
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
				return new ResponseEntity<String>(token.getToken(), HttpStatus.CREATED);
			}
			else {
				return new ResponseEntity<String>("Wrong password", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
			return new ResponseEntity<String>("No such user exists", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/users")
	@ResponseBody
	public ResponseEntity<String> userGet(
		@RequestParam(required = false) Long groupId,
		@RequestParam(required = false) Roles role,
		@RequestParam String token) {
		if (tokenRepository.existsByToken(token)) {
			JSONArray responseObject = new JSONArray();
			if (groupId != null) {
				List<GroupParticipant> list = groupParticipantRepository.getAllByGroup_Id(groupId);
				list.forEach(user -> {
					JSONObject object = new JSONObject();
					object.put("name", user.getUser().getName());
					object.put("email", user.getUser().getEmail());
					responseObject.put(object);
				});
				return new ResponseEntity<String>(responseObject.toString(), HttpStatus.CREATED);
			}
			else if (role != null) {
				List<User> list = userRepository.getAllByRole(role);
				list.forEach(user -> {
					JSONObject object = new JSONObject();
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
		else {
      return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}
	}

  @PostMapping("/users") 
	@ResponseBody
	public ResponseEntity<String> userPost(@RequestParam String email, 
		@RequestParam String password,
		@RequestParam String name, 
		@RequestParam(required = false) String role,
		@RequestParam String token) {
			try {
				if (tokenRepository.getByToken(token).getUser().getRole() == Roles.ADMIN) {
					if (!userRepository.existsByEmail(email)) {
						User user = new User();
						user.setEmail(email);
						user.setName(name);
						user.setPassword(password);
						try {
							user.setRole(Roles.valueOf(role));
						} catch (IllegalArgumentException e) {
							user.setRole(Roles.USER);
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
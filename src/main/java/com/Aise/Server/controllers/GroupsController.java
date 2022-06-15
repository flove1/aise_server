package com.Aise.Server.controllers;

import java.util.ArrayList;
import java.util.List;

import com.Aise.Server.models.Grade;
import com.Aise.Server.models.Group;
import com.Aise.Server.models.GroupParticipant;
import com.Aise.Server.models.enums.Roles;
import com.Aise.Server.repositories.GradeRepository;
import com.Aise.Server.repositories.GroupParticipantRepository;
import com.Aise.Server.repositories.GroupRepository;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupsController {

	@Autowired GroupRepository groupRepository;
  @Autowired GroupParticipantRepository groupParticipantRepository;
	@Autowired TaskRepository taskRepository;
	@Autowired TokenRepository tokenRepository;
	@Autowired UserRepository userRepository;
	@Autowired GradeRepository gradeRepository;


  @PutMapping("/groups/user")
  @ResponseBody
  public ResponseEntity<String> groupsUserPost(
    @RequestParam(required = false) String groupIds,
    @RequestParam(required = false) String pGroupIds,
    @RequestParam(required = false) String lGroupIds,
    @RequestParam Long userId,
    @RequestParam String token) {
    try {
      if (tokenRepository.getByToken(token).getUser().getRole() == Roles.ADMIN) {
        try {
          if (userRepository.getById(userId).getRole() == Roles.STUDENT) {
            groupParticipantRepository.deleteAllByUser_Id(userId);
            for (String groupId: groupIds.split("\\.")) {
              GroupParticipant groupParticipant = new GroupParticipant();
              groupParticipant.setGroup(groupRepository.getById(Long.valueOf(groupId)));
              groupRepository.getById(Long.valueOf(groupId)).getCourses().forEach(course -> {
                course.getTasks().forEach(task -> {
                  Grade grade = new Grade();
                  grade.setTask(taskRepository.getById(task.getId()));
                  grade.setUser(userRepository.getById(userId));
                  gradeRepository.save(grade);
                });
              });
              groupParticipant.setUser(userRepository.getById(userId));
              groupParticipantRepository.save(groupParticipant);
            } 
          }         
          else {

          }
          return new ResponseEntity<String>("Groups were updated", HttpStatus.CREATED);
        } catch (NullPointerException e) {
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

  @GetMapping("/groups")
  @ResponseBody
  public ResponseEntity<String> groupsGet(
    @RequestParam(required = false) Long userId,
    @RequestParam String token) {
    if (tokenRepository.existsByToken(token)) {
      JSONArray responseObject = new JSONArray();
      if (userId != null) {
        List<GroupParticipant> list = groupParticipantRepository.getAllByUser_Id(userId);
        list.forEach(group -> {
          JSONObject object = new JSONObject();
          object.put("groupName", group.getGroup().getGroupName());
          object.put("groupId", group.getGroup().getId());
          responseObject.put(object);
        });
      }
      else {
        List<Group> list = groupRepository.findAll();
        list.forEach(group -> {
          JSONObject object = new JSONObject();
          object.put("groupName", group.getGroupName());
          object.put("groupId", group.getId());
          responseObject.put(object);
        });
      }
      return new ResponseEntity<String>(responseObject.toString(), HttpStatus.CREATED);
    } 
    else {
      return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
    }
  }
  
	@PostMapping("/groups")
	@ResponseBody
	public ResponseEntity<String> groupsPost(
    @RequestParam String groupName,
		@RequestParam String token) {
    try {
      if (tokenRepository.getByToken(token).getUser().getRole() == Roles.ADMIN) {
        if (!groupRepository.existsByGroupName(groupName)) {
          Group group = new Group();
          group.setGroupName(groupName);
          groupRepository.save(group);
          return new ResponseEntity<String>("New group was created", HttpStatus.CREATED);
        }
        else {
          return new ResponseEntity<String>("Group already exists", HttpStatus.BAD_REQUEST);
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
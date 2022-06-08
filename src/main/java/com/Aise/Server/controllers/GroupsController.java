package com.Aise.Server.controllers;

import java.util.List;

import com.Aise.Server.models.Group;
import com.Aise.Server.models.GroupParticipant;
import com.Aise.Server.models.enums.Roles;
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
public class GroupsController {

	@Autowired GroupRepository groupRepository;
  @Autowired GroupParticipantRepository groupParticipantRepository;
	@Autowired TokenRepository tokenRepository;
	@Autowired UserRepository userRepository;


  @PostMapping("/groups/user")
  @ResponseBody
  public ResponseEntity<String> groupsUserPost(
    @RequestParam Long groupId,
    @RequestParam Long userId,
    @RequestParam String token) {
    try {
      if (tokenRepository.getByToken(token).getUser().getRole() == Roles.ADMIN) {
        try {
          GroupParticipant groupParticipant = new GroupParticipant();
          groupParticipant.setGroup(groupRepository.getById(groupId));
          groupParticipant.setUser(userRepository.getById(userId));
          groupParticipantRepository.save(groupParticipant);
          return new ResponseEntity<String>("New group participant was succesfuly added", HttpStatus.CREATED);
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
    @RequestParam String token) {
    if (tokenRepository.existsByToken(token)) {
      List<Group> list = groupRepository.findAll();
      JSONArray responseObject = new JSONArray();
      list.forEach(group -> {
        JSONObject object = new JSONObject();
        object.put("groupName", group.getGroupName());
        object.put("groupId", group.getId());
        responseObject.put(object);
      });
      return new ResponseEntity<String>(responseObject.toString(), HttpStatus.CREATED);
    } 
    else {
      return new ResponseEntity<String>("Invalid token", HttpStatus.CREATED);
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
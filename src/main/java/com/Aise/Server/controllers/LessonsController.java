package com.Aise.Server.controllers;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;

import com.Aise.Server.models.Lesson;
import com.Aise.Server.models.User;
import com.Aise.Server.models.enums.LessonTypes;
import com.Aise.Server.models.enums.Roles;
import com.Aise.Server.repositories.CourseRepository;
import com.Aise.Server.repositories.GroupParticipantRepository;
import com.Aise.Server.repositories.GroupRepository;
import com.Aise.Server.repositories.LessonRepository;
import com.Aise.Server.repositories.RoomRepository;
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
public class LessonsController {

	@Autowired CourseRepository courseRepository;
	@Autowired GroupRepository groupRepository;
	@Autowired GroupParticipantRepository groupParticipantRepository;
	@Autowired LessonRepository lessonRepository;
	@Autowired RoomRepository roomRepository;
	@Autowired TokenRepository tokenRepository;
	@Autowired UserRepository userRepository;

	@GetMapping("/lessons")
	@ResponseBody
	public ResponseEntity<String> lessonsGet(
		@RequestParam(required = false) DayOfWeek weekDay,
		@RequestParam String token) {
		if (tokenRepository.existsByToken(token)) {
			JSONArray responseObject = new JSONArray();
			User user = tokenRepository.getByToken(token).getUser();
			DayOfWeek selectedDay = (weekDay==null?DayOfWeek.of(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)):weekDay);
			System.out.println(selectedDay);
			ArrayList<Lesson> lessonsList = new ArrayList<>();
			if (user.getRole() == Roles.STUDENT) {
				groupParticipantRepository.getAllByUser_Id(user.getId()).forEach(group -> {
					group.getGroup().getCourses().forEach(course -> {
						lessonRepository.getAllByCourse_IdAndWeekDay(course.getId(), selectedDay).forEach(lesson -> {
							lessonsList.add(lesson);
						});
					});
				});
			}
			else {
				courseRepository.getAllByLecturer_IdOrPracticant_Id(user.getId(), user.getId()).forEach(course -> {
					lessonRepository.getAllByCourse_IdAndWeekDay(course.getId(), selectedDay).forEach(lesson -> {
						lessonsList.add(lesson);
					});
				});
			}
			lessonsList.forEach(lesson -> {
				JSONObject object = new JSONObject();
				object.put("course", lesson.getCourse().getCourseName());
				object.put("courseId", lesson.getCourse().getId());
				object.put("start", lesson.getStartTime());
				object.put("end", lesson.getEndTime());
				object.put("room", lesson.getRoom().getRoom());
				object.put("type", lesson.getType().toString());
				if (lesson.getType() == LessonTypes.LECTURE) {
					object.put("teacher", lesson.getCourse().getLecturer().getName());
					object.put("teacherId", lesson.getCourse().getLecturer().getId());
				}
				else {
					object.put("teacher", lesson.getCourse().getPracticant().getName());
					object.put("teacherId", lesson.getCourse().getPracticant().getId());
				}
				object.put("weekday", lesson.getWeekDay());
				responseObject.put(object);
			});
			return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/lessons")
	@ResponseBody
	public ResponseEntity<String> lessonsPost(
		@RequestParam Long courseId,
		@RequestParam Time start,
		@RequestParam Time end,
		@RequestParam DayOfWeek weekDay,
		@RequestParam LessonTypes type,
		@RequestParam(required = false) String room,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() != Roles.STUDENT) {
				try {
					Lesson lesson = new Lesson();
					lesson.setCourse(courseRepository.getById(courseId));
					lesson.setStartTime(start);
					lesson.setEndTime(end);
					lesson.setWeekDay(weekDay);
					lesson.setType(type);
					lesson.setRoom(roomRepository.getByRoom((room == null? "Online":room)));
					lessonRepository.save(lesson);
					return new ResponseEntity<String>("Lesson was succesfuly created", HttpStatus.CREATED);
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

  @DeleteMapping("/lessons")
  @ResponseBody
  public ResponseEntity<String> lessonsDelete(
		@RequestParam Long lessonId,
		@RequestParam String token) {
		try {
			User user = tokenRepository.getByToken(token).getUser();
			if (user.getRole() == Roles.ADMIN || user.getRole() == Roles.TEACHER) {
				try {
					lessonRepository.deleteById(lessonId);
					return new ResponseEntity<String>("Lesson was succesfully deleted", HttpStatus.ACCEPTED);
				} catch (NullPointerException|DataIntegrityViolationException e) {
					return new ResponseEntity<String>("Wrong id", HttpStatus.BAD_REQUEST);
				}
			}
			else {
				return new ResponseEntity<String>("Unsufficient priviliedges", HttpStatus.BAD_REQUEST);
			}
		} catch (NullPointerException|DataIntegrityViolationException e) {
      return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
		}
  }

	@GetMapping("/rooms")
	@ResponseBody
	public ResponseEntity<String> roomsGet(
		@RequestParam String token) {
			if (tokenRepository.existsByToken(token)) {
				JSONArray responseObject = new JSONArray();
				roomRepository.findAll().forEach(room -> {
					JSONObject object = new JSONObject();
					object.put("room", room.getRoom());
					responseObject.put(object);
				});
				return new ResponseEntity<String>(responseObject.toString(4), HttpStatus.OK);

			}
			else {
				return new ResponseEntity<String>("Invalid token", HttpStatus.BAD_REQUEST);
			}
		}
}
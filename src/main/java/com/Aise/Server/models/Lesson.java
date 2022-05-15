package com.Aise.Server.models;

import java.sql.Time;
import java.time.DayOfWeek;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.Aise.Server.models.enums.LessonTypes;

@Entity
@Table(name="lessons")
public class Lesson {
  @Column @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Course.class)
  @JoinColumn(name = "course_id", referencedColumnName = "id")
  private Course course;

  @Column private Time startTime;
  @Column private Time endTime;
  @Column @Enumerated(EnumType.STRING) private DayOfWeek weekDay;

  @Column @Enumerated(EnumType.STRING) LessonTypes type;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Room.class)
  @JoinColumn(name = "room", referencedColumnName = "room")
  private Room room;

  public Lesson() {}
  
  public Long getId() {
    return id;
  }
  public Course getCourse() {
    return course;
  }
  public Time getStartTime() {
    return startTime;
  }
  public Time getEndTime() {
    return endTime;
  }
  public DayOfWeek getWeekDay() {
    return weekDay;
  }
  public LessonTypes getType() {
    return type;
  }
  public Room getRoom() {
    return room;
  }
  
  public void setCourse(Course course) {
    this.course = course;
  }
  public void setStartTime(Time startTime) {
    this.startTime = startTime;
  }
  public void setEndTime(Time endTime) {
    this.endTime = endTime;
  }
  public void setWeekDay(DayOfWeek weekDay) {
    this.weekDay = weekDay;
  }
  public void setType(LessonTypes type) {
    this.type = type;
  }
  public void setRoom(Room room) {
    this.room = room;
  }
}
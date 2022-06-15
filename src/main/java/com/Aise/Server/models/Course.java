package com.Aise.Server.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="courses")
public class Course {
  @Column @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
  @Column private String courseName;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
  @JoinColumn(name = "lecturer_id", referencedColumnName = "id")
  private User lecturer;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
  @JoinColumn(name = "practicant_id", referencedColumnName = "id")
  private User practicant;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Group.class)
  @JoinColumn(name = "group_id", referencedColumnName = "id")
  private Group group;

  @OneToMany(mappedBy = "course")
  List<Lesson> lessons;

  @OneToMany(mappedBy = "course")
  List<Task> tasks;

  @Column(columnDefinition = "BIT") 
  private Boolean finished;

  public Course() {}
  
  public long getId() {
    return id;
  }
  public String getCourseName() {
    return courseName;
  }
  public User getLecturer() {
    return lecturer;
  }
  public User getPracticant() {
    return practicant;
  }
  public Group getGroup() {
    return group;
  }
  public List<Lesson> getLessons() {
    return lessons;
  }
  public List<Task> getTasks() {
    return tasks;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }
  public void setLecturer(User lecturer) {
    this.lecturer = lecturer;
  }
  public void setPracticant(User practicant) {
    this.practicant = practicant;
  }
  public void setGroup(Group group) {
    this.group = group;
  }
}

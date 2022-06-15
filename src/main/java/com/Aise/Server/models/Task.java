package com.Aise.Server.models;

import java.sql.Date;
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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name="tasks")
public class Task {
  @Column @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
  @Column private String title = "No title";
  
  @Column private Date deadline;
  @Column private String description = "No description";

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Course.class)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "course_id", referencedColumnName = "id")
  private Course course;

  @OneToMany(mappedBy = "task")
  List<Grade> grades;

  public Task() {}
  
  public long getId() {
    return id;
  }
  public String getTitle() {
    return title;
  }
  public String getDescription() {
    return description;
  }
  public Date getDeadline() {
    return deadline;
  }
  public Course getCourse() {
    return course;
  }
  public List<Grade> getGrades() {
    return grades;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }
  public void setCourse(Course course) {
    this.course = course;
  }
  public void setGrades(List<Grade> grades) {
    this.grades = grades;
  }
}
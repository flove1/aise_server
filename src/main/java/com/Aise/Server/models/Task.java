package com.Aise.Server.models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="tasks")
public class Task {
  @Column @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
  @Column private String title;
  
  @Column private Timestamp deadline;
  @Column private String description;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Course.class)
  @JoinColumn(name = "course_id", referencedColumnName = "id")
  private Course course;

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
  public Timestamp getDeadline() {
    return deadline;
  }
  public Course getCourse() {
    return course;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public void setDeadline(Timestamp deadline) {
    this.deadline = deadline;
  }
  public void setCourse(Course course) {
    this.course = course;
  }
}
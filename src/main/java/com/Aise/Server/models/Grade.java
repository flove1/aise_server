package com.Aise.Server.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="task_grades")
public class Grade {
  @Column @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Task.class)
  @JoinColumn(name = "task_id", referencedColumnName = "id")
  private Task task;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @Column private int grade;
  @Column @Lob private String comment;

  public long getId() {
    return id;
  }
  public Task getTask() {
    return task;
  }
  public User getUser() {
    return user;
  }
  public int getGrade() {
    return grade;
  }
  public String getComment() {
    return comment;
  }

  public void setTask(Task task) {
    this.task = task;
  }
  public void setUser(User user) {
    this.user = user;
  }
  public void setGrade(int grade) {
    this.grade = grade;
  }
  public void setComment(String comment) {
    this.comment = comment;
  }
}
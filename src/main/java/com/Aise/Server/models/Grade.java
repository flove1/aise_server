package com.Aise.Server.models;

import java.sql.Blob;

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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.Nullable;


@Entity
@Table(name="task_grades")
public class Grade {
  @Column @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Task.class)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "task_id", referencedColumnName = "id")
  private Task task;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @Column private Integer grade;
  @Column @Lob private String comment;
  @Column @Lob private Blob submission;
  @Nullable @Column(columnDefinition = "BIT") private Boolean finished = false;
  @Column(columnDefinition = "BIT") private Boolean graded = false;

  public long getId() {
    return id;
  }
  public Task getTask() {
    return task;
  }
  public User getUser() {
    return user;
  }
  public Integer getGrade() {
    return grade;
  }
  public String getComment() {
    return comment;
  }
  public Blob getSubmission() {
    return submission;
  }
  public Boolean getFinished() {
    return finished;
  }
  public Boolean getGraded() {
    return graded;
  }

  public void setTask(Task task) {
    this.task = task;
  }
  public void setUser(User user) {
    this.user = user;
  }
  public void setGrade(Integer grade) {
    this.grade = grade;
  }
  public void setComment(String comment) {
    this.comment = comment;
  }
  public void setSubmission(Blob submission) {
    this.submission = submission;
  }
  public void setFinished(Boolean finished) {
    this.finished = finished;
  }
  public void setGraded(Boolean graded) {
    this.graded = graded;
  }
}
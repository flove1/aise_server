package com.Aise.Server.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="groups")
public class Group{
  @Column @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
  @Column private String groupName;

  @OneToMany(mappedBy = "group")
  List<Course> courses;

  public Group() {}

  public long getId() {
    return id;
  }
  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  public List<Course> getCourses() {
    return courses;
  }
}
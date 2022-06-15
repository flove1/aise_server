package com.Aise.Server.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.Aise.Server.models.enums.Roles;

@Entity
@Table(name="users")
public class User{
  @Column @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
  @Column(unique = true) private String email;
  @Column private String name;
  @Column(columnDefinition="varchar(255) default 'Aitu2022!'") private String password = "Aitu2022!";
  @Column(columnDefinition = "enum('STUDENT','TEACHER','ADMIN')") @Enumerated(EnumType.STRING) private Roles role = Roles.STUDENT;

  public User() {}

  public long getId() {
    return id;
  }
  public String getEmail() {
    return email;
  }
  public String getName() {
    return name;
  }
  public String getPassword() {
    return password;
  }
  public Roles getRole() {
    return role;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public void setRole(Roles role) {
    this.role = role;
  }
}
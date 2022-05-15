package com.Aise.Server.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="rooms")
public class Room {
  @Column @Id private String room;

  public Room() {}
  
  public String getRoom() {
    return room;
  }
  public void setRoom(String room) {
    this.room = room;
  }
}
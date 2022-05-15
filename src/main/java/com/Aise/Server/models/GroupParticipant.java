package com.Aise.Server.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity()
@Table(name="group_participants")
public class GroupParticipant {
  @Column @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Group.class)
  @JoinColumn(name = "group_id", referencedColumnName = "id")
  private Group group;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
  @JoinColumn(name = "participant_id", referencedColumnName = "id")
  private User user;

  public GroupParticipant() {};

  public long getId() {
    return id;
  }
  public Group getGroup() {
    return group;
  }
  public User getUser() {
    return user;
  }

  public void setGroup(Group group) {
    this.group = group;
  }
  public void setUser(User user) {
    this.user = user;
  }
}


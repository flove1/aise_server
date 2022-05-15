package com.Aise.Server.repositories;


import com.Aise.Server.models.Group;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
  Group getByGroupName(String groupName);
  Boolean existsByGroupName(String groupName);
}

package com.Aise.Server.repositories;

import java.util.List;

import com.Aise.Server.models.User;
import com.Aise.Server.models.enums.Roles;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {  
  List<User> getAllByRole(Roles role);
  User getByEmail(String email);
  Boolean existsByEmail(String email);
}

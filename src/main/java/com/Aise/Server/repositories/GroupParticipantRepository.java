package com.Aise.Server.repositories;

import java.util.List;

import com.Aise.Server.models.GroupParticipant;

import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupParticipantRepository extends JpaRepository<GroupParticipant, Long> {  
  List<GroupParticipant> getAllByGroup_Id(Long groupId);
  List<GroupParticipant> getAllByUser_Email(String email);
  List<GroupParticipant> getAllByUser_Id(Long id);
  Boolean existsByGroup_IdAndUser_id(Long groupId, Long userId);
}

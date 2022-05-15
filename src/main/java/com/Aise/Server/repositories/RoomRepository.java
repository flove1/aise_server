package com.Aise.Server.repositories;

import com.Aise.Server.models.Room;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RoomRepository extends JpaRepository<Room, String> {  
  Room getByRoom(String room);
}

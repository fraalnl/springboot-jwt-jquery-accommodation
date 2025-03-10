package edu.tus.service;

import edu.tus.dto.RoomDto;
import edu.tus.model.Room;
import java.util.List;
import java.util.Optional;

public interface RoomService {
	
    List<Room> getAllRooms();
    Optional<Room> getRoomById(Long id); 
    Room addRoom(RoomDto roomDto);
    Optional<Room> updateRoom(Long id, RoomDto roomDto);
    boolean deleteRoom(Long id);
}

package edu.tus.service;

import edu.tus.dto.RoomDto;
import edu.tus.model.Room;
import edu.tus.dao.RoomRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room addRoom(RoomDto roomDto) {
        Room room = new Room();
        room.setName(roomDto.getName());
        room.setEmail(roomDto.getEmail());
        room.setPhone(roomDto.getPhone());
        room.setAddress(roomDto.getAddress());
        room.setEircode(roomDto.getEircode());
        room.setDistance(roomDto.getDistance());
        room.setRoomType(roomDto.getRoomType());
        room.setDurationStay(roomDto.getDurationStay());
        room.setRent(roomDto.getRent());
        room.setBills(roomDto.getBills());
        room.setGenderPreference(roomDto.getGenderPreference());
        room.setAddMessage(roomDto.getAddMessage());
        room.setImage(roomDto.getImage());
        return roomRepository.save(room);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    
    @Override
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }
    
    @Override
    public Optional<Room> updateRoom(Long id, RoomDto roomDto) {
        Optional<Room> roomOpt = roomRepository.findById(id);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setName(roomDto.getName());
            room.setEmail(roomDto.getEmail());
            room.setPhone(roomDto.getPhone());
            room.setAddress(roomDto.getAddress());
            room.setEircode(roomDto.getEircode());
            room.setDistance(roomDto.getDistance());
            room.setRoomType(roomDto.getRoomType());
            room.setDurationStay(roomDto.getDurationStay());
            room.setRent(roomDto.getRent());
            room.setBills(roomDto.getBills());
            room.setGenderPreference(roomDto.getGenderPreference());
            room.setAddMessage(roomDto.getAddMessage());
            room.setImage(roomDto.getImage());
            Room updatedRoom = roomRepository.save(room);
            return Optional.of(updatedRoom);
        } else {
            return Optional.empty();
        }
    }
    
    @Override
    public boolean deleteRoom(Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
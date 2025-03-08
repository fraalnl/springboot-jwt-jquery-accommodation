package edu.tus.controller;

import edu.tus.dto.RoomDto;
import edu.tus.dto.StudentDto;
import edu.tus.model.Room;
import edu.tus.service.RoomService;
import edu.tus.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accommodation")
public class AdminController {

    private final UserService userService;
    private final RoomService roomService;

    public AdminController(final UserService userService, final RoomService roomService) {
        this.userService = userService;
        this.roomService = roomService;
    }

    /**
     * Endpoint for admin to create a student account.
     * Requires a valid admin JWT token.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/students")
    public ResponseEntity<Map<String, String>> createStudent(@RequestBody final StudentDto dto) {
        String token = userService.createStudent(dto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Student account created successfully with role STUDENT");
        response.put("jwtToken", token);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for admin to add a room.
     * Requires a valid admin JWT token.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rooms")
    public ResponseEntity<Map<String, String>> addRoom(@RequestBody final RoomDto roomDto) {
        roomService.addRoom(roomDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Room has been added successfully.");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to list all rooms.
     * Requires a valid admin JWT token.
     */
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> listRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @GetMapping("/rooms/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        Optional<Room> roomOpt = roomService.getRoomById(id);
        return roomOpt.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/rooms/{id}")
    public ResponseEntity<Map<String, Object>> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        Optional<Room> updatedRoomOpt = roomService.updateRoom(id, roomDto);
        if (updatedRoomOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Room updated successfully.");
            response.put("room", updatedRoomOpt.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable Long id) {
        boolean deleted = roomService.deleteRoom(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Room deleted successfully."));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

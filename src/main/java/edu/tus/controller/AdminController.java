package edu.tus.controller;

import edu.tus.dto.RoomDto;
import edu.tus.dto.StudentDto;
import edu.tus.model.Room;
import edu.tus.service.RoomService;
import edu.tus.service.UserService;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accommodation")
public class AdminController {

    private final UserService userService;
    private final RoomService roomService;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    public AdminController(final UserService userService, final RoomService roomService) {
        this.userService = userService;
        this.roomService = roomService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/students")
    public ResponseEntity<EntityModel<Map<String, String>>> createStudent(@RequestBody final StudentDto dto) {
        String token = userService.createStudent(dto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Student account created successfully with role STUDENT");
        response.put("jwtToken", token);
        EntityModel<Map<String, String>> resource = EntityModel.of(response);
        resource.add(linkTo(methodOn(AdminController.class).createStudent(dto)).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rooms")
    public ResponseEntity<EntityModel<Map<String, String>>> addRoom(@RequestBody final RoomDto roomDto) {
        Room room = roomService.addRoom(roomDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Room has been added successfully.");

        EntityModel<Map<String, String>> resource = EntityModel.of(response);
        resource.add(linkTo(methodOn(AdminController.class).getRoom(room.getId())).withSelfRel());
        resource.add(linkTo(methodOn(AdminController.class).listRooms()).withRel("rooms"));

        return ResponseEntity.ok(resource);
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @GetMapping("/rooms")
    public ResponseEntity<CollectionModel<EntityModel<Room>>> listRooms() {
        List<EntityModel<Room>> rooms = roomService.getAllRooms().stream()
            .map(room -> {
                EntityModel<Room> resource = EntityModel.of(room,
                    linkTo(methodOn(AdminController.class).getRoom(room.getId())).withSelfRel(),
                    linkTo(methodOn(AdminController.class).listRooms()).withRel("rooms"));

                // If the user is an ADMIN, add update & delete links
                if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .stream().anyMatch(auth -> auth.getAuthority().equals(ROLE_ADMIN))) {
                    resource.add(linkTo(methodOn(AdminController.class)
                    		.updateRoom(room.getId(), new RoomDto())).withRel("update"));
                    resource.add(linkTo(methodOn(AdminController.class)
                    		.deleteRoom(room.getId())).withRel("delete"));
                }

                return resource;
            })
            .collect(Collectors.toList());

        CollectionModel<EntityModel<Room>> collection = CollectionModel.of(rooms,
            linkTo(methodOn(AdminController.class).listRooms()).withSelfRel());
        return ResponseEntity.ok(collection);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @GetMapping("/rooms/{id}")
    public ResponseEntity<EntityModel<Room>> getRoom(@PathVariable Long id) {
        Optional<Room> roomOpt = roomService.getRoomById(id);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            EntityModel<Room> resource = EntityModel.of(room,
                linkTo(methodOn(AdminController.class).getRoom(id)).withSelfRel(),
                linkTo(methodOn(AdminController.class).listRooms()).withRel("rooms"));

            // If the user is an ADMIN, add update & delete links
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(auth -> auth.getAuthority().equals(ROLE_ADMIN))) {
                resource.add(linkTo(methodOn(AdminController.class).updateRoom(id, new RoomDto())).withRel("update"));
                resource.add(linkTo(methodOn(AdminController.class).deleteRoom(id)).withRel("delete"));
            }

            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/rooms/{id}")
    public ResponseEntity<EntityModel<Map<String, Object>>> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        Optional<Room> updatedRoomOpt = roomService.updateRoom(id, roomDto);
        if (updatedRoomOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Room updated successfully.");
            response.put("room", updatedRoomOpt.get());

            EntityModel<Map<String, Object>> resource = EntityModel.of(response);
            resource.add(linkTo(methodOn(AdminController.class).getRoom(id)).withSelfRel());
            resource.add(linkTo(methodOn(AdminController.class).listRooms()).withRel("rooms")); 

            // Only add update & delete links if the user is an ADMIN
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(auth -> auth.getAuthority().equals(ROLE_ADMIN))) {
                resource.add(linkTo(methodOn(AdminController.class).updateRoom(id, roomDto)).withRel("update"));
                resource.add(linkTo(methodOn(AdminController.class).deleteRoom(id)).withRel("delete"));
            }

            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<EntityModel<Map<String, String>>> deleteRoom(@PathVariable Long id) {
        boolean deleted = roomService.deleteRoom(id);
        if (deleted) {
            Map<String, String> response = Map.of("message", "Room deleted successfully.");
            EntityModel<Map<String, String>> resource = EntityModel.of(response);
            resource.add(linkTo(methodOn(AdminController.class).listRooms()).withRel("rooms"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

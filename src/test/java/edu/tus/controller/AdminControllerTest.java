package edu.tus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tus.dto.RoomDto;
import edu.tus.dto.StudentDto;
import edu.tus.model.Room;
import edu.tus.service.RoomService;
import edu.tus.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({AdminControllerTest.TestConfig.class, AdminControllerTest.TestSecurityConfig.class})
class AdminControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
       UserService userService() {
            return mock(UserService.class);
        }
        @Bean
        RoomService roomService() {
            return mock(RoomService.class);
        }
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
              // Disable CSRF for testing purposes
              .csrf(csrf -> csrf.disable())
              .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
              .httpBasic(Customizer.withDefaults());
            return http.build();
        }
        @Bean
        InMemoryUserDetailsManager userDetailsService() {
            UserDetails admin = User.withUsername("admin")
                .password("{noop}admin123")
                .roles("ADMIN")
                .build();
            return new InMemoryUserDetailsManager(admin);
        }
    }

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoomService roomService;
    
    @Test
    void testCreateStudent() throws Exception {
        StudentDto dto = new StudentDto();
        dto.setUsername("Test Student");
        dto.setEmail("test@student.com");
        String fakeToken = "dummy-token";
        when(userService.createStudent(any(StudentDto.class))).thenReturn(fakeToken);
        
        mockMvc.perform(post("/api/accommodation/students")
            .with(httpBasic("admin", "admin123"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Student account created successfully with role STUDENT"))
            .andExpect(jsonPath("$.jwtToken").value(fakeToken));
    }
    
    @Test
    void testAddRoom() throws Exception {
        RoomDto dto = new RoomDto();
        dto.setName("Test Room");
        dto.setEmail("test@room.com");
        dto.setPhone("123456789");
        dto.setAddress("123 Test Address");
        dto.setEircode("T123");
        dto.setDistance(1.0);
        dto.setRoomType("Single");
        dto.setDurationStay("Long-term");
        dto.setRent(1000);
        dto.setBills("included");
        dto.setGenderPreference("none");
        dto.setAddMessage("no message");
        
        Room room = new Room();
        room.setId(1L);
        room.setName(dto.getName());
        when(roomService.addRoom(any(RoomDto.class))).thenReturn(room);
        
        mockMvc.perform(post("/api/accommodation/rooms")
            .with(httpBasic("admin", "admin123"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message")
                .value("Room has been added successfully."));
    }
    
    @Test
    void testGetRoomFound() throws Exception {
        Room room = new Room();
        room.setId(1L);
        room.setName("Test Room");
        when(roomService.getRoomById(1L)).thenReturn(Optional.of(room));
        
        mockMvc.perform(get("/api/accommodation/rooms/1")
            .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Room"));
    }
    
    @Test
    void testGetRoomNotFound() throws Exception {
        when(roomService.getRoomById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/accommodation/rooms/99")
            .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void testListRooms() throws Exception {
        Room room1 = new Room();
        room1.setId(1L);
        room1.setName("Room One");
        Room room2 = new Room();
        room2.setId(2L);
        room2.setName("Room Two");
        when(roomService.getAllRooms()).thenReturn(Arrays.asList(room1, room2));
        
        mockMvc.perform(get("/api/accommodation/rooms")
            .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded").exists());
    }
    
    @Test
    void testUpdateRoomFound() throws Exception {
        RoomDto dto = new RoomDto();
        dto.setName("Updated Room");
        dto.setEmail("update@room.com");
        dto.setPhone("987654321");
        dto.setAddress("456 Update St");
        dto.setEircode("U456");
        dto.setDistance(2.0);
        dto.setRoomType("Double");
        dto.setDurationStay("Short-term");
        dto.setRent(1500);
        dto.setBills("not included");
        dto.setGenderPreference("male");
        dto.setAddMessage("updated message");
        
        Room updatedRoom = new Room();
        updatedRoom.setId(1L);
        updatedRoom.setName(dto.getName());
        when(roomService.updateRoom(eq(1L), any(RoomDto.class))).thenReturn(Optional.of(updatedRoom));
        
        mockMvc.perform(put("/api/accommodation/rooms/1")
            .with(httpBasic("admin", "admin123"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Room updated successfully."))
            .andExpect(jsonPath("$.room.id").value(1))
            .andExpect(jsonPath("$.room.name").value("Updated Room"));
    }
    
    @Test
    void testDeleteRoomFound() throws Exception {
        when(roomService.deleteRoom(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/accommodation/rooms/1")
            .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message")
                .value("Room deleted successfully."));
    }
    
    @Test
    void testDeleteRoomNotFound() throws Exception {
        when(roomService.deleteRoom(99L)).thenReturn(false);
        mockMvc.perform(delete("/api/accommodation/rooms/99")
            .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNotFound());
    }
}

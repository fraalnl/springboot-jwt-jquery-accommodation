package edu.tus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.tus.dto.RoomDto;
import edu.tus.model.Room;
import edu.tus.model.RoomImage;
import edu.tus.dao.RoomRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Allow lenient stubbing for the entire class
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomServiceImpl roomService; // Explicitly use the concrete type

    private RoomDto sampleRoomDto;

    @BeforeEach
    void setUp() {
        sampleRoomDto = new RoomDto();
        sampleRoomDto.setName("Test Room");
        sampleRoomDto.setEmail("test@example.com");
        sampleRoomDto.setPhone("1234567890");
        sampleRoomDto.setAddress("123 Test St");
        sampleRoomDto.setEircode("TST123");
        sampleRoomDto.setDistance(1.0);
        sampleRoomDto.setRoomType("Single");
        sampleRoomDto.setDurationStay("Long-term");
        sampleRoomDto.setRent(1000);
        sampleRoomDto.setBills("included");
        sampleRoomDto.setGenderPreference("none");
        sampleRoomDto.setAddMessage("Test message");
        // Provide two image URLs
        sampleRoomDto.setImages(Arrays.asList("image1.jpg", "image2.jpg"));
    }

    @Test
    void testAddRoom() {
        // When saving, assign an ID to the room.
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            room.setId(1L);
            return room;
        });

        Room room = roomService.addRoom(sampleRoomDto);
        assertNotNull(room);
        assertEquals("Test Room", room.getName());
        // Verify that images were converted correctly.
        List<RoomImage> images = room.getImages();
        assertNotNull(images);
        assertEquals(2, images.size());
        for (RoomImage image : images) {
            assertEquals(room, image.getRoom());
        }
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testGetRoomById_Found() {
        Room room = new Room();
        room.setId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Optional<Room> result = roomService.getRoomById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetRoomById_NotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Room> result = roomService.getRoomById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteRoom() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        boolean deleted = roomService.deleteRoom(1L);
        assertTrue(deleted);
        verify(roomRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateRoom_PreserveImages() {
        // Create an existing room with one image.
        Room room = new Room();
        room.setId(1L);
        RoomImage existingImage = new RoomImage();
        existingImage.setImageUrl("existing.jpg");
        existingImage.setRoom(room);
        room.setImages(new ArrayList<>(Arrays.asList(existingImage)));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Create a RoomDto with no images (empty list) to simulate update without new images.
        RoomDto updateDto = new RoomDto();
        updateDto.setName("Updated Room");
        updateDto.setEmail("updated@example.com");
        updateDto.setPhone("0987654321");
        updateDto.setAddress("456 Updated St");
        updateDto.setEircode("UPD456");
        updateDto.setDistance(2.0);
        updateDto.setRoomType("Double");
        updateDto.setDurationStay("Short-term");
        updateDto.setRent(2000);
        updateDto.setBills("not included");
        updateDto.setGenderPreference("female");
        updateDto.setAddMessage("Updated message");
        updateDto.setImages(Collections.emptyList()); // No new images provided

        Optional<Room> updatedRoom = roomService.updateRoom(1L, updateDto);
        assertTrue(updatedRoom.isPresent());
        // Check that the name was updated.
        assertEquals("Updated Room", updatedRoom.get().getName());
        // Existing image should be preserved.
        assertEquals(1, updatedRoom.get().getImages().size());
        assertEquals("existing.jpg", updatedRoom.get().getImages().get(0).getImageUrl());
    }
}

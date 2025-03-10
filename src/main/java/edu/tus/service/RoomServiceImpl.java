package edu.tus.service;

import edu.tus.dto.RoomDto;
import edu.tus.model.Room;
import edu.tus.model.RoomImage;
import edu.tus.dao.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;

	public RoomServiceImpl(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	private List<RoomImage> convertImageUrlsToRoomImages(Room room, List<String> imageUrls) {
		List<RoomImage> images = new ArrayList<>();
		if (imageUrls != null) {
			for (String url : imageUrls) {
				RoomImage image = new RoomImage();
				image.setImageUrl(url);
				image.setRoom(room);
				images.add(image);
			}
		}
		return images;
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
		// Convert image URLs to RoomImage entities
		room.setImages(convertImageUrlsToRoomImages(room, roomDto.getImages()));

		return roomRepository.save(room);
	}

	@Transactional
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

	        // Preserve Images if None are Sent in the Update
	        if (roomDto.getImages() != null && !roomDto.getImages().isEmpty()) {
	            updateRoomImages(room, roomDto.getImages()); // Update images if provided
	        }

	        Room updatedRoom = roomRepository.save(room);
	        return Optional.of(updatedRoom);
	    } else {
	        return Optional.empty();
	    }
	}

	//If an error occurs after some images are deleted but before new images are added, the room could end up missing some images.
	//with @Transactional: all changes are rolled back, and the room's images remain unchanged
	@Transactional
	private void updateRoomImages(Room room, List<String> newImageUrls) {
		if (newImageUrls == null) {
			newImageUrls = new ArrayList<>(); //a new empty list is assigned to prevent NullPointerException
		}

		List<RoomImage> currentImages = room.getImages();
		//newImageSet is a HashSet of the new image URLs
		Set<String> newImageSet = new HashSet<>(newImageUrls);

		// Find images to remove
		Iterator<RoomImage> iterator = currentImages.iterator();
		while (iterator.hasNext()) {
			RoomImage existingImage = iterator.next();
			if (!newImageSet.contains(existingImage.getImageUrl())) {
				iterator.remove(); // If an image is not in the new image list, it gets removed
			}
		}

		// Add new images
		for (String newUrl : newImageUrls) {
			//Checks if each new image already exists in currentImages
			boolean exists = currentImages.stream().anyMatch(img -> img.getImageUrl().equals(newUrl));
			if (!exists) { //If it doesn’t exist, a new RoomImage object is created and added to currentImages
				RoomImage newImage = new RoomImage();
				newImage.setImageUrl(newUrl);
				newImage.setRoom(room);
				currentImages.add(newImage);
			}
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
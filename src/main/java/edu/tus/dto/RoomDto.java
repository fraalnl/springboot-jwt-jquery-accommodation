package edu.tus.dto;

import java.util.List;

public class RoomDto {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String eircode;
    private double distance;
    private String roomType;
    private String durationStay;
    private int rent;
    private String bills;
    private String genderPreference;
    private String addMessage;
    private List<String> images;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getEircode() {
        return eircode;
    }
    public void setEircode(String eircode) {
        this.eircode = eircode;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public String getRoomType() {
        return roomType;
    }
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    public String getDurationStay() {
        return durationStay;
    }
    public void setDurationStay(String durationStay) {
        this.durationStay = durationStay;
    }
    public int getRent() {
        return rent;
    }
    public void setRent(int rent) {
        this.rent = rent;
    }
    public String getBills() {
        return bills;
    }
    public void setBills(String bills) {
        this.bills = bills;
    }
    public String getGenderPreference() {
        return genderPreference;
    }
    public void setGenderPreference(String genderPreference) {
        this.genderPreference = genderPreference;
    }
    public String getAddMessage() {
        return addMessage;
    }
    public void setAddMessage(String addMessage) {
        this.addMessage = addMessage;
    }
	public List<String> getImages() {
		return images;
	}
	public void setImages(List<String> images) {
		this.images = images;
	}
}


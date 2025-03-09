-- Insert room records (without an image column)
INSERT INTO rooms (name, email, phone, address, eircode, distance, room_type, duration_stay, rent, bills, gender_preference, add_message)
VALUES
('John', 'john.smith@gmail.com', '0861234567', '12 Main Street, Athlone', 'N37 AB12', 0.8, 'Single', '5 days a week', 500, 'included all', 'no preference', 'no smoking'),
('Emily', 'emily.jones@yahoo.com', '0872345678', '34 Park Avenue, Athlone', 'N37 CD34', 1.5, 'Double', '7 days a week', 600, 'not included', 'female', 'with pets'),
('Michael', 'michael.brown@hotmail.com', '0893456789', '56 River Road, Athlone', 'N37 EF56', 5.3, 'Studio', 'no preference', 550, 'include only heating', 'male', 'no smoking'),
('Sarah', 'sarah.wilson@gmail.com', '0854567890', '78 Lake View, Athlone', 'N37 GH78', 10.0, 'Single', '5 days a week', 480, 'included all', 'no preference', 'with pets'),
('David', 'david.miller@outlook.com', '0865678901', '90 Market Street, Athlone', 'N37 IJ90', 15.7, 'Double', '7 days a week', 700, 'not included', 'male', 'no smoking'),
('Anna', 'anna.davis@gmail.com', '0876789012', '102 College Road, Athlone', 'N37 KL12', 0.5, 'Studio', 'no preference', 450, 'include only heating', 'female', 'with pets'),
('Robert', 'robert.moore@yahoo.com', '0887890123', '114 Garden Lane, Athlone', 'N37 MN34', 2.2, 'Single', '5 days a week', 520, 'included all', 'no preference', 'no smoking'),
('Jessica', 'jessica.taylor@hotmail.com', '0898901234', '126 Queen Street, Athlone', 'N37 OP56', 7.8, 'Double', '7 days a week', 630, 'not included', 'female', 'with pets'),
('Thomas', 'thomas.anderson@gmail.com', '0869012345', '138 King Road, Athlone', 'N37 QR78', 12.3, 'Studio', 'no preference', 580, 'include only heating', 'male', 'no smoking'),
('Laura', 'laura.thomas@outlook.com', '0870123456', '150 Castle Street, Athlone', 'N37 ST90', 19.9, 'Single', '5 days a week', 750, 'included all', 'no preference', 'with pets');

-- Insert two images for each room
INSERT INTO room_image (room_id, image_url) VALUES
(1, 'john_room.jpg'),
(1, 'john_room2.jpg'),
(2, 'emily_room.jpg'),
(2, 'emily_room2.jpg'),
(3, 'michael_room.jpg'),
(3, 'michael_room2.jpg'),
(4, 'sarah_room.jpg'),
(4, 'sarah_room2.jpg'),
(5, 'david_room.jpg'),
(5, 'david_room2.jpg'),
(6, 'anna_room.jpg'),
(6, 'anna_room2.jpg'),
(7, 'robert_room.jpg'),
(7, 'robert_room2.jpg'),
(8, 'jessica_room.jpg'),
(8, 'jessica_room2.jpg'),
(9, 'thomas_room.jpg'),
(9, 'thomas_room2.jpg'),
(10, 'laura_room.jpg'),
(10, 'laura_room2.jpg');

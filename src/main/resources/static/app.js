$(document).ready(function() {
	const baseURL = "http://localhost:8082";
	//JWT token is stored in a variable, will be lost when the page is refreshed
	let token = '';
	let userRole = '';
	let selectedRoomId = null;
	let roomsTable;

	// Handle login form submission
	$("#loginForm").on("submit", function(e) {
		e.preventDefault();
		const username = $("#username").val();
		const password = $("#password").val();
		$.ajax({
			url: baseURL + "/api/auth/login",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({ username: username, password: password }),
			success: function(response) {
				// Clear any previous error message
				$("#loginError").addClass("d-none").html('');
				token = response.token; //return ResponseEntity.ok(Map.of("token", token));
				// if username is 'admin', assign admin role; else, student.
				userRole = (username.toLowerCase() === 'admin') ? 'ADMIN' : 'STUDENT';
				$("#loginSection").hide();
				$("#dashboardSection").removeClass("d-none").removeClass("hidden");
				$("#logoutBtn").removeClass("d-none").removeClass("hidden");
				if (userRole === 'ADMIN') {
					$("#adminActions").removeClass("d-none").removeClass("hidden");
				}
				loadRooms();
			},
			error: function(xhr, status, error) {
				let errorMessage = "Invalid credentials. Please try again.";
				// Try to parse JSON error message if available
				try {
					const response = JSON.parse(xhr.responseText);
					if (response.error) {
						errorMessage = response.error;
					}
				} catch (e) {
					// If not JSON, fallback to the default message.
				}
				$("#loginError").removeClass("d-none").html(errorMessage);
			}
		});
	});

	function loadRooms() {
		$.ajax({
			url: baseURL + "/api/accommodation/rooms",
			method: "GET",
			headers: { "Authorization": "Bearer " + token },
			success: function(response) {
				//console.log("Rooms loaded:", response);
				populateRoomsTable(response); // Pass full response (including HATEOAS links)
			},
			error: function() {
				alert("Failed to load rooms.");
			}
		});
	}

	function populateRoomsTable(response) {
		const rooms = response._embedded ? response._embedded.roomList : response;

		if ($.fn.DataTable.isDataTable('#roomsTable')) {
			$('#roomsTable').DataTable().destroy();
		}

		const tbody = $("#roomsTable tbody");
		tbody.empty();

		rooms.forEach(function(room) {
			// Wrap the buttons in a button group to keep them on one line
			let actions = `<div class="btn-group" role="group" aria-label="Room Actions">`;
			actions += `<button class="btn btn-info btn-sm viewRoomBtn" data-url="${room._links.self.href}">View</button>`;
			if (userRole === 'ADMIN') {
				if (room._links.update) {
					actions += `<button class="btn btn-primary btn-sm updateRoomBtn" data-url="${room._links.update.href}">Edit</button>`;
				}
				if (room._links.delete) {
					actions += `<button class="btn btn-danger btn-sm deleteRoomBtn" data-url="${room._links.delete.href}">Delete</button>`;
				}
			}
			actions += `</div>`;

			const row = `<tr>
	                          <td>${room.name}</td>
	                          <td>${room.address}</td>
	                          <td>${room.distance}</td>
	                          <td>${room.durationStay}</td>
	                          <td>${room.rent}</td>
	                          <td>${actions}</td>
	                     </tr>`;
			tbody.append(row);
		});

		roomsTable = $('#roomsTable').DataTable();
	}

	$(document).on("click", ".viewRoomBtn", function() {
		const roomUrl = $(this).data("url"); // Uses HATEOAS `_links.self.href`

		$.ajax({
			url: roomUrl,
			method: "GET",
			headers: { "Authorization": "Bearer " + token },
			success: function(room) {
				console.log("Room details:", room);
				$("#detailRoomName").text(room.name);
				$("#detailRoomEmail").text(room.email);
				$("#detailRoomPhone").text(room.phone);
				$("#detailRoomAddress").text(room.address);
				$("#detailRoomEircode").text(room.eircode);
				$("#detailRoomDistance").text(room.distance);
				$("#detailRoomType").text(room.roomType);
				$("#detailRoomDurationStay").text(room.durationStay);
				$("#detailRoomRent").text(room.rent);
				$("#detailRoomBills").text(room.bills);
				$("#detailRoomGenderPreference").text(room.genderPreference);
				$("#detailRoomAdditionalMessage").text(room.addMessage);

				// Update two images side by side using the images array from room
				if (room.images && room.images.length >= 1 && room.images[0].imageUrl) {
					$("#detailRoomImage1").attr("src", "/images/" + room.images[0].imageUrl).show();
				} else {
					$("#detailRoomImage1").hide();
				}
				if (room.images && room.images.length >= 2 && room.images[1].imageUrl) {
					$("#detailRoomImage2").attr("src", "/images/" + room.images[1].imageUrl).show();
				} else {
					$("#detailRoomImage2").hide();
				}
				$("#viewRoomModal").modal("show");
			},
			error: function() {
				alert("Failed to fetch room details.");
			}
		});
	});

	function showAlert(message, type) {
		//console.log("showAlert triggered:", message); // Debug: Check if the function is called
		const alertHTML = `
	      <div class="alert alert-${type} alert-dismissible fade show" role="alert">
	        ${message}
	        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	    `;
		$("#alertPlaceholder").html(alertHTML);

		// Auto-dismiss the alert after 3 seconds.
		setTimeout(() => {
			$(".alert").alert('close');
		}, 3000);
	}

	$("#addRoomBtn").on("click", function() {
		$("#addRoomForm")[0].reset();
		$("#addRoomModal").modal("show");
	});

	$("#addRoomForm").on("submit", function(e) {
		e.preventDefault();
		const roomData = {
			name: $("#roomName").val(),
			email: $("#roomEmail").val(),
			phone: $("#roomPhone").val(),
			address: $("#roomAddress").val(),
			eircode: $("#roomEircode").val(),
			distance: parseFloat($("#roomDistance").val()),
			roomType: $("#roomType").val(),
			durationStay: $("#roomDurationStay").val(),
			rent: parseFloat($("#roomRent").val()),
			bills: $("#roomBills").val(),
			genderPreference: $("#roomGenderPreference").val(),
			addMessage: $("#roomAdditionalMessage").val(),
			images: $("#roomImage").val().split(",").map(s => s.trim()).filter(s => s !== "")
		};

		$.ajax({
			url: baseURL + "/api/accommodation/rooms",
			method: "POST",
			contentType: "application/json",
			headers: { "Authorization": "Bearer " + token },
			data: JSON.stringify(roomData),
			success: function() {
				// Remove focus from any element inside the modal
				$(document.activeElement).blur();
				// Hide the modal
				$("#addRoomModal").modal("hide");
				// Use a short timeout to ensure the modal is fully hidden before showing the alert.
				setTimeout(function() {
					loadRooms();
					showAlert("Room added successfully!", "success");
				}, 500);
			},
			error: function() {
				showAlert("Failed to add room.", "danger");
			}
		});
	});

	// Show Update Room Modal with pre-filled data (admin only)
	$(document).on("click", ".updateRoomBtn", function() {
		const updateUrl = $(this).data("url"); // Uses `_links.update.href`

		$.ajax({
			url: updateUrl,
			method: "GET",  // get pre-filled data of the room
			headers: { "Authorization": "Bearer " + token },
			success: function(room) {
				$("#updateRoomId").val(room.id);
				$("#updateRoomName").val(room.name);
				$("#updateRoomEmail").val(room.email);
				$("#updateRoomPhone").val(room.phone);
				$("#updateRoomAddress").val(room.address);
				$("#updateRoomEircode").val(room.eircode);
				$("#updateRoomDistance").val(room.distance);
				$("#updateRoomType").val(room.roomType);
				$("#updateRoomDurationStay").val(room.durationStay);
				$("#updateRoomRent").val(room.rent);
				$("#updateRoomBills").val(room.bills);
				$("#updateRoomGenderPreference").val(room.genderPreference);
				$("#updateRoomAdditionalMessage").val(room.addMessage);
				// Pre-fill the images field with a comma-separated list of image URLs
				if (room.images && room.images.length > 0) {
					$("#updateRoomImage").val(room.images.map(img => img.imageUrl).join(", "));
				} else {
					$("#updateRoomImage").val("");
				}
				$("#updateRoomModal").modal("show");
			},
			error: function() {
				showAlert("Failed to fetch room details", "danger");
			}
		});
	});

	$("#updateRoomForm").on("submit", function(e) {
		e.preventDefault();
		const roomId = $("#updateRoomId").val();
		const imagesString = $("#updateRoomImage").val();
		const imagesArray = imagesString.split(",").map(s => s.trim()).filter(s => s !== "");

		const roomData = {
			name: $("#updateRoomName").val(),
			email: $("#updateRoomEmail").val(),
			phone: $("#updateRoomPhone").val(),
			address: $("#updateRoomAddress").val(),
			eircode: $("#updateRoomEircode").val(),
			distance: parseFloat($("#updateRoomDistance").val()),
			roomType: $("#updateRoomType").val(),
			durationStay: $("#updateRoomDurationStay").val(),
			rent: parseFloat($("#updateRoomRent").val()),
			bills: $("#updateRoomBills").val(),
			genderPreference: $("#updateRoomGenderPreference").val(),
			addMessage: $("#updateRoomAdditionalMessage").val(),
			images: imagesArray
		};

		$.ajax({
			url: baseURL + "/api/accommodation/rooms/" + roomId,
			method: "PUT",
			contentType: "application/json",
			headers: { "Authorization": "Bearer " + token },
			data: JSON.stringify(roomData),
			success: function() {
				$(document.activeElement).blur();
				$("#updateRoomModal").modal("hide");
				setTimeout(function() {
					loadRooms();
					showAlert("Room updated successfully!", "success");
				}, 500);
			},
			error: function() {
				showAlert("Failed to update room", "danger");
			}
		});
	});

	$(document).on("click", ".deleteRoomBtn", function() {
		selectedRoomUrl = $(this).data("url"); // Use dynamic HATEOAS link
		$("#deleteRoomModal").modal("show");
	});

	$("#confirmDeleteRoomBtn").on("click", function() {
		if (selectedRoomUrl) {
			$.ajax({
				url: selectedRoomUrl,
				method: "DELETE",
				headers: { "Authorization": "Bearer " + token },
				success: function() {
					$(document.activeElement).blur();
					$("#deleteRoomModal").modal("hide");
					setTimeout(function() {
						loadRooms();
						showAlert("Room deleted successfully!", "success");
					}, 500);
				},
				error: function() {
					showAlert("Failed to delete room", "danger");
				}
			});
		}
	});

	// Show Create Student Account Modal (admin only)
	$("#createStudentBtn").on("click", function() {
		$("#createStudentForm")[0].reset();
		$("#createStudentModal").modal("show");
	});

	// Handle Create Student Account form submission
	$("#createStudentForm").on("submit", function(e) {
		e.preventDefault();
		const studentData = {
			username: $("#studentUsername").val(),
			email: $("#studentEmail").val(),
			name: $("#studentName").val(),
			password: $("#studentPassword").val()
		};
		$.ajax({
			url: baseURL + "/api/accommodation/students",
			method: "POST",
			contentType: "application/json",
			headers: { "Authorization": "Bearer " + token },
			data: JSON.stringify(studentData),
			success: function(response) {
				$(document.activeElement).blur();
				$("#createStudentModal").modal("hide");
				setTimeout(function() {
					showAlert(response.message, "success");
				}, 500);
			},
			error: function() {
				showAlert("Failed to create student account", "danger");
			}
		});
	});

	// Logout button: Show logout confirmation modal
	$("#logoutBtn").on("click", function() {
		$("#logoutModal").modal("show");
	});

	// Confirm Logout: Clear token, reset userRole, destroy DataTable, and return to login page.
	$("#confirmLogoutBtn").on("click", function() {
		token = '';
		userRole = '';
		if (roomsTable) {
			roomsTable.destroy();
		}
		$("#dashboardSection").addClass("hidden");
		$("#logoutBtn").addClass("hidden");
		$("#adminActions").addClass("hidden");
		$("#loginSection").show();
		$("#logoutModal").modal("hide");
	});
});

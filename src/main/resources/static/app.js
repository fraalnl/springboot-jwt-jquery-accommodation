$(document).ready(function() {
  // Set the base URL to localhost:8082
  const baseURL = "http://localhost:8082";
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
        
        token = response.token;
        // For demo purposes: if username is 'admin', then assign admin role; otherwise, student.
        userRole = (username.toLowerCase() === 'admin') ? 'ADMIN' : 'STUDENT';
        $("#loginSection").hide();
        $("#dashboardSection").removeClass("d-none").removeClass("hidden");
        $("#logoutBtn").removeClass("d-none").removeClass("hidden");
        if (userRole === 'ADMIN') {
          $("#adminActions").removeClass("d-none").removeClass("hidden");
        }
        loadRooms();
      },
      error: function(xhr) {
        // Display a Bootstrap alert with an error message
        $("#loginError")
          .removeClass("d-none")
          .html('<strong>Error:</strong> ' + (xhr.responseJSON && xhr.responseJSON.error ? xhr.responseJSON.error : 'Invalid credentials. Please try again.'));
      }
    });
  });


  // Load rooms from backend
  function loadRooms() {
    $.ajax({
      url: baseURL + "/api/accommodation/rooms",
      method: "GET",
      headers: {
        "Authorization": "Bearer " + token
      },
      success: function(rooms) {
        populateRoomsTable(rooms);
      },
      error: function() {
        alert("Failed to load rooms.");
      }
    });
  }

  // Populate DataTable with selected room fields (modern borderless style)
  function populateRoomsTable(rooms) {
    if ($.fn.DataTable.isDataTable('#roomsTable')) {
      $('#roomsTable').DataTable().destroy();
    }
    const tbody = $("#roomsTable tbody");
    tbody.empty();
    rooms.forEach(function(room) {
      // Always include the View button.
      let actions = `<button class="btn btn-info btn-sm viewRoomBtn" data-id="${room.id}">View</button>`;
      // Only for admin: include Edit and Delete buttons.
      if (userRole === 'ADMIN') {
        actions += ` <button class="btn btn-primary btn-sm updateRoomBtn" data-id="${room.id}">Edit</button>`;
        actions += ` <button class="btn btn-danger btn-sm deleteRoomBtn" data-id="${room.id}">Delete</button>`;
      }
      const row = `<tr>
                    <td>${room.name}</td>
                    <td>${room.address}</td>
                    <td>${room.roomType}</td>
                    <td>${room.durationStay}</td>
                    <td>${room.rent}</td>
                    <td>${actions}</td>
                  </tr>`;
      tbody.append(row);
    });
    roomsTable = $('#roomsTable').DataTable();
  }

  // View Room Details (both admin and student)
  $(document).on("click", ".viewRoomBtn", function() {
    const roomId = $(this).data("id");
    $.ajax({
      url: baseURL + "/api/accommodation/rooms/" + roomId,
      method: "GET",
      headers: {
        "Authorization": "Bearer " + token
      },
      success: function(room) {
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
        $("#detailRoomImage").attr("src", "images/" + room.image);
        $("#viewRoomModal").modal("show");
      },
      error: function() {
        alert("Failed to fetch room details.");
      }
    });
  });

  // Show Add Room Modal (admin only)
  $("#addRoomBtn").on("click", function() {
    $("#addRoomForm")[0].reset();
    $("#addRoomModal").modal("show");
  });

  // Handle Add Room form submission
  $("#addRoomForm").on("submit", function(e) {
    e.preventDefault();
    const roomData = {
      name: $("#roomName").val(),
      email: $("#roomEmail").val(),
      phone: $("#roomPhone").val(),
      address: $("#roomAddress").val(),
      eircode: $("#roomEircode").val(),
      distance: parseFloat($("#roomDistance").val()),
      room_type: $("#roomType").val(),
      duration_stay: $("#roomDurationStay").val(),
      rent: parseFloat($("#roomRent").val()),
      bills: $("#roomBills").val(),
      gender_preference: $("#roomGenderPreference").val(),
      add_message: $("#roomAdditionalMessage").val(),
      image: $("#roomImage").val()
    };
    $.ajax({
      url: baseURL + "/api/accommodation/rooms",
      method: "POST",
      contentType: "application/json",
      headers: {
        "Authorization": "Bearer " + token
      },
      data: JSON.stringify(roomData),
      success: function() {
        $("#addRoomModal").modal("hide");
        loadRooms();
      },
      error: function() {
        alert("Failed to add room.");
      }
    });
  });

  // Show Update Room Modal with pre-filled data (admin only)
  $(document).on("click", ".updateRoomBtn", function() {
    const roomId = $(this).data("id");
    $.ajax({
      url: baseURL + "/api/accommodation/rooms/" + roomId,
      method: "GET",
      headers: {
        "Authorization": "Bearer " + token
      },
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
        $("#updateRoomImage").val(room.image);
        $("#updateRoomModal").modal("show");
      },
      error: function() {
        alert("Failed to fetch room details.");
      }
    });
  });

  // Handle Update Room form submission
  $("#updateRoomForm").on("submit", function(e) {
    e.preventDefault();
    const roomId = $("#updateRoomId").val();
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
      image: $("#updateRoomImage").val()
    };
    $.ajax({
      url: baseURL + "/api/accommodation/rooms/" + roomId,
      method: "PUT",
      contentType: "application/json",
      headers: {
        "Authorization": "Bearer " + token
      },
      data: JSON.stringify(roomData),
      success: function() {
        $("#updateRoomModal").modal("hide");
        loadRooms();
      },
      error: function() {
        alert("Failed to update room.");
      }
    });
  });

  // Handle Delete Room action (admin only)
  $(document).on("click", ".deleteRoomBtn", function() {
    selectedRoomId = $(this).data("id");
    $("#deleteRoomModal").modal("show");
  });

  // Confirm Delete Room
  $("#confirmDeleteRoomBtn").on("click", function() {
    if (selectedRoomId) {
      $.ajax({
        url: baseURL + "/api/accommodation/rooms/" + selectedRoomId,
        method: "DELETE",
        headers: {
          "Authorization": "Bearer " + token
        },
        success: function() {
          $("#deleteRoomModal").modal("hide");
          loadRooms();
        },
        error: function() {
          alert("Failed to delete room.");
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
      headers: {
        "Authorization": "Bearer " + token
      },
      data: JSON.stringify(studentData),
      success: function(response) {
        alert(response.message);
        $("#createStudentModal").modal("hide");
      },
      error: function() {
        alert("Failed to create student account.");
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

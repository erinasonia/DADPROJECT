<?php
include 'db.php'; // Include your database connection script

// Check if POST data exists
$postdata = file_get_contents("php://input");
$request = json_decode($postdata);

if (isset($request->usernameLogin) && isset($request->password)) {
    // Sanitize user input to prevent SQL Injection
    $usernameLogin = mysqli_real_escape_string($conn, $request->usernameLogin);
    $password = mysqli_real_escape_string($conn, $request->password);

    // Query to fetch user role based on provided username and password
    $sql = "SELECT user_id, role FROM users WHERE username = '$usernameLogin' AND password = '$password'";
    $result = $conn->query($sql);

    if ($result) {
        if ($result->num_rows > 0) {
            // User found, fetch user_id and role
            $row = $result->fetch_assoc();
            $user_id = $row['user_id'];
            $role = $row['role'];

            // Return JSON response with user_id and role
            echo json_encode(array("user_id" => $user_id, "role" => $role));
        } else {
            // User not found or credentials are incorrect
            echo json_encode(array("error" => "Invalid username or password"));
        }
    } else {
        // SQL query execution failed
        echo json_encode(array("error" => "Database query failed: " . $conn->error));
    }
} else {
    // Username or password not provided
    echo json_encode(array("error" => "Username or password not provided"));
}

// Close database connection
$conn->close();
?>

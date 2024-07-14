<?php
include 'db.php';

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Retrieve POST data
$postData = json_decode(file_get_contents("php://input"));

// Check if data exists and required fields are present
if (isset($postData->summon_id) && isset($postData->status)) {
    $summon_id = $postData->summon_id;
    $status = $postData->status;

    // Prepare update statement
    $sql = "UPDATE summons SET status = '$status' WHERE summon_id = '$summon_id'";

    // Execute update statement
    if ($conn->query($sql) === TRUE) {
        echo "Status updated successfully";
    } else {
        echo "Error updating status: " . $conn->error;
    }
} else {
    echo "Invalid data received";
}

// Close connection
$conn->close();
?>

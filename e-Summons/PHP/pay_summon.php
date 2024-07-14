<?php
include 'db.php'; // Include your database configuration file

// Get the JSON input from the request body
$input = json_decode(file_get_contents('php://input'), true);
$summonID = $input['summon_id'];

// Prepare and execute the SQL query to update the status to 'Paid'
$stmt = $conn->prepare("UPDATE summons SET status = 'Paid' WHERE summon_id = ?");
$stmt->bind_param("s", $summonID);
$stmt->execute();

if ($stmt->affected_rows > 0) {
    $response = array("success" => "Summon paid successfully.");
} else {
    $response = array("error" => "Failed to pay the summon. Please try again.");
}

// Close the statement and connection
$stmt->close();
$conn->close();

// Return the JSON response
echo json_encode($response);
?>

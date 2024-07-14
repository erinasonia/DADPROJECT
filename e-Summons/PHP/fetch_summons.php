<?php
include 'db.php'; // Include your database connection script

// Get the JSON input from the request body
$input = json_decode(file_get_contents('php://input'), true);
$userID = $input['user_id']; // Retrieve user_id sent from Java application

// Prepare and execute the SQL query
$stmt = $conn->prepare("SELECT s.summon_id, c.case_type, s.amount, s.status 
                        FROM summons s
                        INNER JOIN cases c ON s.case_id = c.case_id
                        WHERE s.user_id = ? AND s.status != 'Paid'");
$stmt->bind_param("s", $userID);
$stmt->execute();
$result = $stmt->get_result();

// Fetch all the rows as an array
$summons = $result->fetch_all(MYSQLI_ASSOC);

// Close the statement and connection
$stmt->close();
$conn->close();

// Return the JSON response
echo json_encode($summons);
?>

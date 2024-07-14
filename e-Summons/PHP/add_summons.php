<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "e_summons";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get the input data
$input = file_get_contents('php://input');
$data = json_decode($input, true);

$summon_number = $data['summon_id'];
$username = $data['username'];
$case_type = $data['case_type'];
$amount = $data['amount'];

// Log input data for debugging
error_log("Input Data: " . print_r($data, true));

// Get user_id based on username
$sql_user = "SELECT user_id FROM users WHERE username = '$username'";
$result_user = $conn->query($sql_user);
if ($result_user->num_rows > 0) {
    $user_id = $result_user->fetch_assoc()['user_id'];
} else {
    echo json_encode(["status" => "error", "message" => "User not found"]);
    $conn->close();
    exit();
}

// Get case_id based on case_type
$sql_case = "SELECT case_id FROM cases WHERE case_type = '$case_type'";
$result_case = $conn->query($sql_case);
if ($result_case->num_rows > 0) {
    $case_id = $result_case->fetch_assoc()['case_id'];
} else {
    echo json_encode(["status" => "error", "message" => "Case type not found"]);
    $conn->close();
    exit();
}

// Log fetched IDs for debugging
error_log("Fetched Data: summon_id=$summon_number, user_id=$user_id, case_id=$case_id, amount=$amount");

// Default status for new summon (assuming 'unpaid')
$status = 'unpaid';

// Insert new summon with status
$sql2 = "INSERT INTO summons (summon_id, user_id, case_id, amount, status) 
         VALUES ('$summon_number', '$user_id', '$case_id', '$amount', '$status')";

if ($conn->query($sql2) === TRUE) {
    echo json_encode(["status" => "success", "message" => "New record created successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Error: " . $sql2 . "<br>" . $conn->error]);
    // Log the exact SQL error for debugging
    error_log("SQL Error: " . $conn->error);
}

$conn->close();
?>


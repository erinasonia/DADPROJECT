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

$summon_id = $data['summon_id'];
$username = $data['username'];
$case_type = $data['case_type'];
$amount = $data['amount'];

// Get user_id based on username
$sql_user = "SELECT user_id FROM users WHERE username = '$username'";
$result_user = $conn->query($sql_user);
$user_id = $result_user->fetch_assoc()['user_id'];

// Get case_id based on case_type
$sql_case = "SELECT case_id FROM cases WHERE case_type = '$case_type'";
$result_case = $conn->query($sql_case);
$case_id = $result_case->fetch_assoc()['case_id'];

// Update summon details
$sql2 = "UPDATE summons SET user_id='$user_id', case_id='$case_id', amount='$amount' WHERE summon_id='$summon_id'";

if ($conn->query($sql2) === TRUE) {
    echo "Record updated successfully";
} else {
    echo "Error: " . $sql2 . "<br>" . $conn->error;
}

$conn->close();
?>

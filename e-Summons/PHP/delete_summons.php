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

// Delete summon
$sql = "DELETE FROM summons WHERE summon_id='$summon_id'";

if ($conn->query($sql) === TRUE) {
    echo "Record deleted successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>

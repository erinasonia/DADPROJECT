<?php
include 'db.php';

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "SELECT summons.summon_id, summons.status, users.username, cases.case_type, summons.amount FROM summons 
        JOIN users ON summons.user_id = users.user_id 
        JOIN cases ON summons.case_id = cases.case_id";
$result = $conn->query($sql);

if (!$result) {
    die("Query failed: " . $conn->error);
}

$summons = array();
while ($row = $result->fetch_assoc()) {
    $summons[] = $row;
}
echo json_encode($summons);

$conn->close();
?>

<?php
include 'db.php';

$sql = "SELECT * FROM cases";
$result = $conn->query($sql);

$cases = array();
while($row = $result->fetch_assoc()) {
    $cases[] = $row;
}
echo json_encode($cases);

$conn->close();
?>

<?php 
require "conn.php";
$user_name = $_POST["user_name"];
$user_pass = $_POST["password"];
$user_level = $_POST["level"];
$check_qry = "SELECT * FROM users WHERE username like '$user_name'";
$result = mysqli_query($conn ,$check_qry);
if(mysqli_num_rows($result) > 0) {
echo "failure user taken";
}
else{
$mysql_qry = "insert into users (username, password, level) values ('$user_name','$user_pass','$user_level')";
if($conn->query($mysql_qry) === TRUE) {
echo "success register";
}
else {
echo "failure register";
}

}
$conn->close();
 
?>
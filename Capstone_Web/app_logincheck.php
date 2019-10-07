<?php
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  $user_id = $_POST['StudentID'];
  $user_pw = $_POST['Password'];

  $hashed_id = hash('sha512', $user_id);
  $hashed_pw = hash('sha512', $user_pw);

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  $sql = "SELECT * FROM student WHERE StudentID='$hashed_id'";
  $resource = mysqli_query( $db, $sql );

  $row = mysqli_fetch_assoc($resource);

  $user_id = $row['StudentID'];
  $user_pw = $row['Password'];

  $row_array = array(
    "user_id" => $user_id,
    "user_pw" => $user_pw
  );

  header('Content-Type: application/json; charset=utf8');
  $json = json_encode($row_array);

  print_r($json);

  mysqli_close($db);
 ?>

<?php
  session_start();
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  mysqli_set_charset($db,"utf8");

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }
  //else {
    //echo "올바른 접근 입니다.";
    //echo "</br>";
  //}

  $user_fingerprint = $_POST['fingerprint'];
  $user_phone = $_POST['phoneNumber'];

  //$auth_status = $_SESSION['auth_status'];
  //echo "auth_status : $auth_status</br>";

  //echo "$user_fingerprint : { $user_fingerprint }";
  //echo "</br>";

  //echo "$user_phone : { $user_phone }";
  //echo "</br>";

  $sql = "SELECT * FROM student WHERE PhoneNumber='$user_phone'";
  $resource = mysqli_query( $db, $sql );

  $row = mysqli_fetch_assoc($resource);
  $num = mysqli_num_rows( $resource );

  // phonenumber exist
  if( $num > 0 ) {
    //echo $row['PhoneNumber'];

    if( $row['FingerPrint'] == '' ) {
      $query = "UPDATE student SET FingerPrint='$user_fingerprint' WHERE PhoneNumber='$user_phone'";
      $resource = mysqli_query( $db, $query );
      //echo "</br> Fingerprint inserted";

      $query = "UPDATE student SET auth_status=1 WHERE PhoneNumber='$user_phone'";
      $resource = mysqli_query( $db, $query );
    }
    else {

      $query = "UPDATE student SET auth_status=1 WHERE PhoneNumber='$user_phone'";
      $resource = mysqli_query( $db, $query );
      /*
      if( $row['FingerPrint'] == $user_fingerprint ) {
          echo "인증에 성공하였습니다.";

      }
      else {
        echo "인증에 실패했습니다.";
      }
      */
    }
  }
  // phonenumber not exist
  else {
    echo "인증에 실패했습니다.";
  }

  $_SESSION['auth_status'] = $auth_status;

  mysql_close($db);
?>

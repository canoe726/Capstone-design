<?php
    require "db_require.php";
    $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

    session_start();

    $id = $_SESSION['StudentID'];
    $hashed_id = hash('sha512', $id);

    $query = "UPDATE student SET auth_status=0 WHERE StudentID='$hashed_id'";
    $resource = mysqli_query( $db, $query );

    session_destroy();

    echo "<script> alert('로그아웃 되었습니다.'); </script>";
?>
<meta http-equiv="refresh" content="0;url=index.php" />

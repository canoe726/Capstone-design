<?php
  require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  $user_id = $_POST["user_id"];
  $hashed_id = hash('sha512', $user_id);

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

  $sql = "SELECT * FROM student WHERE StudentID='$hashed_id'";

  $result=mysqli_query($db,$sql);
  $data = array();
  if($result){

    while($row=mysqli_fetch_array($result)){
        array_push($data,
            array('name'=>$row[1],
            'phone'=>$row[3],
            'email'=>$row[4],
            'finger'=>$row[5]
        ));
    }

    //echo "<pre>"; print_r($data); echo '</pre>';
    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;
  }
  else {
    echo "SQL문 처리중 에러 발생 : ";
    echo mysqli_error($db);
  }

?>

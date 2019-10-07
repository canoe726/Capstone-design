<?php
  session_start();

  $fp = fopen("./voicedata/". $_SESSION['StudentID'] ."/result.txt", 'r');
  $result = fgets($fp);
  fclose($fp);
  echo $result;

  //echo "<script> top.window.open('about:blank','_self').close() </script>";
  //echo "<script> top.window.opener=self </script>";
  //echo "<script> top.self.close() </script>";
?>

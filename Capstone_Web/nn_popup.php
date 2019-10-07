<?php
  $sname = $_POST['sname'];
  $student_name = $sname;

  //echo $student_name;

  $output = shell_exec("D: && cd D:\Bitnami\wampstack-7.1.27-0\apache2\htdocs\Capstone && test_model_nn_voice_classification.py 2>&1". $student_name);
  //print_r($output);
  //echo "<script> top.window.open('about:blank','_self').close() </script>";
  //echo "<script> top.window.opener=self </script>";
  //echo "<script> top.self.close() </script>";
?>

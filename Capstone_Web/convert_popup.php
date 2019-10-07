<?php
  //$sname = $_POST['sname'];
  //$student_name = $sname;

  $length = 4;
  $dir = "./voicedata/";
  $parent_dir = array("14000001","14000002","14000003","14000004");

  $ffmpeg =  'D:\ffmpeg-win64\bin\ffmpeg.exe';
  $input = '';
  $m4a_options = ' -acodec pcm_s16le ';
  $output = '';

  for($i=0; $i<$length; $i++) {
    //echo $parent_dir[$i];
    //echo "<br />";
    $count = 0;
    $handle  = opendir($dir.$parent_dir[$i]);
    $files = array();
    // 디렉터리에 포함된 파일을 저장한다.
    while (false !== ($filename = readdir($handle))) {
        if($filename == "." || $filename == ".."){
            continue;
        }

        // 파일인 경우만 목록에 추가한다.
        if(is_file($dir.$parent_dir[$i] . "/" . $filename)){
            $files[] = $filename;
            if( $filename == "auth_recorded.wav") {
              $count += 1;
            }
        }
    }
    // 핸들 해제
    closedir($handle);

    echo "count : ".$count."<br>";
    if( $count == 0 ) {
      $input = $dir.$parent_dir[$i].'/auth_recorded.m4a';
      $output = $dir.$parent_dir[$i].'/auth_recorded.wav';
      exec($ffmpeg.' -i '.$input.$m4a_options.$output);
    }
  }
  echo "<script> top.window.open('about:blank','_self').close() </script>";
  echo "<script> top.window.opener=self </script>";
  echo "<script> top.self.close() </script>";
/*
  //auth_recorded.m4a -acodec pcm_s16le auth_recorded.wav'
  exec($ffmpeg.' -i '.$input.$m4a_options.$output);
*/
/*
if( $count == 10 ) {
  echo "already conversion finished<br>";
}
else if( $count == 9 ) {
  $input = $dir.$parent_dir[$i].'/auth_recorded.m4a';
  $output = $dir.$parent_dir[$i].'/auth_recorded.wav';
  exec($ffmpeg.' -i '.$input.$m4a_options.$output);
}
else {
  // 파일명을 출력한다.
  foreach ($files as $f) {
    $input = $dir.$parent_dir[$i]."/".$f;
    $out = substr($f, 0, -3);
    $output = $dir.$parent_dir[$i]."/".$out."wav";
    exec($ffmpeg.' -i '.$input.$m4a_options.$output);
  }
}
*/
 ?>

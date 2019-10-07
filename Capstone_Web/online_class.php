<!DOCTYPE HTML>
<?php
	session_start();

	$_SESSION['auth_result'] = 0;

	require "db_require.php";
  $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

  if( !$db ) {
   die( 'MYSQL connect ERROR: ' . mysqli_error($db));
  }

	$user_id = $_SESSION["StudentID"];
  $hashed_id = hash('sha512', $user_id);

	$video_name = "Be_an_artist_right_now";

	$video_source = "./VideoDic/video_file/" . $video_name . ".mp4";
	$keyword_source = "./VideoDic/video_keyword/" . $video_name . ".txt";

	$keywords = array();
	$lines = @file($keyword_source) or $result = "파일을 읽을 수 없습니다.";
	if ($lines != null) {
		for($i = 0; $i < count($lines); $i++) {
			$keywords[$i] = $lines[$i];
		}
	}

	$answer = $keywords[0];
	shuffle($keywords);

	for($i = 0; $i < count($lines); $i++) {
		if( $keywords[$i] == $answer ) {
			$answer_index = ($i + 1);
		}
	}
?>
<!--
	Ion by TEMPLATED
	templated.co @templatedco
	Released for free under the Creative Commons Attribution 3.0 license (templated.co/license)
-->
<html>
	<head>
		<title>온라인 강의 시청</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<!--[if lte IE 8]><script src="js/html5shiv.js"></script><![endif]-->
		<script src="js/jquery.min.js"></script>
		<script src="js/skel.min.js"></script>
		<script src="js/skel-layers.min.js"></script>
		<script src="js/init.js"></script>

    <!-- If you'd like to support IE8 (for Video.js versions prior to v7) -->
		<link href="https://vjs.zencdn.net/7.4.1/video-js.css" rel="stylesheet">
    <script src="https://vjs.zencdn.net/ie8/1.1.2/videojs-ie8.min.js"></script>

		<noscript>
			<link rel="stylesheet" href="css/skel.css" />
			<link rel="stylesheet" href="css/style.css" />
			<link rel="stylesheet" href="css/style-xlarge.css" />
		</noscript>

	  <script language="JavaScript">
	    function chk_radio()
	    {
	      var chkRadio = document.getElementsByName('chk_info');
	      var chk_value = 0;
	      for(var i=0; i<chkRadio.length; i++) {
	        if( chkRadio[i].checked == true ) {
	          chk_value = chkRadio[i].value;
	          break;
	        }
	      }

	      if( chk_value == <?=$answer_index?> ) {
	        location.href="online_auth.php";
					alert("수강 완료 되었습니다.")
	      }
	    }
	  </script>
	</head>

	<body id="top">

		<div id="ViewTimer"></div>

		<!-- Header -->
		<header id="header" class="skel-layers-fixed">
			<h1><a href="index.php">온라인 강의 인증</a></h1>
			<nav id="nav">
				<ul>
					<li><a href="index.php">홈</a></li>
					<li><a href="online_class.php">강의 시청</a></li>
					<li><a href="personal_information.php">개인 정보</a></li>

					<?php if(!isset($_SESSION['StudentID'])) { ?>
					<li><a href="login.php" class="button special">로그인</a></li>
					<?php } else { ?>
					<li><a href="logout.php" class="button special">로그아웃</a></li>
					<?php	} ?>

				</ul>
			</nav>
		</header>

		<!-- Main -->
		<section id="main" class="wrapper style1">
			<header class="major">
				<h2>온라인 강의 시청</h2>
				<p>강의 시청 중 본인 인증 SAMPLE</p>
			</header>

			<div class="container">
				<div class="row">
					<div class="8u skel-cell-important">
						<?php if(!isset($_SESSION['StudentID'])) { ?>
							<p>로그인 후 이용가능합니다.</p>
						<?php } else { ?>
						<section>
							<h2>동영상 SAMPLE</h2>
							<!--<a href="#" class="image fit"><img src="images/pic03.jpg" alt="" /></a>-->
							<video id='lecture' class='video-js' controls preload='auto' width='1024' height='768'
					    poster='./images/video_panel.jpg' data-setup='{}' onclick = "init()">
					      <source src=<?=$video_source?> type='video/mp4'>
					      <source src='MY_VIDEO.webm' type='video/webm'>
					      <p class='vjs-no-js'>
					        To view this video please enable JavaScript, and consider upgrading to a web browser that
					        <a href='https://videojs.com/html5-video-support/' target='_blank'>supports HTML5 video</a>
					      </p>
					    </video>
					    <script src='https://vjs.zencdn.net/7.4.1/video.js'></script>
							<br>
							<p>인터넷 샘플 강의</p>
							<p>시청 중 : 온라인 강의 수강 중 10분 당 랜덤한 시간에 1회 팝업 생성, 조건을 만족해야 강의 수강 가능</p>
							<p>시청 후 : 온라인 강의 내용 중 키워드에 해당하는 부분에 대해서 테스트를 진행, 조건을 만족해야 강의 수강 인정</p>
						</section>
						<?php	} ?>
					</div>
				</div>
				<hr class="major" />
			</div>

			<!-- variable to python script -->
			<form id='val2_nn_pop' method='POST' target='popup_window' action='nn_popup.php'>
		    <input type="hidden" name="sname" value=<?=$user_id?> />
		  </form>

			<form id='val2_db_up_pop' method='POST' target='popup_window' action='db_update_popup.php'>
		    <input type="hidden" name="sname" value=<?=$user_id?> />
		  </form>

			<form id='val2_db_re_pop' method='POST' target='popup_window' action='db_reset_popup.php'>
		    <input type="hidden" name="sname" value=<?=$user_id?> />
		  </form>

			<form id='val2_res_pop' method='POST' target='popup_window' action='res_popup.php'>
		    <input type="hidden" name="sname" value=<?=$user_id?> />
		  </form>

			<form id='val2_convert_pop' method='POST' target='popup_window' action='convert_popup.php'>
		    <input type="hidden" name="sname" value=<?=$user_id?> />
		  </form>

			<?php if(isset($_SESSION['StudentID'])) { ?>
					<div class="popup" id="popup_quiz">
						<h2> 키워드 퀴즈 </h2>
						<h1> 이 강의의 핵심적인 키워드를 선택해 주세요. </h1>
						<br>
				    <p> 키워드 1 : <input type="radio" name="chk_info" value="1">  <?=$keywords[0]?> </p>
						<p> 키워드 2 : <input type="radio" name="chk_info" value="2">  <?=$keywords[1]?> </p>
						<p> 키워드 3 : <input type="radio" name="chk_info" value="3">  <?=$keywords[2]?> </p>
						<p> 키워드 4 : <input type="radio" name="chk_info" value="4">  <?=$keywords[3]?> </p>
				    <input type="button" onclick="chk_radio()" value="확인">
				  </div>
					<div class="dim"></div>
			<?php } ?>

		</section>

		<?php include 'footer.php';?>

		<script type = "text/Javascript" charset="utf-8">
          var count10m = null;
          var myLecture = null;
          var time_check = false;
					var db_check = false;
					var auth_words = new Array();
					var auth_failed = 0;
					var result;
					auth_words[0] = '충무';
					auth_words[1] = '충무';
					auth_words[2] = '충무';
					var rand_number;

          function init() {
              if (myLecture == null) {
                  myLecture = videojs("lecture");
              }
              time_check = false;
              if (count10m == null) {
                  startCount();
              }
							// 키워드 퀴즈
							myLecture.on("ended", function() {
                myLecture.currentTime(0);
                myLecture = null;
								location.href="#popup_quiz";
              });
          }
          function startCount() {
              if (count10m) {
                  stopCount();
              }

							// 단어 목소리 인증
              count10m = new Worker("./js/count_seconds.js");
              count10m.postMessage('start');
              count10m.onmessage = function (e) {
                  myLecture.pause();

                  while (!time_check) {
											rand_number = Math.floor(Math.random() * 3);
                      time_check = confirm("\n인증단어 : " + auth_words[rand_number] + "\n\n목소리 인증해주세요\n\n인증 실패 횟수 : " + auth_failed);
									}
									// maching learning processing during 10~15 seconds
									document.getElementById('val2_convert_pop').submit();
									setTimeout(function() {execute_nn();}, 1000);

									// check result of machine learning in mysql after 25 seconds
									setTimeout(function() {checkDB();}, 15000);

									stopCount();
                  myLecture.play();
                  init();
              };
          }

          function stopCount() {
              if (count10m) {
                  count10m.terminate();
                  count10m = null;
              }
          }

					function execute_nn() {
						document.getElementById('val2_nn_pop').submit();
					}

					function checkDB() {
						document.getElementById('val2_db_up_pop').submit();
						//document.getElementById('val2_res_pop').submit();

						if (window.XMLHttpRequest) { // 모질라, 사파리, IE7+ ...
		            xhr = new XMLHttpRequest();
		        } else if (window.ActiveXObject) { // IE 6 이하
		            xhr = new ActiveXObject("Microsoft.XMLHTTP");
		        }

		        xhr.onreadystatechange = loader;
		        xhr.open("POST", "res_popup.php", false);
		        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		        xhr.send(null);

						xhr.open("POST", "res_popup.php", false);
		        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		        xhr.send(null);

						//alert("result : " + result);

						if( result == 1 ) {
							alert("인증에 성공 하셨습니다.\n현재 인증 실패 횟수 : " + auth_failed);
			      }
						else {
							auth_failed += 1;
							alert("인증에 실패 하셨습니다.\n현재 인증 실패 횟수 : " + auth_failed);
						}

						if( auth_failed == 2 ) {
							document.getElementById('val2_db_re_pop').submit();
							location.href="http://123.109.137.37/Capstone/online_auth.php";
							alert("목소리 인증에 2번 실패하셨습니다.\n 재인증 후 강의 시청해주시길 바랍니다.");
						}
					}

					function loader() {
		        if(xhr.readyState == 4 && xhr.status == 200) { // 응답에 성공하면
		          result = xhr.responseText;
		          document.getElementById("content").innerHTML = result;
		        }
		        else { //응답에 실패하면
		          //alert("error");
		          console.error("error.");
		        }
		      }
    </script>

	</body>
</html>

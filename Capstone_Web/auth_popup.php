<!DOCTYPE HTML>
<html>
	<head>
    <title>온라인 강의 인증</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<!--[if lte IE 8]><script src="js/html5shiv.js"></script><![endif]-->
		<script src="js/jquery.min.js"></script>
		<script src="js/skel.min.js"></script>
		<script src="js/init.js"></script>
		<noscript>
			<link rel="stylesheet" href="css/skel.css" />
			<link rel="stylesheet" href="css/style.css" />
			<link rel="stylesheet" href="css/style-xlarge.css" />
			<link rel="stylesheet" href="css/style-popup.css" />
		</noscript>

		<script language="JavaScript">

			var SetTime = 300;		// 최초 설정 시간(기본 : 초)

			function msg_time() {	// 1초씩 카운트
				m = Math.floor(SetTime / 60) + "분 " + (SetTime % 60) + "초";	// 남은 시간 계산
				var msg = "현재 남은 시간은 <font color='red'>" + m + "</font> 입니다.";
				document.all.ViewTimer.innerHTML = msg;		// div 영역에 보여줌
				SetTime--;					// 1초씩 감소
				if (SetTime < 0) {			// 시간이 종료 되었으면..
					clearInterval(tid);		// 타이머 해제
					alert("종료");
				}
			}
			window.onload = function TimerStart(){ tid=setInterval('msg_time()',1000) };

			var auth_status = 0;
			function close_popup(obj) {
				obj.style.backgroundColor = 'yellow';
				self.close();
			}

		</script>

  </head>

  <body>

		<div id="ViewTimer"></div>

		<p> 모바일 인증 어플리케이션을 실행 시킨 후 지문인증을 해주세요. </p>
		<p> 지문인증이 완료되면 자동으로 화면이 전환됩니다. </p>
		<p> < 지문이 등록되어 있지 않다면 (스마트폰-설정-지문)을 먼저 등록 해주세요. > </p>

		<?php
			$auth_status = $_POST["auth_status"];
			echo "auth : " + $auth_status;
			if( $auth_status === 1 ) {
				header('Location: http://123.109.137.37/Capstone/online_class.php');
			}
		?>

		<!--
		<input type="button" value="지문 인증"/> </br></br>
    <input type="button" value="SMS 인증"/> </br></br>
    <input type="button" value="이메일 인증"/> </br></br> -->

		
  </body>
</html>

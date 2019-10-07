<!DOCTYPE HTML>
<?php
	require "db_require.php";
	session_start();

 	if(isset($_SESSION['StudentID'])) {
		$db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

		$id = $_SESSION['StudentID'];
		$hashed_id = hash('sha512', $id);

		$sql = "SELECT * FROM student WHERE StudentID='$hashed_id'";
		$resource = mysqli_query( $db, $sql );

		$row = mysqli_fetch_assoc($resource);

		if( $row['StudentID'] == $hashed_id )
		{
			/* If success */
			if( $row['auth_status'] == '1' ) {
				echo "<script>document.location.href='http://123.109.137.37/Capstone/online_class.php';</script>";
			}
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
		<noscript>
			<link rel="stylesheet" href="css/skel.css" />
			<link rel="stylesheet" href="css/style.css" />
			<link rel="stylesheet" href="css/style-xlarge.css" />
			<link rel="stylesheet" href="css/style-popup.css" />
		</noscript>
	</head>
	<body id="top">

		<div id="ViewTimer"></div>

		<!-- Header -->
		<header id="header" class="skel-layers-fixed">
			<h1><a href="index.php">온라인 강의 인증</a></h1>
			<nav id="nav">
				<ul>
					<li><a href="index.php">홈</a></li>
					<li><a href="online_auth.php">강의 시청</a></li>
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
						<section  align="center">

							<p> 모바일 인증 어플리케이션을 실행 시킨 후 지문인증을 해주세요. </p>
							<p> 지문인증이 완료한 후 버튼을 누르면, 자동으로 화면이 전환됩니다. </p>
							<p> < 지문이 등록되어 있지 않다면 (스마트폰-설정-지문)을 먼저 등록 해주세요. > </p>
							<input type="button" value="인증 후 강의시청" onclick="location.reload();"/>

						</section>
						<?php	} ?>
					</div>
				</div>
				<hr class="major" />
			</div>
		</section>

		<?php include 'footer.php';?>

	</body>
</html>

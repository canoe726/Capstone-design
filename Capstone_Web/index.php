<!DOCTYPE HTML>
<?php session_start(); ?>
<!--
	Ion by TEMPLATED
	templated.co @templatedco
	Released for free under the Creative Commons Attribution 3.0 license (templated.co/license)
-->
<html>
	<head>
		<title>온라인 강의 인증 홈</title>
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
		</noscript>
	</head>
	<body id="top">

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

		<!-- Banner -->
			<section id="banner">
				<div class="inner">
					<h2>온라인 강의 인증</h2>
					<p>Sejong e-learning authentication SAMPLE</p>
				</div>
			</section>

		<!-- One -->
			<section id="one" class="wrapper style1">
				<header class="major">
					<h2>Something For You</h2>
					<h2>온라인 강의 본인 인증 시스템</h2>
					<p>온라인 강의 수강/시험시 본인 인증을 통한 대리 수강/시험 방지</p>
				</header>
				<div class="container">
					<div class="row">
						<div class="4u">
							<section class="special box">
								<p><img src="images/fingerprint.jpg" width="200" height="200"> </p>
								<h3>온라인 강의 시청 전 모바일 본인 인증</h3>
							</section>
						</div>
						<div class="4u">
							<section class="special box">
								<p><img src="images/voiceprint.jpg" width="300" height="200"> </p>
								<h3>온라인 강의 시청 중 목소리 인증</h3>
							</section>
						</div>
						<div class="4u">
							<section class="special box">
								<p><img src="images/keyword.jpg" width="200" height="200"> </p>
								<h3>온라인 강의 시청 후 키워드 퀴즈</h3>
							</section>
						</div>
					</div>
				</div>
			</section>

			<?php include 'footer.php';?>

	</body>
</html>

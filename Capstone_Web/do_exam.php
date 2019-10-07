<!DOCTYPE HTML>
<?php session_start(); ?>
<!--
	Ion by TEMPLATED
	templated.co @templatedco
	Released for free under the Creative Commons Attribution 3.0 license (templated.co/license)
-->
<html>
	<head>
		<title>온라인 시험 보기</title>
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
						<li><a href="do_exam.php">시험 보기</a></li>
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
					<h2>온라인 시험 보기</h2>
					<p>온라인 시험 본인 인증 SAMPLE</p>
				</header>
				<div class="container">
					<div class="row">

						<div class="8u skel-cell-important">
							<?php if(!isset($_SESSION['StudentID'])) { ?>
								<p>로그인 후 이용가능합니다.</p>
							<?php } else { ?>
							<section>
								<h2>시험 SAMPLE</h2>
								<a href="#" class="image fit"><img src="images/pic03.jpg" alt="" /></a>
								<p>Vis accumsan feugiat adipiscing nisl amet adipiscing accumsan blandit accumsan sapien blandit ac amet faucibus aliquet placerat commodo. Interdum ante aliquet commodo accumsan vis phasellus adipiscing. Ornare a in lacinia. Vestibulum accumsan ac metus massa tempor. Accumsan in lacinia ornare massa amet. Ac interdum ac non praesent. Cubilia lacinia interdum massa faucibus blandit nullam. Accumsan phasellus nunc integer. Accumsan euismod nunc adipiscing lacinia erat ut sit. Arcu amet. Id massa aliquet arcu accumsan lorem amet accumsan commodo odio cubilia ac eu interdum placerat placerat arcu commodo lobortis adipiscing semper ornare pellentesque.</p>
								<p>Amet nibh adipiscing adipiscing. Commodo ante vis placerat interdum massa massa primis. Tempus condimentum tempus non ac varius cubilia adipiscing placerat lorem turpis at. Aliquet lorem porttitor interdum. Amet lacus. Aliquam lobortis faucibus blandit ac phasellus. In amet magna non interdum volutpat porttitor metus a ante ac neque. Nisi turpis. Commodo col. Interdum adipiscing mollis ut aliquam id ante adipiscing commodo integer arcu amet blandit adipiscing arcu ante.</p>
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

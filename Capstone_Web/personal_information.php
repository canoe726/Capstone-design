<!DOCTYPE HTML>
<?php
  require "db_require.php";
	session_start();

	if(isset($_SESSION['StudentID'])) {
    $id = $_SESSION['StudentID'];
    $hashed_id = hash('sha512', $id);

    $db = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);
    if( !$db ) {
     die( 'MYSQL connect ERROR: ' . mysqli_error($db));
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
		<title>온라인 인증 개인정보</title>
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

		<!-- Main -->
			<section id="main" class="wrapper style1">
				<header class="major">
					<h2>개인정보</h2>
				</header>
				<div class="container">
					<section>
						<h2>개인 정보 내역</h2>
						<p>Personal information.</p>
					</section>

					<!-- if login status -->
					<?php if(isset($_SESSION['StudentID'])) {

						$sql = "SELECT * FROM student WHERE StudentID='$hashed_id'";
						$resource = mysqli_query( $db, $sql );

						$row = mysqli_fetch_assoc($resource);

						if( $row['StudentID'] == $hashed_id )
						{
							echo "StudentName : {$row['StudentName']}";
							echo "<br/>\n";
							echo "PhoneNumber : {$row['PhoneNumber']}";
							echo "<br/>\n";
							echo "Email : {$row['Email']}";
							echo "<br/>\n";
						}
					}
					?>
					</br>

					<a class="image fit"><img src="images/banner.jpg" alt="" /></a>
					<hr class="major" />
				</div>
			</section>

			<?php include 'footer.php';?>

	</body>
</html>

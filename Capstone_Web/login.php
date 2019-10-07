<!DOCTYPE HTML>
<?php session_start(); ?>
<html>
  <head>
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
<body>
  <!-- Header -->
    <header id="header" class="skel-layers-fixed">
      <h1><a href="index.php">온라인 강의 인증</a></h1>
      <nav id="nav">
        <ul>
          <li><a href="index.php">홈</a></li>
          <li><a href="online_auth.php">강의 시청</a></li>
          <li><a href="personal_information.php">개인 정보</a></li>
          <li><a href="login.php" class="button special">로그인</a></li>
        </ul>
      </nav>
    </header>

<!-- Banner -->
  <section id="banner">
    <div class="inner">
      <h2>온라인 강의 인증</h2>
      <p>Sejong e-learning authentication SAMPLE</p>
      <h3>로그인</h3>
    </div>
  </section>

  </br>
  <h1>로그인</h1>

  <?php if(!isset($_SESSION['StudentID'])) { ?>

  <form method="POST" action="signup_check.php">
      <p>학번을 입력하세요.</p>
      <input type="text" name="StudentID" placeholder="학번을 입력하세요. 8자리 ex)12345678">

      <p>비밀번호를 입력하세요.</p>
      <input type="password" name="Password" placeholder="비밀번호를 입력하세요.">
      </br>
      <input type="submit" name="login" value="로그인">
      <input type="submit" name="signup" value="회원가입">
  </form>

  <?php } else {
      $user_id = $_SESSION['StudentID'];
      echo "<p><strong>($user_id)</strong>님은 이미 로그인하고 있습니다. ";
      echo "<a href=\"index.php\">[돌아가기]</a> ";
      echo "<a href=\"logout.php\">[로그아웃]</a></p>";
  } ?>
</body>
</html>

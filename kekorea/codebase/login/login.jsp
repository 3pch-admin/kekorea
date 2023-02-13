<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<script type="text/javascript" src="/Windchill/jsp/asset/axisj/jquery/jquery-1.12.3.min.js"></script>
<title>국제 PLM 로그인 페이지</title>
<style type="text/css">
embed{
	min-width: 412px;
}

html {
 height: 100%;
}

body {
height: 100%;
/* 	margin-top: -70px; */
 	background-repeat: no-repeat; 
	background-size: cover;
 	background-position: top; 
/* 	background-position: 54px 100px; */
}

.login_table{
	text-align: center;
	margin: auto;
	height: 100%;
}

#id,#pw{
	width: 120px;
}

.loginBtn{
	display: inline-block;
	border-radius: 5px;
	text-align: center;
	cursor: pointer;
	height: 24px;
	color: white;
	font-weight: bold;
	letter-spacing: 1px;
	line-height: 26px !important;
	-webkit-line-height: 26px !important;
	padding: 0px 10px 25px 10px;
	border: 1px solid #00c192;
	background: #00c192;
}

/* @media (min-width:399px) {
	object {
		height: 300px;
	}
	object embed {
		height: 300px;
	}
} */

@media (min-width:599px) {
	object {
		height: 350px;
	}
	object embed {
		height: 350px;
	}

	 img {
		width:100%;
	}
	
	.rew{
	width: 140px;
} 
}

@media (min-width:799px) {
	object {
		height: 532px;
	}
	object embed {
		height: 532px;
	}
	.rew{
	width: 160px;
} 
	
}

@media (min-width:999px) {
	object {
		height: 632px;
	}
	object embed {
		height: 632px;
	}
	.rew{
	width: 180px;
} 
	
}

body {
background-image: url("/Windchill/login/login.jpg");
}

</style>

<script type="text/javascript">
$(document).ready(function(){
	var key = getCookie("key");
	$("input[name=j_username]").val(key);

	$("input[name=j_username]").focus();
	
	if($("input[name=j_username]").val()!=""){
		$("input[name=id_check]").attr("checked", true);
	}
	
	$("input[name=id_check]").change(function(){
		if($("input[name=id_check]").is(":checked")){
			setCookie("key",$("input[name=j_username]").val(),7);
		}else{
			deleteCookie("key");
		}
	});
	
	$("input[name=j_username]").keyup(function(){
		if($("input[name=id_check]").is(":checked")){
			setCookie("key",$("input[name=j_username]").val(),7);
		}
	});
});

function setCookie(cookieName, value, exdays){
	var exdate = new Date();
	exdate.setDate(exdate.getDate()+exdays);
	var cookieValue = escape(value) + ((exdays==null)?"":"; expires="+ exdate.toGMTString());
	document.cookie = cookieName + "=" + cookieValue;
}

function deleteCookie(cookieName){
	var expireDate = new Date();
	expireDate.setDate(expireDate.getDate()-1);
	document.cookie = cookieName + "=" + "; expires="+expireDate.toGMTString();
}

function getCookie(cookieName){
	cookieName = cookieName + "=";
	var cookieData = document.cookie;
	var start = cookieData.indexOf(cookieName);
	var cookieValue = '';
	if(start != -1){
		start += cookieName.length;
		var end = cookieData.indexOf(';',start);
		if(end==-1)end=cookieData.length;
		cookieValue = cookieData.substring(start, end);
	}
	return unescape(cookieValue);
}
</script>
</head>
<body>
<form method="POST" action="j_security_check" id="login">
				<table class="login_table">
				<tr>
					<td style="height: 480px;">&nbsp;</td>
				</tr>
				<tr>
<!-- 					<td class="rew" width="120"> -->
<!-- 						<img src="/Windchill/jsp/images/logo2.gif" class="logoImg"  height="40px;"> -->
<!-- 					</td> -->
					<td>
						<table>
							<tr>
								<td>
									<input type="text" name="j_username" id="id" value="wcadmin">
								</td>
							</tr>
							<tr>
								<td>
									<input type="password" name="j_password" id="pw" value="wcadmin1">
								</td>
							</tr>
						</table>
					</td>
					<td>
						<input type="submit" name="loginBtn" class="loginBtn" value="로그인">
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
					</td>
					<td>
						<input type="checkbox" class="id_check" name="id_check"  id="id_check"><label>아이디 저장</label>
					</td>
				</tr>
				</table>
</form>
</body>
</html>
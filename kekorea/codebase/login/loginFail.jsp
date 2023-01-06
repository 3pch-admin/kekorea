<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>로그인실패</title>
<script type="text/javascript">
	function loginPage() {
		alert("로그인 실패");
		history.go(-1);
	}
</script>
</head>
<body onLoad="loginPage();">

</body>
</html>
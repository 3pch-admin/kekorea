<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String code = (String) request.getAttribute("code");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
</head>
<body>
	<iframe src="/Windchill/plm/korea/chart?code=<%=code%>" style="height: 320px;"></iframe>
	<iframe src="/Windchill/plm/korea/list?code=<%=code%>" style="height: 450px;"></iframe>
</body>
</html>
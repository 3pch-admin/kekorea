<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String code = (String) request.getAttribute("code");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<!-- 차트 프레임 -->
	<iframe src="/Windchill/plm/korea/chart?code=<%=code%>" style="height: 400px;"></iframe>
	<!-- 차트 프레임 //-->
	
	<!-- 리스트 프레임 -->
	<iframe src="/Windchill/plm/korea/list?code=<%=code%>" style="height: 330px;"></iframe>
	<!-- 리스트 프레임 //-->
</body>
</html>
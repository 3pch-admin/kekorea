<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><tiles:insertAttribute name="title" ignore="true"></tiles:insertAttribute></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
</head>
<body>
	<form method="post">
		<table id="popup_table">
			<tr>
				<tiles:insertAttribute name="body">
				</tiles:insertAttribute>
			</tr>
			<tr>
				<td><tiles:insertAttribute name="footer"></tiles:insertAttribute></td>
			</tr>
		</table>
<!-- 		<a href="#" id="toTop" title="맨 위로"></a> -->
	</form>
</body>
</html>
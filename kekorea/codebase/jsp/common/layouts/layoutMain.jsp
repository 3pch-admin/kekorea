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
		<tiles:insertAttribute name="header"></tiles:insertAttribute>
		<table id="content_table">
			<colgroup id="colGroups">
				<col width="210">
				<col width="*">
			</colgroup>
			<tr>
				<tiles:insertAttribute name="body"></tiles:insertAttribute>
			</tr>
			<tr>
				<td colspan="3"><tiles:insertAttribute name="footer"></tiles:insertAttribute></td>
			</tr>
		</table>
<!-- 		<a href="#" id="toTop" title="맨 위로"></a> -->
	</form>
</body>
</html>
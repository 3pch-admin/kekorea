<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><tiles:insertAttribute name="title" ignore="false"></tiles:insertAttribute></title>
<!-- CSS 공통 모듈 -->
<%@include file="/extcore/include/css.jsp"%>
<!-- 스크립트 공통 모듈 -->
<%@include file="/extcore/include/script.jsp"%>
</head>
<body class="fixed-sidebar">
	<form>
		<div id="wrapper">
			<tiles:insertAttribute name="body"></tiles:insertAttribute>
		</div>
		<div id="loading">
			<img src="/Windchill/extcore/images/loading.gif">
		</div>
		<script type="text/javascript">
// 			const cover = new ax5.ui.mask();
// 			function openLayer() {
// 				document.getElementById("loading_layer").style.display = "block";
// 				cover.open();
// 			}

// 			function closeLayer() {
// 				document.getElementById("loading_layer").style.display = "none";
// 				cover.close();
// 			}
		</script>
	</form>
</body>
</html>
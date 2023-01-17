<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="/Windchill/jsp/css/bootstrap.css">
<link rel="stylesheet" href="/Windchill/jsp/css/font-awesome.css">
<link rel="stylesheet" href="/Windchill/jsp/js/plugins/gritter/jquery.gritter.css">
<link rel="stylesheet" href="/Windchill/jsp/css/animate.css">
<link rel="stylesheet" href="/Windchill/jsp/css/layout.css">
<title><tiles:insertAttribute name="title" ignore="false"></tiles:insertAttribute></title>
</head>
<body class="fixed-sidebar">
	<div id="wrapper">
		<tiles:insertAttribute name="header"></tiles:insertAttribute>
		<div id="page-wrapper" class="gray-bg dashbard-1">
			<tiles:insertAttribute name="body"></tiles:insertAttribute>
			<tiles:insertAttribute name="footer"></tiles:insertAttribute>
		</div>
	</div>
	<script type="text/javascript" src="/Windchill/jsp/asset/axisj/jquery/jquery-1.12.3.min.js"></script>
	<script src="/Windchill/jsp/js/plugins/popper.min.js"></script>
	<script src="/Windchill/jsp/js/plugins/bootstrap.js"></script>
	<script src="/Windchill/jsp/js/plugins/metisMenu/jquery.metisMenu.js"></script>
	<script src="/Windchill/jsp/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
	<script src="/Windchill/jsp/js/plugins/inspinia.js"></script>
	<script src="/Windchill/jsp/js/plugins/gritter/jquery.gritter.min.js"></script>
	<script type="text/javascript">
		function redirect(url) {
			document.getElementById("content").src = "/Windchill/plm" + url;
		}
	</script>
</body>
</html>
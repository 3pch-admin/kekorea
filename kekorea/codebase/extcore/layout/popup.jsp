<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><tiles:insertAttribute name="title" ignore="false"></tiles:insertAttribute></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<link rel="stylesheet" href="/Windchill/extcore/component/ax5ui-mask/dist/ax5mask.css">
<script type="text/javascript" src="/Windchill/extcore/component/ax5core/dist/ax5core.min.js"></script>
<script type="text/javascript" src="/Windchill/extcore/component/ax5ui-mask/dist/ax5mask.min.js"></script>
</head>
<body class="fixed-sidebar">
	<form>
		<div id="wrapper">
			<tiles:insertAttribute name="body"></tiles:insertAttribute>
		</div>
		<div id="loading_layer">
			<img src="/Windchill/extcore/images/loading.gif">
		</div>
		<script type="text/javascript">
			const _mask = new ax5.ui.mask();
			function openLayer() {
				document.getElementById("loading_layer").style.display = "block";
				_mask.open();
			}

			function closeLayer() {
				document.getElementById("loading_layer").style.display = "none";
				_mask.close();
			}
		</script>
	</form>
</body>
</html>
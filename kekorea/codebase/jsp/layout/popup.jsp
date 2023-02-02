<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<title><tiles:insertAttribute name="title" ignore="false"></tiles:insertAttribute></title>
</head>
<body class="fixed-sidebar">
	<form>
		<div id="wrapper">
			<tiles:insertAttribute name="body"></tiles:insertAttribute>
		</div>
		<div id="loading_layer">
			<img src="/Windchill/jsp/images/loading.gif">
		</div>
		<script type="text/javascript">
			const cover = new ax5.ui.mask();
			function open() {
				$("#loading_layer").show();
				cover.open();
			}

			function close() {
				$("#loading_layer").hide();
				cover.close();
			}
		</script>
	</form>
</body>
</html>
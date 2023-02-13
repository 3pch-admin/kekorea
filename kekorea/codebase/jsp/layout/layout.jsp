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
<link rel="stylesheet" href="/Windchill/jsp/asset/ax5ui-mask/dist/ax5mask.css">
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
	<div id="loading_layer">
		<img src="/Windchill/jsp/images/loading.gif">
	</div>
	<script type="text/javascript" src="/Windchill/jsp/asset/axisj/jquery/jquery-1.12.3.min.js"></script>
	<script type="text/javascript" src="/Windchill/jsp/asset/ax5core/dist/ax5core.min.js"></script>
	<script type="text/javascript" src="/Windchill/jsp/asset/ax5ui-mask/dist/ax5mask.min.js"></script>
	<script src="/Windchill/jsp/js/plugins/popper.min.js"></script>
	<script src="/Windchill/jsp/js/plugins/bootstrap.js"></script>
	<script src="/Windchill/jsp/js/plugins/metisMenu/jquery.metisMenu.js"></script>
	<script src="/Windchill/jsp/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
	<script src="/Windchill/jsp/js/plugins/inspinia.js"></script>
	<script src="/Windchill/jsp/js/plugins/gritter/jquery.gritter.min.js"></script>
	<script type="text/javascript">
		const cover = new ax5.ui.mask();
		let iframe = document.getElementById("content");
		function moveToPage(obj, url) {
			let menu = document.getElementsByClassName("menu");
			for (let i = 0; i < menu.length; i++) {
				menu[i].classList.remove("menu");
			}
			openLayer();
			obj.classList.add("menu");
			iframe.src = "/Windchill/plm" + url;
		}

		let toggle = document.getElementById("toggle");
		toggle.addEventListener("click", function() {
			let open = $(this).data("open");
			if (open === "open") {
				$(this).data("open", "close");
				iframe.style.width = "1890px";
			} else {
				$(this).data("open", "open");
				iframe.style.width = "1670px";
			}
		})

		function openLayer() {
			$("#loading_layer").show();
			cover.open();
		}

		function closeLayer() {
			$("#loading_layer").hide();
			cover.close();
		}
	</script>
</body>
</html>
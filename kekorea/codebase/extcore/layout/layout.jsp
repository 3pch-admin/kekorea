<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="/Windchill/extcore/css/bootstrap.css">
<link rel="stylesheet" href="/Windchill/extcore/css/fonts/font-awesome.css">
<link rel="stylesheet" href="/Windchill/extcore/css/jquery.gritter.css">
<link rel="stylesheet" href="/Windchill/extcore/css/animate.css">
<link rel="stylesheet" href="/Windchill/extcore/css/layout.css">
<link rel="stylesheet" href="/Windchill/extcore/component/ax5ui-mask/dist/ax5mask.css">
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
		<img src="/Windchill/extcore/images/loading.gif">
	</div>
	<script type="text/javascript" src="/Windchill/extcore/component/axisj/jquery/jquery-1.12.3.min.js"></script>
	<script type="text/javascript" src="/Windchill/extcore/component/ax5core/dist/ax5core.min.js"></script>
	<script type="text/javascript" src="/Windchill/extcore/component/ax5ui-mask/dist/ax5mask.min.js"></script>
	<script src="/Windchill/extcore/js/plugins/popper.min.js"></script>
	<script src="/Windchill/extcore/js/plugins/bootstrap.js"></script>
	<script src="/Windchill/extcore/js/plugins/metisMenu/jquery.metisMenu.js"></script>
	<script src="/Windchill/extcore/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
	<script src="/Windchill/extcore/js/plugins/inspinia.js"></script>
	<script src="/Windchill/extcore/js/plugins/gritter/jquery.gritter.min.js"></script>
	<script type="text/javascript">
		const cover = new ax5.ui.mask();
		const iframe = document.getElementById("content");
		const subLoc = document.getElementById("subLoc");
		function moveToPage(obj, url, loc) {
			const menu = document.getElementsByClassName("menu");
			for (let i = 0; i < menu.length; i++) {
				menu[i].classList.remove("menu");
			}
			obj.classList.add("menu");
			subLoc.innerHTML = loc;
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
			document.getElementById("loading_layer").style.display = "block";
			cover.open();
		}

		function closeLayer() {
			document.getElementById("loading_layer").style.display = "none";
			cover.close();
		}

		function logout() {
			if (!confirm("로그아웃 하시겠습니까?")) {
				return false;
			}
			document.execCommand("ClearAuthenticationCache");
			document.location.href = "/Windchill/login/logout.jsp";
		}

		function _logout() {
			document.execCommand("ClearAuthenticationCache");
			document.location.href = "/Windchill/login/logout.jsp";
		}

		function index() {
			cover.open();
			document.location.href = "/Windchill/plm/index";
		}

		function _popup(url) {
			const popW = 800;
			const popH = 200;
			const left = (screen.width - popW) / 2;
			const top = (screen.height - popH) / 2;
			const panel = window.open("/Windchill/plm" + url, "", "top=" + top + ", left=" + left + ", height=" + popH + ", width=" + popW);
			return panel;
		}
	</script>
</body>
</html>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<CommonCode> maks = (ArrayList<CommonCode>) request.getAttribute("maks");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<!-- CSS 공통 모듈 -->
<%@include file="/extcore/include/css.jsp"%>
<!-- 스크립트 공통 모듈 -->
<%@include file="/extcore/include/script.jsp"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<!-- AUIGrid 리스트페이지에서만 사용할 js파일 -->
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1"></script>
</head>
<body>
	<form>
		<div id="tabs">
			<ul>
				<%
					for(CommonCode mak : maks) {
						String code = mak.getCode();
						String name = mak.getName();
				%>
				<li>
					<a href="/Windchill/plm/korea/info?code=<%=code%>"><%=name %></a>
				</li>
				<%
					}
				%>
			</ul>
		</div>
		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				$("#tabs").tabs();
			})
		</script>
	</form>
</body>
</html>
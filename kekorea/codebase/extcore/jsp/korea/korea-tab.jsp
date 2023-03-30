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
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
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
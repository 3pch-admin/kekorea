<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.project.dto.ProjectDTO"%>
<%@page import="e3ps.project.task.dto.TaskDTO"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ProjectDTO data = (ProjectDTO) request.getAttribute("data");
TaskDTO dto = (TaskDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
</head>
<body>
	<form>

		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				parent.parent.closeLayer();
			})
		</script>
	</form>
</body>
</html>
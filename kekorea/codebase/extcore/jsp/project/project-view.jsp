<%@page import="e3ps.project.dto.ProjectDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ProjectDTO dto = (ProjectDTO) request.getAttribute("dto");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
</head>
<body>
	<form>

		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">참조작번</th>
				<td colspan="3">
					<jsp:include page="/extcore/include/reference-project-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="true" name="multi" />
						<jsp:param value="" name="obj" />
						<jsp:param value="150" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>

		<br>

		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">특이사항</th>
				<td colspan="3">
					<jsp:include page="/extcore/include/issue-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="true" name="multi" />
						<jsp:param value="" name="obj" />
						<jsp:param value="150" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				_createAUIGrid_(_columns_);
				AUIGrid.resize(_myGridID_);
				_createAUIGrid(_columns);
				AUIGrid.resize(_myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(_myGridID_); 
				AUIGrid.resize(_myGridID); 
			});
		</script>
	</form>
</body>
</html>
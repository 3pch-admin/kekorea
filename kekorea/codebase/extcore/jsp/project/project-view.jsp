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
<!-- CSS 공통 모듈 -->
<%@include file="/extcore/include/css.jsp"%>
<!-- 스크립트 공통 모듈 -->
<%@include file="/extcore/include/script.jsp"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
</head>
<body>
	<form>

		<!-- 참조작번 그리드 -->
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

		<!-- 여백 -->
		<br>

		<!-- 특이사항 그리드 -->
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
				// 참조 작번
				_createAUIGrid_(_columns_);
				AUIGrid.resize(_myGridID_);
				// 특이사항 추가 그리드
				_createAUIGrid(_columns);
				AUIGrid.resize(_myGridID);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(_myGridID_); // 참조 작번
				AUIGrid.resize(_myGridID); // 특이사항
			});
		</script>
	</form>
</body>
</html>
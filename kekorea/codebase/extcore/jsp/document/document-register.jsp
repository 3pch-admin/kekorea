<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<table class="create-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req">결재 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="AXInput width-700">
				</td>
			</tr>
			<tr>
				<th class="req">결재 문서</th>
				<td>
					<jsp:include page="/extcore/include/document-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="true" name="multi" />
						<jsp:param value="" name="obj" />
						<jsp:param value="300" name="height" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req">결재</th>
				<td>
					<jsp:include page="/extcore/include/register-include.jsp">
						<jsp:param value="300" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			// 등록
			function create() {
			}

			document.addEventListener("DOMContentLoaded", function() {
				_createAUIGrid(_columns);
				_createAUIGrid_(_columns_); // 결재
			});

			window.addEventListener("resize", function() {
				AUIGrid.bind(_myGridID);
			});
		</script>
	</form>
</body>
</html>
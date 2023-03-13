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
					<input type="button" value="결재등록" title="결재등록" onclick="registerLine();">
				</td>
			</tr>
		</table>

		<table class="create-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">결재 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="AXInput width-700">
				</td>
			</tr>
			<tr>
				<th class="req lb">결재 문서</th>
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
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/include/register-include.jsp">
						<jsp:param value="300" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			// 등록
			function registerLine() {
				const url = getCallUrl("/document/register");
				const params = new Object();
				const _addRows = AUIGrid.getAddedRowItems(_myGridID); // 문사
				const _addRows_ = AUIGrid.getAddedRowItems(_myGridID_); // 결재
				params.name = document.getElementById("name").value;
				params._addRows = _addRows;
				params._addRows_ = _addRows_;
				console.log(params);
// 				call(url, params, function(data) {
// 					alert(data.msg);
// 					if (data.result) {
// 						document.location.href = getCallUrl("/workspace/approval");
// 					}
// 				})
			}

			document.addEventListener("DOMContentLoaded", function() {
				_createAUIGrid(_columns);
				_createAUIGrid_(_columns_); // 결재
			});

			window.addEventListener("resize", function() {
				AUIGrid.bind(_myGridID);
				AUIGrid.bind(_myGridID_);
			});
		</script>
	</form>
</body>
</html>
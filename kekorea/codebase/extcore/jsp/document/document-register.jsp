<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						문서결재 등록
					</div>
				</td>
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
					<input type="text" name="name" id="name" class="width-700">
				</td>
			</tr>
			<tr>
				<th class="req lb">결재 의견</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="6"></textarea>
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
						<jsp:param value="250" name="height" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/include/register-include.jsp">
						<jsp:param value="250" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
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
				_createAUIGrid_(_columns_); 
			});

			window.addEventListener("resize", function() {
				AUIGrid.bind(_myGridID);
				AUIGrid.bind(_myGridID_);
			});
		</script>
	</form>
</body>
</html>
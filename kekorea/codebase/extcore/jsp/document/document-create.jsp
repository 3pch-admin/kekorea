<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="location" id="location">
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="자가결재" title="자가결재" class="blue">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="130">
		<col width="700">
		<col width="130">
		<col width="700">
	</colgroup>
	<tr>
		<th class="req">저장위치</th>
		<td colspan="3" class="indent5">
			<span id="loc"><%=DocumentHelper.ROOT%></span>
			<input type="button" value="폴더선택" title="폴더선택" class="blue" onclick="folder();">
		</td>
	</tr>
	<tr>
		<th class="req lb">문서제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500">
		</td>
		<th class="req lb">문서번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="AXInput width-500" readonly="readonly">
		</td>
	</tr>
	<tr>
		<th class="lb">설명</th>
		<td colspan="3" class="indent5">
			<textarea name="description" id="description" rows="6" cols=""></textarea>
		</td>
	</tr>
	<tr>
		<th class="req lb">관련부품</th>
		<td colspan="3">
			<jsp:include page="/extcore/include/part-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
				<jsp:param value="true" name="multi" />
				<jsp:param value="" name="obj" />
				<jsp:param value="150" name="height" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/secondary-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">결재</th>
		<td colspan="3">
			<jsp:include page="/extcore/include/register-include.jsp">
				<jsp:param value="200" name="height" />
			</jsp:include>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function folder() {
		const location = decodeURIComponent("/Default/문서");
		const url = getCallUrl("/folder?location=" + location + "&container=product&method=setNumber&multi=false");
		popup(url, 500, 600);
	}

	function setNumber(item) {
		const url = getCallUrl("/document/setNumber");
		const params = new Object();
		params.oid = item.oid;
		call(url, params, function(data) {
			document.getElementById("loc").innerHTML = item.location;
			document.getElementById("location").value = item.location;
			document.getElementById("number").value = data.number;
		})
	}

	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		_createAUIGrid(_columns);
		_createAUIGrid_(_columns_);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});
</script>
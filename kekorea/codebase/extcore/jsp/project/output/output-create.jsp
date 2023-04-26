<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%
String toid = (String) request.getAttribute("toid");
String poid = (String) request.getAttribute("poid");
String location = (String) request.getAttribute("location");
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="toid" id="toid" value="<%=toid%>">
<input type="hidden" name="poid" id="poid" value="<%=poid%>">
<input type="hidden" name="location" id="location" value="<%=location%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				산출물 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="500">
		<col width="150">
		<col width="500">
	</colgroup>
	<tr>
		<th class="req lb">산출물 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-300">
		</td>
		<th>진행율</th>
		<td class="indent5">
			<input type="number" name="progress" id="progress" class="width-300" value="0" maxlength="3">
		</td>
	</tr>
	<tr>
		<th class="req lb">KEK 작번</th>
		<td colspan="5">
			<jsp:include page="/extcore/jsp/common/project-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="6"></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
				<jsp:param value="" name="oid" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">결재</th>
		<td colspan="3">
			<jsp:include page="/extcore/jsp/common/approval-register.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function create() {
		const params = new Object();
		const url = getCallUrl("/output/create");
		const name = document.getElementById("name");
		const progress = document.getElementById("progress").value;
		const description = document.getElementById("description").value;
		const addRows9 = AUIGrid.getGridData(myGridID9);
		const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
		const toid = document.getElementById("toid").value;
		const poid = document.getElementById("poid").value;
		const location = document.getElementById("location").value;
		if (isNull(name.value)) {
			alert("산출물 제목을 입력하세요.");
			name.focus();
			return false;
		}

		if (addRows9.length === 0) {
			alert("하나 이상의 작번이 추가되어야 합니다.");
			return false;
		}

		if (addRows8.length === 0) {
			alert("결재선을 지정하세요.");
			_register();
			return false;
		}

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		params.name = name.value;
		params.description = description;
		params.progress = Number(progress);
		params.addRows9 = addRows9
		params.primarys = toArray("primarys");
		params.location = location;
		params.toid = toid;
		params.poid = poid;
		toRegister(params, addRows8);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function auiBeforeRemoveRow(event) {
		const item = event.items[0];
		const oid = document.getElementById("poid").value;
		if (item.oid === oid) {
			alert("기준 작번은 제거 할 수 없습니다.");
			return false;
		}
		return true;
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid9(columns9);
		createAUIGrid8(columns8);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		AUIGrid.addRow(myGridID9, <%=list%>);
		AUIGrid.bind(myGridID9, "beforeRemoveRow", auiBeforeRemoveRow);
		document.getElementById("name").focus();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
	});
</script>
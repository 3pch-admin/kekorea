<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="location" id="location">
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create('false');">
			<input type="button" value="자가결재" title="자가결재" class="blue" onclick="create('false')">
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
		<th class="req lb">저장위치</th>
		<td class="indent5">
			<span id="loc"><%=DocumentHelper.DOCUMENT_ROOT%></span>
			<input type="button" value="폴더선택" title="폴더선택" class="blue" onclick="folder();">
		</td>
		<th class="req">도번선택</th>
		<td class="indent5">
			<input type="text" name="numberRule" id="numberRule" class="width-300" readonly="readonly">
		</td>
	</tr>
	<tr>
		<th class="req lb">문서제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-500">
		</td>
		<th class="req">문서번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="width-500" readonly="readonly">
		</td>
	</tr>
	<tr>
		<th class="lb">설명</th>
		<td colspan="3" class="indent5">
			<textarea name="description" id="description" rows="6"></textarea>
		</td>
	</tr>
	<tr>
		<th class="req lb">관련부품</th>
		<td colspan="3">
			<jsp:include page="/extcore/jsp/common/part-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
				<jsp:param value="" name="oid" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">결재</th>
		<td colspan="5">
			<jsp:include page="/extcore/jsp/common/approval-register.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
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
		const url = getCallUrl("/doc/setNumber");
		const params = new Object();
		params.loc = item.location;
		call(url, params, function(data) {
			document.getElementById("loc").innerHTML = item.location;
			document.getElementById("location").value = item.location;
			document.getElementById("number").value = data.number;
		})
	}

	function create(isSelf) {
		const name = document.getElementById("name");
		const number = document.getElementById("number").value;
		const description = document.getElementById("description").value;
		const location = document.getElementById("location").value;
		const addRows7 = AUIGrid.getAddedRowItems(myGridID7);
		const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
		if (isNull(name.value)) {
			alert("문서제목을 입력하세요.");
			name.focus();
			return false;
		}

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		const params = new Object();
		const url = getCallUrl("/doc/create");
		params.name = name.value;
		params.number = number;
		params.isSelf = !!isSelf;
		params.description = description;
		params.location = location;
		params.primarys = toArray("primarys");
		toRegister(params, addRows8);
		console.log(params);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		});
	};

	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("name").focus();
		// DOM이 로드된 후 실행할 코드 작성
		createAUIGrid7(columns7);
		createAUIGrid8(columns8);
		AUIGrid.resize(myGridID7);
		AUIGrid.resize(myGridID8);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID7);
		AUIGrid.resize(myGridID8);
	});
</script>
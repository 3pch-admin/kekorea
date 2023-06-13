<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT %>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				문서 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create('false');">
			<input type="button" value="자가결재" title="자가결재" class="blue" onclick="create('true')">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="600">
		<col width="150">
		<col width="600">
	</colgroup>
	<tr>
		<th class="req lb">저장위치</th>
		<td class="indent5" colspan="3">
			<span id="loc"><%=DocumentHelper.DOCUMENT_ROOT%></span>
			<input type="button" value="폴더선택" title="폴더선택" class="blue" onclick="folder();">
		</td>
	</tr>
	<tr>
		<th class="req lb">문서제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-400">
		</td>
		<th class="req">문서번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" readonly="readonly" class="width-200">
		</td>
	</tr>
	<tr>
		<th class="lb">도번</th>
		<td colspan="3">
			<jsp:include page="/extcore/jsp/common/numberRule-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>	
	<tr>
		<th class="lb">설명</th>
		<td colspan="3" class="indent5">
			<textarea name="description" id="description" rows="6"></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">관련부품</th>
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
			<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
				<jsp:param value="" name="oid" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">결재</th>
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
		const addRows11 = AUIGrid.getAddedRowItems(myGridID11);
		const primarys = toArray("primarys");

		if(location === "/Default/문서") {
			alert("문서 저장위치를 선택하세요.");
			folder();
			return false;
		}
		
		if (isNull(name.value)) {
			alert("문서제목을 입력하세요.");
			name.focus();
			return false;
		}
		
// 		if(addRows11.length === 0) {
// 			alert("도번을 추가하세요.");
// 			insert11();
// 			return false;
// 		}
		
		if(primarys.length === 0) {
			alert("첨부파일을 선택하세요.");
			return false;
		}

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		const params = new Object();
		const url = getCallUrl("/doc/create");
		params.name = name.value;
		params.number = number;
		params.self = JSON.parse(isSelf);
		params.description = description;
		params.location = location;
		params.addRows7 = addRows7;
		params.addRows11 = addRows11;
		params.primarys = primarys;
		toRegister(params, addRows8);
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
		// DOM이 로드된 후 실행할 코드 작성
		createAUIGrid7(columns7);
		createAUIGrid11(columns11);
		createAUIGrid8(columns8);
		AUIGrid.resize(myGridID7);
		AUIGrid.resize(myGridID11);
		AUIGrid.resize(myGridID8);
		document.getElementById("name").focus();
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID7);
		AUIGrid.resize(myGridID11);
		AUIGrid.resize(myGridID8);
	});
</script>
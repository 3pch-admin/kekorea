<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
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
		<th class="req">수배표 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500">
		</td>
		<th>회의록 템플릿 선택</th>
		<td class="indent5">
			<select name="engType" id="engType" class="width-200">
				<option value="">선택</option>
				<option value="기계">기계</option>
				<option value="전기">전기</option>
			</select>
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">KEK 작번</font>
		</th>
		<td colspan="3">
			<jsp:include page="/extcore/include/project-include.jsp">
				<jsp:param value="create" name="mode" />
				<jsp:param value="true" name="multi" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req">내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="6"></textarea>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/secondary-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>
<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
			<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT_NO",
		dataType : "string",
		width : 80,
	}, {
		dataField : "unitName",
		headerText : "UNIT NAME",
		dataType : "string",
		width : 120
	}, {
		dataField : "partNo",
		headerText : "부품번호",
		dataType : "string",
		style : "underline",
		width : 130,
	}, {
		dataField : "partName",
		headerText : "부품명",
		dataType : "string",
		width : 200,
	}, {
		dataField : "standard",
		headerText : "규격",
		dataType : "string",
		width : 250,
	}, {
		dataField : "maker",
		headerText : "MAKER",
		dataType : "string",
		width : 130,
	}, {
		dataField : "customer",
		headerText : "거래처",
		dataType : "string",
		width : 130,
	}, {
		dataField : "quantity",
		headerText : "수량",
		dataType : "numeric",
		width : 60,
		formatString : "###0",
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
	}, {
		dataField : "unit",
		headerText : "단위",
		dataType : "string",
		width : 80,
	}, {
		dataField : "price",
		headerText : "단가",
		dataType : "numeric",
		width : 120,
	}, {
		dataField : "currency",
		headerText : "화폐",
		dataType : "string",
		width : 60,
	}, {
		dataField : "won",
		headerText : "원화금액",
		dataType : "numeric",
		width : 120,
	}, {
		dataField : "partListDate",
		headerText : "수배일자",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
		editable : false
	}, {
		dataField : "exchangeRate",
		headerText : "환율",
		dataType : "numeric",
		width : 80,
		formatString : "#,##0.0000"
	}, {
		dataField : "referDrawing",
		headerText : "참고도면",
		dataType : "string",
		width : 120,
	}, {
		dataField : "classification",
		headerText : "조달구분",
		dataType : "string",
		width : 120,
	}, {
		dataField : "note",
		headerText : "비고",
		dataType : "string",
		width : 250,
	} ];

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			selectionMode : "multipleCells",
			editable : true,
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			// 복사 후 편집 이벤트 발생하는 속성
			$compaEventOnPaste : true,
			showRowCheckColumn : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		readyHandler();
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
	}

	function auiCellEditEndHandler() {

	}

	function readyHandler() {
		let item = new Object();
		item.partListDate = new Date();
		AUIGrid.addRow(myGridID, item, "first");
	}

	// 등록
	function create() {

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		const params = new Object();
		const _addRows = AUIGrid.getAddedRowItems(_myGridID); // 프로젝트
		const addRows = AUIGrid.getAddedRowItems(myGridID); // 프로젝트
		const url = getCallUrl("/partlist/create");
		params.name = document.getElementById("name").value;
		params.description = document.getElementById("description").value;
		params.engType = document.getElementById("engType").value;
		params._addRows = _addRows;
		params.addRows = addRows;
		params.secondarys = toArray("secondarys");
		console.log(params);
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				// 실패시 처리할 부분..
			}
		})
	}

	// 행 추가
	function addRow() {
		let item = new Object();
		item.partListDate = new Date();
		AUIGrid.addRow(myGridID, item, "last");
	}

	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		// 작번 추가 그리드
		_createAUIGrid(_columns);
		createAUIGrid(columns);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>
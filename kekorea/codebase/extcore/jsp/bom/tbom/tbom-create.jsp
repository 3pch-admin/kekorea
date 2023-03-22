<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.org.Department"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				T-BOM 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<!-- create table -->
<table class="create-table">
	<colgroup>
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th class="req lb">T-BOM 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-500">
		</td>
	</tr>
	<tr>
		<th class="lb">설명</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="5" cols=""></textarea>
		</td>
	</tr>
	<tr>
		<th class="req lb">KEK 작번</th>
		<td>
			<jsp:include page="/extcore/include/project-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
				<jsp:param value="true" name="multi" />
				<jsp:param value="" name="obj" />
				<jsp:param value="200" name="height" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td class="indent5">
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
<div id="grid_wrap" style="height: 400px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "ok",
		headerText : "검증",
		width : 80,
		renderer : {
			type : "CheckBoxEditRenderer",
		},
		editable : false
	}, {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "numeric",
		width : 100,
		formatString : "###0",
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
			maxlength : 3,
		},
	}, {
		dataField : "code",
		headerText : "중간코드",
		dataType : "string",
		width : 130,
		editable : false
	}, {
		dataField : "keNumber",
		headerText : "부품번호",
		dataType : "string",
		width : 150,
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		width : 270,
		editable : false
	}, {
		dataField : "model",
		headerText : "KokusaiModel",
		dataType : "string",
		editable : false
	}, {
		dataField : "qty",
		headerText : "QTY",
		dataType : "numeric",
		width : 100,
		formatString : "###0",
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
	}, {
		dataField : "unit",
		headerText : "UNIT",
		dataType : "string",
		width : 130
	}, {
		dataField : "provide",
		headerText : "PROVIDE",
		dataType : "string",
		width : 130
	}, {
		dataField : "discontinue",
		headerText : "DISCONTINUE",
		dataType : "string",
		width : 200
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			selectionMode : "multipleCells",
			editable : true,
			fillColumnSizeMode : true,
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			// 복사 후 편집 이벤트 발생하는 속성
			$compaEventOnPaste : true,
			showRowCheckColumn : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		readyHandler();
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
	}

	// 행 삭제
	function deleteRow() {
		const checked = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	};

	function readyHandler() {
		const item = new Object();
		item.createdDate = new Date();
		AUIGrid.addRow(myGridID, item, "first");
	}

	// 행 추가
	function addRow() {
		const item = new Object();
		item.createdDate = new Date();
		AUIGrid.addRow(myGridID, item, "last");
	}

	// 등록
	function create() {

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}
		const params = new Object();
		const addRows = AUIGrid.getAddedRowItems(myGridID); // 도면 일람표
		const _addRows = AUIGrid.getAddedRowItems(_myGridID); // 프로젝트
		params.name = document.getElementById("name").value;
		params.addRows = addRows;
		params._addRows = _addRows;
		const url = getCallUrl("/tbom/create");
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

	function auiCellEditEndHandler(event) {
		const dataField = event.dataField;
		const number = event.item.keNumber;
		if (dataField === "keNumber") {
			const url = getCallUrl("/tbom/getData?number=" + number);
			call(url, null, function(data) {
				if (data.ok) {
					const item = {
						ok : data.ok,
						name : data.name,
						keNumber : data.keNumber,
						lotNo : data.lotNo,
						oid : data.oid,
						code : data.code,
						model : data.model,
						createdDate : new Date()
					}
					AUIGrid.updateRow(myGridID, item, event.rowIndex);
				} else {
					const item = {
						ok : data.ok,
					}
				}
			}, "GET");
		}
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		_createAUIGrid(_columns);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>
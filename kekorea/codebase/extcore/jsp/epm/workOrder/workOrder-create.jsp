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
		<col width="*">
	</colgroup>
	<tr>
		<th class="req">도면 일람표 명</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500">
		</td>
	</tr>
	<tr>
		<th class="req">KEK 작번</th>
		<td class="indent5">
			<jsp:include page="/extcore/include/project-include.jsp">
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req">작업 내용</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="8"></textarea>
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
		dataType : "boolean",
		width : 80,
		renderer : {
			type : "CheckBoxEditRenderer",
		},
	}, {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
		editable : false,
		style : "left indent10"
	}, {
		dataField : "number",
		headerText : "DWG. NO",
		dataType : "string",
		width : 200
	}, {
		dataField : "current",
		headerText : "CURRENT VER",
		dataType : "string",
		width : 130,
		editable : false
	}, {
		dataField : "rev",
		headerText : "REV",
		dataType : "string",
		width : 130,
		editable : false
	}, {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "string",
		width : 100
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
		editable : false
	}, {
		dataField : "note",
		headerText : "NOTE",
		dataType : "string",
		width : 350,
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
		let checked = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checked.length - 1; i >= 0; i--) {
			let rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	};

	function readyHandler() {
		let item = new Object();
		item.createdDate = new Date();
		AUIGrid.addRow(myGridID, item, "first");
	}

	function auiCellEditEndHandler(event) {
		let dataField = event.dataField;
		if (dataField === "number") {
			let number = event.item.number;
			let url = getCallUrl("/workOrder/getData?number=" + number);
			call(url, null, function(data) {
				if (data.ok) {
					let item = {
						ok : data.ok,
						name : data.name,
						rev : data.rev,
						current : data.current,
						lotNo : data.lotNo,
						oid : data.oid,
						createdDate : new Date()
					}
					AUIGrid.updateRow(myGridID, item, event.rowIndex);
				} else {
					let item = {
						ok : data.ok,
					}
				}
			}, "GET");
		}
	}

	// 행 추가
	function addRow() {
		let item = new Object();
		item.createdDate = new Date();
		AUIGrid.addRow(myGridID, item, "last");
	}

	// 등록
	function create() {

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}
		let params = new Object();
		let addRows = AUIGrid.getAddedRowItems(myGridID); // 도면 일람표
		let _addRows = AUIGrid.getAddedRowItems(_myGridID); // 프로젝트
		params.name = document.getElementById("name").value;
		params.addRows = addRows;
		params._addRows = _addRows;
		let url = getCallUrl("/workOrder/create");
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

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		_createAUIGrid(_columns)
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>
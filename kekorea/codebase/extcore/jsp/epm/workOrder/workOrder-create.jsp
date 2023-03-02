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
		<th>
			<font class="req">도면 일람표 명</font>
		</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput">
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">KEK 작번</font>
		</th>
		<td class="indent5">
			<jsp:include page="/jsp/include/include-project.jsp"></jsp:include>
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">작업 내용</font>
		</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="8"></textarea>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td class="indent5">
			<jsp:include page="/extcore/jsp/common/include/include-secondary.jsp" />
		</td>
	</tr>
</table>
<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="행 추가" title="행 추가" class="blut" onclick="addRow();">
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
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			selectionMode : "multiCells",
			editable : true,
			fillColumnSizeMode : true,
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			// 복사 후 편집 이벤트 발생하는 속성
			$compaEventOnPaste : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
	}

	function auiCellEditEndHandler(event) {
		let dataField = event.dataField;
		if (dataField === "number") {
			let number = event.item.number;
			let url = getCallUrl("/workOrder/getData?number=" + number);
			call(url, null, function(data) {
				console.log(data);
				if (data.ok) {
					let item = {
						ok : data.ok,
						name : data.name,
						rev : data.rev,
						current : data.current,
						lotNo : data.lotNo
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

	function auiReadyHandler() {
		for (let i = 0; i < 20; i++) {
			let item = new Object();
			item.createdDate = new Date();
			AUIGrid.addRow(myGridID, item, "first");
		}
	}

	// 행 추가
	function addRow() {
		let item = new Object();
		item.createdDate = new Date();
		AUIGrid.addRow(myGridID, item, "first");
	}

	// 행 삭제
	function deleteRow() {
		let checked = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = 0; i < checked.length; i++) {
			let rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	}

	// 등록
	function create() {

	}

	$(function() {
		createAUIGrid(columns);
	})

	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>도면 일람표 등록</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" id="createBtn" title="등록">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<table class="create_table">
	<colgroup>
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th>
			<font class="req">도면 일람표 명</font>
		</th>
		<td>
			<input type="text" name="name" id="name" class="AXInput wid500">
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">KEK 작번</font>
		</th>
		<td colspan="3">
			<jsp:include page="/jsp/include/include-project.jsp"></jsp:include>
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">작업 내용</font>
		</th>
		<td>
			<textarea name="description" id="description" rows="5"></textarea>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td>
			<jsp:include page="/jsp/include/include-secondary.jsp" />
		</td>
	</tr>
</table>
<table class="btn_table">
	<tr>
		<td class="left">
			<input type="button" value="추가" id="addRowBtn" title="추가">
			<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
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
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			showRowCheckColumn : true, // 엑스트라 체크 박스 사용 여부
			editable : true,
			enableSorting : false,
			fillColumnSizeMode : true,
			selectionMode : "multipleCells",
			$compaEventOnPaste : true
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		auiReadyHandler();
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
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
			console.log(item);
			AUIGrid.addRow(myGridID, item, "first");
		}
	}

	$(function() {

		createAUIGrid(columns);

		$("#closeBtn").click(function() {
			self.close();
		})

		$("#addRowBtn").click(function() {
			let item = new Object();
			item.createdDate = new Date();
			AUIGrid.addRow(myGridID, item, "first");
		})

		$("#deleteRowBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID, rowIndex);
			}
		})

		$("#createBtn").click(function() {
			let url = getCallUrl("/project/create");
			let params = new Object();
			params = form(params, "create_table");
			openLayer();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					opener.loadGridData();
					self.close();
				} else {
					closeLayer();
				}
			}, "POST");
		})
	})

	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>
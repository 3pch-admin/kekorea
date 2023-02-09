<%@page import="org.json.JSONArray"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray drawingCompanys = (JSONArray) request.getAttribute("drawingCompanys");
JSONArray writtenDocuments = (JSONArray) request.getAttribute("writtenDocuments");
JSONArray businessSectors = (JSONArray) request.getAttribute("businessSectors");
JSONArray classificationWritingDepartment = (JSONArray) request.getAttribute("classificationWritingDepartment");
%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>KEK 도번 등록</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="추가" class="redBtn" id="addRowBtn" title="추가">
			<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
			<input type="button" value="등록" id="createBtn" title="등록">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 490px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	let businessSectors =
<%=businessSectors%>
	let drawingCompanys =
<%=drawingCompanys%>
	let classificationWritingDepartment =
<%=classificationWritingDepartment%>
	let writtenDocuments =
<%=writtenDocuments%>
	const columns = [ {
		dataField : "last",
		headerText : "최종도번",
		dataType : "string",
		width : 120,
		editable : false,
	}, {
		dataField : "number",
		headerText : "도면번호",
		dataType : "string",
		editable : false,
		width : 120
	}, {
		dataField : "name",
		headerText : "도면명",
		dataType : "string",
		width : 250
	}, {
		dataField : "businessSector",
		headerText : "사업부문",
		dataType : "string",
		width : 200,
		renderer : {
			type : "DropDownListRenderer",
			list : businessSectors, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value" // value 에 해당되는 필드명
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = businessSectors.length; i < len; i++) {
				if (jsonList[i]["key"] == value) {
					retStr = businessSectors[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		}
	}, {
		dataField : "drawingCompany",
		headerText : "도면생성회사",
		dataType : "string",
		width : 150,
		renderer : {
			type : "DropDownListRenderer",
			list : drawingCompanys, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value" // value 에 해당되는 필드명
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = drawingCompanys.length; i < len; i++) {
				if (jsonList[i]["key"] == value) {
					retStr = drawingCompanys[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		}
	}, {
		dataField : "department",
		headerText : "작성부서구분",
		dataType : "string",
		width : 150,
		renderer : {
			type : "DropDownListRenderer",
			list : classificationWritingDepartment, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value", // value 에 해당되는 필드명
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = classificationWritingDepartment.length; i < len; i++) {
				if (jsonList[i]["key"] == value) {
					retStr = classificationWritingDepartment[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		}
	}, {
		dataField : "document",
		headerText : "작성문서구분",
		dataType : "string",
		width : 150,
		renderer : {
			type : "DropDownListRenderer",
			list : writtenDocuments, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value" // value 에 해당되는 필드명
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = writtenDocuments.length; i < len; i++) {
				if (jsonList[i]["key"] == value) {
					retStr = writtenDocuments[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		}
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "numeric",
		width : 120,
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
	}, ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showRowCheckColumn : true, // 체크 박스 출력,
			fillColumnSizeMode : true,
			editable : true,
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
		AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
	}

	function auiAddRowHandler(event) {
		let selected = AUIGrid.getSelectedIndex(myGridID);
		if (selected.length <= 0) {
			return;
		}

		let rowIndex = selected[0];
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "name");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
		AUIGrid.openInputer(myGridID);
	}

	function auiCellEditEndHandler(event) {
		let dataField = event.dataField;
		let item = event.item;
		if (dataField === "drawingCompany") {
			let drawingCompany = item.drawingCompany;
			item.number = drawingCompany;
			AUIGrid.updateRow(myGridID, item, event.rowIndex);
		}

		if (dataField === "department") { // 작성부서
			let department = item.department;
			item.number = item.number + department;
			AUIGrid.updateRow(myGridID, item, event.rowIndex);
		}

		if (dataField === "document") { // 작성문서
			let document = item.document;
			item.number = item.number + document;
			AUIGrid.updateRow(myGridID, item, event.rowIndex);
			let url = getCallUrl("/numberRule/last?number=" + item.number);
			call(url, null, function(data) {
				let last = data.last;
				let next = data.next;
				AUIGrid.updateRowsById(myGridID, {
					rowId : item.rowId,
					last : last,
					number : item.number + next
				});
			}, "GET");
		}
	}

	$(function() {
		createAUIGrid(columns);

		$("#closeBtn").click(function() {
			self.close();
		})

		$("#addRowBtn").click(function() {
			let item = new Object();
			item.businessSector = "K";
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
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/numberRule/create");
			params.addRows = addRows;
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
</script>
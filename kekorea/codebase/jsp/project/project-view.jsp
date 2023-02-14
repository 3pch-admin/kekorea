<%@page import="org.json.JSONArray"%>
<%@page import="e3ps.project.template.beans.TemplateViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td class="left">
			<input type="button" value="추가" id="addRowBtn" title="추가" class="greenBtn">
			<input type="button" value="삭제" id="deleteRowBtn" title="삭제" class="redBtn">
			<input type="button" value="위로" id="upRowBtn" title="위로" class="orangeBtn">
			<input type="button" value="아래로" id="downRowBtn" title="아래로" class="redBtn">
			<input type="button" value="저장" id="saveBtn" title="저장" class="blueBtn">
		</td>
		<td class="right">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="50%">
		<col width="30">
		<col width="49%">
	</colgroup>
	<tr>
		<td valign="top">
			<div id="grid_wrap" style="height: 920px; border-top: 1px solid #3180c3;"></div>
		</td>
		<td>&nbsp;</td>
		<td valign="top">
			<iframe src="/Windchill/plm/project/projectView?oid=<%=oid %>" style="height: 910px; width: 100%;"></iframe>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "프로젝트 명",
		dataType : "string",
		style : "left indent10",
		width : 300,
	}, {
		dataField : "taskType",
		headerText : "태스크타입",
		dataType : "string",
		width : 100,
	}, {
		dataField : "description",
		headerText : "설명",
		dataType : "string",
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	}, {
		dataField : "isNew",
		headerText : "isNew",
		dataType : "boolean",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : false,
			displayTreeOpen : true,
			treeColumnIndex : 0,
			enableDrag : true,
			enableDragByCellDrag : true,
			enableDrop : true,
			enableUndoRedo : true,
			editable : true,
			// 			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			// 			showRowCheckColumn : true,
			treeLevelIndent : 15,
			enableSorting : false,
			// 			rowCheckToRadio : true,
			showStateColumn : true,
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
		AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
	}

	function auiCellEditBegin(event) {
		let item = event.item;
		let oid = item.oid;
		let rowIndex = event.rowIndex;
		if (rowIndex === 0) {
			return false;
		}
		return true;
	}

	function auiAddRowHandler(event) {
		let selected = AUIGrid.getSelectedIndex(myGridID);
		if (selected.length <= 0) {
			return;
		}
		let rowIndex = selected[0];
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "name");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex); // ISBN 으로 선택자 이동
		AUIGrid.openInputer(myGridID);
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/project/load?oid=<%=oid%>");
		AUIGrid.showAjaxLoader(myGridID);
		openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			AUIGrid.setGridData(myGridID, data.list);
			closeLayer();
		}, "GET");
	}

	$(function() {
		createAUIGrid(columns);

		$("#saveBtn").click(function() {
			let data = AUIGrid.getTreeGridData(myGridID);
			let json = btoa(unescape(encodeURIComponent(JSON.stringify(data))));
			let removeRows = AUIGrid.getRemovedItems(myGridID);
			let params = new Object();
			params.json = json;
			params.removeRows = removeRows;
			let url = getCallUrl("/project/save");
			openLayer();
			console.log(params);
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					loadGridData();
				} else {
					closeLayer();
				}
			}, "POST");
		})

		$("#addRowBtn").click(function() {
			let selectedItems = AUIGrid.getSelectedItems(myGridID);
			if (selectedItems.length == 0) {
				return;
			}

			let selItem = selectedItems[0].item;
			let parentRowId = null;
			let item = new Object();
			item.isNew = true;
			AUIGrid.addTreeRow(myGridID, item, parentRowId, "last");
		})

		$("#upRowBtn").click(function() {
			let selectedItems = AUIGrid.getSelectedItems(myGridID);
			if (selectedItems.length <= 0) {
				return;
			}
			let rowIndex = selectedItems[0].rowIndex - 1;
			if (rowIndex == 0) {
				AUIGrid.showToastMessage(myGridID, rowIndex + 1, 0, "템플릿 레벨로 이동을 할 수 없습니다.");
				return false;
			}
			AUIGrid.moveRowsToUp(myGridID);
		})

		$("#downRowBtn").click(function() {
			let selectedItems = AUIGrid.getSelectedItems(myGridID);
			if (selectedItems.length <= 0) {
				return;
			}
			let rowIndex = selectedItems[0].rowIndex;
			if (rowIndex == 0) {
				AUIGrid.showToastMessage(myGridID, rowIndex, 0, "템플릿은 레벨 변경이 불가능 합니다.");
				return false;
			}
			AUIGrid.moveRowsToDown(myGridID);
		})

		$("#closeBtn").click(function() {
			self.close();
		})
	})

	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>
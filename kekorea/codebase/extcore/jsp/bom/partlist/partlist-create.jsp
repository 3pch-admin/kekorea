<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String engType = (String) request.getAttribute("engType");
JSONArray list = (JSONArray) request.getAttribute("list");
String poid = (String) request.getAttribute("poid");
String toid = (String) request.getAttribute("toid");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="toid" id="toid" value="<%=toid%>">
<input type="hidden" name="poid" id="poid" value="<%=poid%>">
<style type="text/css">
.ng {
	background-color: yellow;
	color: red;
	font-weight: bold;
}
</style>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				수배표 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
		<li>
			<a href="#tabs-2">수배표</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="create-table">
			<colgroup>
				<col width="130">
				<col width="400">
				<col width="130">
				<col width="400">
				<col width="130">
				<col width="400">
			</colgroup>
			<tr>
				<th class="req lb">수배표 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-400">
				</td>
				<th>설계구분</th>
				<td class="indent5">
					<select name="engType" id="engType" class="width-200">
						<option value="">선택</option>
						<option value="기계">기계</option>
						<option value="전기">전기</option>
					</select>
				</td>
				<th>진행율</th>
				<td class="indent5">
					<input type="number" name="progress" id="progress" class="width-300" value="0">
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
				<th class="req lb">내용</th>
				<td class="indent5" colspan="5">
					<textarea name="description" id="description" rows="8"></textarea>
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
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="행 추가(이전)" title="행 추가(이전)" class="blue" onclick="addBeforeRow();">
					<input type="button" value="행 추가(이후)" title="행 추가(이후)" class="orange" onclick="addAfterRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 780px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "check",
				headerText : "체크",
				dataType : "string",
				width : 80,
// 				editable : false,
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					if (value === "NG") {
						return "ng";
					}
					return "";
				},
			}, {
				dataField : "lotNo",
				headerText : "LOT_NO",
				dataType : "numeric",
				width : 80,
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true,
					maxlength : 3,
				},
			}, {
				dataField : "unitName",
				headerText : "UNIT NAME",
				dataType : "string",
				width : 120,
// 				editable : false,
			}, {
				dataField : "partNo",
				headerText : "부품번호",
				dataType : "string",
				width : 130,
				editRenderer : {
					type : "InputEditRenderer",
					regExp : "^[a-zA-Z0-9]+$",
					autoUpperCase : true,
					maxlength : 10,
				},
			}, {
				dataField : "partName",
				headerText : "부품명",
				dataType : "string",
				width : 200,
// 				editable : false,
			}, {
				dataField : "standard",
				headerText : "규격",
				dataType : "string",
				width : 250,
// 				editable : false,
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
					onlyNumeric : true,
				},
			}, {
				dataField : "unit",
				headerText : "단위",
				dataType : "string",
				width : 80,
// 				editable : false,
			}, {
				dataField : "price",
				headerText : "단가",
				dataType : "numeric",
				width : 120,
// 				editable : false,
			}, {
				dataField : "currency",
				headerText : "화폐",
				dataType : "string",
				width : 60,
// 				editable : false,
			}, {
				dataField : "won",
				headerText : "원화금액",
				dataType : "numeric",
				width : 120,
// 				editable : false,
			}, {
				dataField : "partListDate",
				headerText : "수배일자",
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 100,
// 				editable : false
			}, {
				dataField : "exchangeRate",
				headerText : "환율",
				dataType : "numeric",
				width : 80,
				formatString : "#,##0.0000",
// 				editable : false,
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
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					showDragKnobColumn : true,
					enableDrag : true,
					enableMultipleDrag : true,
					enableDrop : true,
					enableSorting : false,
					$compaEventOnPaste : true,
					editable : true,
					enableRowCheckShiftKey : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					contextMenuItems : [ {
						label : "선택된 행 이전 추가",
						callback : contextItemHandler
					}, {
						label : "선택된 행 이후 추가",
						callback : contextItemHandler
					}, {
						label : "_$line"
					}, {
						label : "선택된 행 삭제",
						callback : contextItemHandler
					} ],
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				readyHandler();
// 				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
				AUIGrid.bind(myGridID, "beforeRemoveRow", auiBeforeRemoveRow);
			}

			function auiCellEditEndHandler(event) {
				const rowIndex = event.rowIndex;
				const dataField = event.dataField;
				const item = event.item;
				const partNo = item.partNo;
				const lotNo = item.lotNo;
				const quantity = item.quantity;
				if (dataField === "lotNo") {
					const url = getCallUrl("/erp/getUnitName?lotNo=" + lotNo);
					call(url, null, function(data) {
						if (data.result) {
							const newItem = {
								unitName : data.unitName,
								partListDate : new Date(),
								sort : rowIndex
							};
							AUIGrid.updateRow(myGridID, newItem, rowIndex);
						}
					}, "GET");
				}

				if (dataField === "partNo") {
					const url = getCallUrl("/erp/validate?partNo=" + partNo);
					call(url, null, function(data) {
						if (data.result) {
							const newItem = {
								check : data.check,
								partListDate : new Date(),
								sort : rowIndex
							};
							AUIGrid.updateRow(myGridID, newItem, rowIndex);
						}
					}, "GET");
				}

				if (dataField === "quantity") {
					// 값이 있을 경우만
					const url = getCallUrl("/erp/getErpItemByPartNoAndQuantity?partNo=" + partNo + "&quantity=" + quantity);
					call(url, null, function(data) {
						if (data.result) {
							const newItem = {
								unit : data.unit,
								exchangeRate : data.exchangeRate,
								price : data.price,
								maker : data.maker,
								customer : data.customer,
								currency : data.currency,
								won : data.won,
								partName : data.partName,
								standard : data.standard,
								partListDate : new Date(),
								sort : rowIndex,
							};
							AUIGrid.updateRow(myGridID, newItem, rowIndex);
						}
					}, "GET");
				}
			}

			function contextItemHandler(event) {
				const item = {
					partListDate : new Date(),
					lotNo : 0,
					quantity : 0,
					price : 0,
					exchangeRate : 0,
					won : 0
				}
				switch (event.contextIndex) {
				case 0:
					AUIGrid.addRow(myGridID, item, "selectionUp");
					break;
				case 1:
					AUIGrid.addRow(myGridID, item, "selectionDown");
					break;
				case 3:
					const selectedItems = AUIGrid.getSelectedItems(myGridID);
					const rows = AUIGrid.getRowCount(myGridID);
					if (rows === 1) {
						alert("최 소 하나의 행이 존재해야합니다.");
						return false;
					}
					for (let i = selectedItems.length - 1; i >= 0; i--) {
						const rowIndex = selectedItems[i].rowIndex;
						AUIGrid.removeRow(myGridID, rowIndex);
					}
					break;
				}
			}

			function auiBeforeRemoveRow(event) {
				const rows = AUIGrid.getRowCount(myGridID);
				if (rows === 1) {
					alert("최소 하나의 행이 존재해야합니다.");
					return false;
				}
				return true;
			}

			function deleteRow() {
				const checked = AUIGrid.getCheckedRowItems(myGridID);
				const rows = AUIGrid.getRowCount(myGridID);
				if (rows === 1) {
					alert("최 소 하나의 행이 존재해야합니다.");
					return false;
				}

				if (checked.length === 0) {
					alert("삭제할 행을 선택하세요.");
					return false;
				}
				for (let i = checked.length - 1; i >= 0; i--) {
					const rowIndex = checked[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			};

			function readyHandler() {
				const item = {
					partListDate : new Date(),
					lotNo : 0,
					quantity : 0,
					price : 0,
					exchangeRate : 0,
					won : 0
				}
				AUIGrid.addRow(myGridID, item, "last");
			}

			function addBeforeRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("추가하려는 행의 기준이 되는 행을 선택하세요.");
					return false;
				}
				if (checkedItems.length > 1) {
					alert("하나의 행만 선택하세요.");
					return false;
				}
				const rowIndex = checkedItems[0].rowIndex;
				const item = {
					partListDate : new Date(),
					lotNo : 0,
					quantity : 0,
					price : 0,
					exchangeRate : 0,
					won : 0
				}
				AUIGrid.addRow(myGridID, item, rowIndex);
			}

			function addAfterRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("추가하려는 행의 기준이 되는 행을 선택하세요.");
					return false;
				}
				if (checkedItems.length > 1) {
					alert("하나의 행만 선택하세요.");
					return false;
				}
				const rowIndex = checkedItems[0].rowIndex;
				const item = {
					partListDate : new Date(),
					lotNo : 0,
					quantity : 0,
					price : 0,
					exchangeRate : 0,
					won : 0
				}
				AUIGrid.addRow(myGridID, item, rowIndex + 1);
			}
		</script>
	</div>
</div>

<script type="text/javascript">
	function create() {
		const params = new Object();
		const url = getCallUrl("/partlist/create");
		const name = document.getElementById("name");
		const addRows = AUIGrid.getGridData(myGridID);
		const addRows9 = AUIGrid.getGridData(myGridID9);
		const addRows8 = AUIGrid.getGridData(myGridID8);
		const engType = document.getElementById("engType").value;
		const description = document.getElementById("description");
		const progress = document.getElementById("progress").value;
		
		if (isNull(name.value)) {
			alert("수배표 제목을 입력하세요.");
			name.focus();
			return false;
		}

		if (isNull(engType)) {
			alert("설계구분을 선택하세요.");
			return false;
		}

		if (isNull(description.value)) {
			alert("내용을 입력하세요.");
			description.focus();
			return false;
		}

		if (addRows9.length === 0) {
			alert("최소 하나 이상의 작번을 추가하세요.");
			insert9();
			return false;
		}

		for (let i = 0; i < addRows.length; i++) {
			const item = addRows[i];
			const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);
			
			if (isNull(item.partNo)) {
				alert("부품번호를 입력하세요.\n수배표 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 3, "부품번호를 입력하세요.");
				return false;
			}

			if (item.check === "NG") {
				alert("ERP에 등록된 부품번호가 아닙니다.\n수배표 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 0, "ERP에 등록된 부품번호가 아닙니다.");
				return false;
			}

			if (isNull(item.lotNo)) {
				alert("LOT NO를 입력하세요.\n수배표 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 1, "LOT NO를 입력하세요.");
				return false;
			}

			if (item.lotNo === 0) {
				alert("LOT NO의 값은 0이 될 수 없습니다.\n수배표 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 1, "LOT NO의 값은 0이 될 수 없습니다.");
				return false;
			}

			if (isNull(item.quantity)) {
				alert("수량을 입력하세요.\n수배표 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 8, "수량을 입력하세요.");
				return false;
			}

			if (item.quantity === 0) {
				alert("수량의 값은 0이 될 수 없습니다.\n수배표 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 8, "수량의 값은 0이 될 수 없습니다.");
				return false;
			}
		}

		if (addRows8.length === 0) {
			alert("결재선을 지정하세요.");
			_register();
			return false;
		}

		addRows9.sort(function(a, b) {
			return a.sort - b.sort;
		});

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}
		params.addRows = addRows;
		params.addRows9 = addRows9;
		params.name = name.value;
		params.engType = engType;
		params.description = description.value;
		params.progress = Number(progress);
		params.secondarys = toArray("secondarys");
		toRegister(params, addRows8);
		console.log(params);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				<%
					if(!StringUtils.isNull(toid)) {
				%>
				opener._reload();
				<%
					} else {
				%>
				opener.loadGridData();
				<%
					}
				%>
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
		document.getElementById("name").focus();
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated9 = AUIGrid.isCreated(myGridID9);
					if (isCreated9) {
						AUIGrid.resize(myGridID9);
					} else {
						createAUIGrid9(columns9);
					}
					const isCreated8 = AUIGrid.isCreated(myGridID8);
					if (isCreated8) {
						AUIGrid.resize(myGridID8);
					} else {
						createAUIGrid8(columns8);
					}
					selectbox("engType");
					$("#engType").bindSelectDisabled(true);
					break;
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				}
			}
		});
		selectbox("engType");
		$("#engType").bindSelectSetValue("<%=engType%>");
		$("#engType").bindSelectDisabled(true);
		createAUIGrid9(columns9);
		createAUIGrid8(columns8);
		createAUIGrid(columns);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		AUIGrid.resize(myGridID);
		AUIGrid.addRow(myGridID9, <%=list%>);
		AUIGrid.bind(myGridID9, "beforeRemoveRow", auiBeforeRemoveRow);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		AUIGrid.resize(myGridID);
	});
</script>
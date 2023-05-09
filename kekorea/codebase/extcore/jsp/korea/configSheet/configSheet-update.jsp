<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray categorys = (JSONArray) request.getAttribute("categorys");
JSONArray baseData = (JSONArray) request.getAttribute("baseData");
String oid = (String) request.getAttribute("oid");
String mode = (String) request.getAttribute("mode");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<style type="text/css">
.row1 {
	background-color: #99CCFF;
}

.row2 {
	background-color: #FFCCFF;
}

.row3 {
	background-color: #CCFFCC;
}

.row4 {
	background-color: #FFFFCC;
}

.row5 {
	background-color: #FFCC99;
}

.row6 {
	background-color: #CCCCFF;
}

.row7 {
	background-color: #99FF66;
}

.row8 {
	background-color: #CC99FF;
}

.row9 {
	background-color: #66CCFF;
}

.row10 {
	background-color: #CCFFCC;
}

.row11 {
	background-color: #FFCCFF;
}

.row12 {
	background-color: #FFFFCC;
}
</style>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CONFIG SHEET 등록
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
			<a href="#tabs-2">CONFIG SHEET</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="create-table">
			<colgroup>
				<col width="200">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">CONFIG SHEET 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="AXInput width-700">
				</td>
			</tr>
			<tr>
				<th class="req lb">KEK 작번</th>
				<td>
					<jsp:include page="/extcore/jsp/common/project-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="false" name="multi" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/jsp/common/approval-register.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="불러오기" title="불러오기" class="blue" onclick="load();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
	</div>
</div>

<script type="text/javascript">
	let myGridID;
	const categorys =
<%=categorys%>
	let itemListMap = {};
	let specListMap = {};
	const columns = [ {
		dataField : "category_code",
		headerText : "CATEGORY",
		dataType : "string",
		style : "aui-left",
		width : 250,
		cellMerge : true,
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			for (let i = 0, len = categorys.length; i < len; i++) {
				if (categorys[i]["key"] == value) {
					retStr = categorys[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "item_code",
		headerText : "ITEM",
		dataType : "string",
		width : 350,
		cellMerge : true,
		style : "aui-left",
		mergeRef : "category_code",
		mergePolicy : "restrict",
		editable : false,
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
			let retStr = "";
			const param = item.category_code;
			const dd = itemListMap[param];
			if (dd === undefined) {
				return value;
			}
			for (let i = 0, len = dd.length; i < len; i++) {
				if (dd[i]["key"] == value) {
					retStr = dd[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		},
	}, {
		dataField : "spec",
		headerText : "사양",
		dataType : "string",
		width : 350,
	}, {
		dataField : "note",
		headerText : "NOTE",
		dataType : "string",
	}, {
		dataField : "apply",
		headerText : "APPLY",
		dataType : "string",
		width : 350
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			enableSorting : false,
			enableCellMerge : true,
			showDragKnobColumn : true,
			editable : true,
			enableRowCheckShiftKey : true,
			rowStyleFunction : function(rowIndex, item) {
				const value = item.category_code;
				if (value === "CATEGORY_2") {
					return "row1";
				} else if (value === "CATEGORY_3") {
					return "row2";
				} else if (value === "CATEGORY_4") {
					return "row3";
				} else if (value === "CATEGORY_5") {
					return "row4";
				} else if (value === "CATEGORY_6") {
					return "row5";
				} else if (value === "CATEGORY_7") {
					return "row6";
				} else if (value === "CATEGORY_8" || value === "CATEGORY_9") {
					return "row7";
				} else if (value === "CATEGORY_10") {
					return "row8";
				} else if (value === "CATEGORY_11") {
					return "row9";
				} else if (value === "CATEGORY_12") {
					return "row4";
				} else if (value === "CATEGORY_13") {
					return "row10";
				} else if (value === "CATEGORY_14") {
					return "row11";
				} else if (value === "CATEGORY_15") {
					return "row12";
				}
				return "";
			}
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
		AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
		readyHandler();
		auiReadyHandler();
	}

	function load() {
		const url = getCallUrl("/configSheet/copy?method=copy&multi=false");
		popup(url, 1500, 700);
	}

	function copy(data, callBack) {
		const oid = data.item.oid;
		const params = new Object();
		const url = getCallUrl("/configSheet/copy");
		params.oid = oid;
		openLayer();
		call(url, params, function(data) {
			if (data.result && data.list.length > 0) {
				AUIGrid.clearGridData(myGridID);
				AUIGrid.addRow(myGridID, data.list);
				callBack(true, "");
			} else {
				callBack(true, data.msg);
			}
			closeLayer();
		})
	}

	function auiCellEditBegin(event) {
		const dataField = event.dataField;
		if (dataField === "category_code") {
			return false;
		}
		return true;
	}

	function contextItemHandler(event) {
		const item = new Object();
		switch (event.contextIndex) {
		case 0:
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

	function auiReadyHandler(event) {
		const item = AUIGrid.getGridData(myGridID);
		for (let i = 0; i < item.length; i++) {
			if (itemListMap.length === undefined) {
				const categoryCode = item[i].category_code;
				const url = getCallUrl("/configSheetCode/getChildrens?parentCode=" + categoryCode + "&codeType=CATEGORY");
				call(url, null, function(data) {
					itemListMap[categoryCode] = data.list;
				}, "GET");
			}
			// 			if (specListMap.length === undefined) {
			// 				const itemCode = item[i].item_code;
			// 				if (itemCode !== "") {
			// 					const url = getCallUrl("/configSheetCode/getChildrens?parentCode=" + itemCode + "&codeType=CATEGORY_ITEM");
			// 					call(url, null, function(data) {
			// 						specListMap[itemCode] = data.list;
			// 					}, "GET");
			// 				}
			// 			}
		}
	}

	function readyHandler() {
		const data =
<%=baseData%>
	AUIGrid.addRow(myGridID, data);
	}

	function auiCellEditEndHandler(event) {
		const dataField = event.dataField;
		const item = event.item;
		const rowIndex = event.rowIndex;
		if (dataField === "category_code") {
			const categoryCode = item.category_code;
			const url = getCallUrl("/commonCode/getChildrens?parentCode=" + categoryCode + "&codeType=CATEGORY");
			call(url, null, function(data) {
				itemListMap[categoryCode] = data.list;
			}, "GET");
		}

		// 		if (dataField === "item_code") {
		// 			const itemCode = item.item_code;
		// 			const url = getCallUrl("/commonCode/getChildrens?parentCode=" + itemCode + "&codeType=CATEGORY_ITEM");
		// 			call(url, null, function(data) {
		// 				specListMap[itemCode] = data.list;
		// 			}, "GET");
		// 		}
	}

	function deleteRow() {
		const checked = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	};

	function create() {
		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/configSheet/create");
		const params = new Object();
		const addRows = AUIGrid.getGridData(myGridID);
		const addRows9 = AUIGrid.getAddedRowItems(myGridID9);
		const addRows8 = AUIGrid.getAddedRowItems(myGridID8);

		addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});

		params.name = document.getElementById("name").value;
		params.description = document.getElementById("description").value;
		params.addRows = addRows;
		params.addRows9 = addRows9;
		params.secondarys = toArray("secondarys");
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
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("name").focus();
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				const tabId = ui.newPanel.prop("id");
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
					break;
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
						openLayer();
						setTimeout(function() {
							AUIGrid.refresh(myGridID);
							closeLayer();
						}, 100);
					}
					break;
				}
			}
		});
		createAUIGrid9(columns9);
		createAUIGrid8(columns8);
		createAUIGrid(columns);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		AUIGrid.resize(myGridID);
	});
</script>